package dep.mgmt.service;

import static dep.mgmt.util.ProcessUtils.TASK_QUEUES;

import dep.mgmt.config.CacheConfig;
import dep.mgmt.model.AppData;
import dep.mgmt.model.AppDataLatestVersions;
import dep.mgmt.model.AppDataRepository;
import dep.mgmt.model.AppDataScriptFile;
import dep.mgmt.model.ProcessSummaries;
import dep.mgmt.model.RequestMetadata;
import dep.mgmt.model.TaskQueues;
import dep.mgmt.model.entity.ProcessSummaryEntity;
import dep.mgmt.model.enums.RequestParams;
import dep.mgmt.update.GradleProjectUpdate;
import dep.mgmt.update.NodeProjectUpdate;
import dep.mgmt.update.PythonProjectUpdate;
import dep.mgmt.update.UpdateBranchDelete;
import dep.mgmt.update.UpdateDependencies;
import dep.mgmt.update.UpdateGradleSpotless;
import dep.mgmt.update.UpdateNpmSnapshots;
import dep.mgmt.update.UpdateRepoResetPull;
import dep.mgmt.util.AppDataUtils;
import dep.mgmt.util.ConstantUtils;
import dep.mgmt.util.ConvertUtils;
import dep.mgmt.util.LogCaptureUtils;
import dep.mgmt.util.ProcessSummaryEmailUtils;
import dep.mgmt.util.ProcessUtils;
import dep.mgmt.util.ScriptUtils;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateRepoService {
  private static final Logger log = LoggerFactory.getLogger(UpdateRepoService.class);

  private final GradleDependencyVersionService gradleDependencyVersionService;
  private final GradlePluginVersionService gradlePluginVersionService;
  private final NodeDependencyVersionService nodeDependencyVersionService;
  private final PythonPackageVersionService pythonPackageVersionService;
  private final ExcludedRepoService excludedRepoService;
  private final GithubService githubService;
  private final EmailService emailService;
  private final LogEntryService logEntryService;
  private final ProcessSummaryService processSummaryService;
  private final LatestVersionService latestVersionService;

  public UpdateRepoService() {
    this.gradleDependencyVersionService = new GradleDependencyVersionService();
    this.gradlePluginVersionService = new GradlePluginVersionService();
    this.nodeDependencyVersionService = new NodeDependencyVersionService();
    this.pythonPackageVersionService = new PythonPackageVersionService();
    this.excludedRepoService = new ExcludedRepoService();
    this.githubService = new GithubService();
    this.emailService = new EmailService();
    this.logEntryService = new LogEntryService();
    this.processSummaryService = new ProcessSummaryService();
    this.latestVersionService = new LatestVersionService();
  }

  public Map<String, List<ProcessSummaries.ProcessSummary.ProcessTask>> getAllProcessTaskQueues() {
    final List<ProcessSummaries.ProcessSummary.ProcessTask> processTasks =
        ProcessUtils.getProcessedTasks().values().stream()
            .sorted(Comparator.comparing(ProcessSummaries.ProcessSummary.ProcessTask::getAdded))
            .toList();
    return Map.of("processTasks", processTasks);
  }

  public void executeTaskQueues() {
    log.info("Execute Task Queues...");
    if (!TASK_QUEUES.isProcessing()) {
      TASK_QUEUES.processQueues();
    }
  }

  public void clearTaskQueues() {
    log.info("Clear Task Queues...");
    TASK_QUEUES.clearQueue();
    ProcessUtils.resetProcessedRepositoriesAndSummary();
  }

  public void recreateLocalCaches(final boolean isExecuteTaskQueues) {
    resetCaches();
    setCaches();
    if (isExecuteTaskQueues) {
      executeTaskQueues();
    }
  }

  public void scheduledUpdate(final RequestMetadata requestMetadata) {
    updateRepos(requestMetadata);
  }

  public void scheduledMongoUpdate() {
    CompletableFuture.runAsync(gradleDependencyVersionService::updateGradleDependencies);
    CompletableFuture.runAsync(gradlePluginVersionService::updateGradlePlugins);
    CompletableFuture.runAsync(nodeDependencyVersionService::updateNodeDependencies);
    CompletableFuture.runAsync(pythonPackageVersionService::updatePythonPackages);
  }

  public void scheduledCleanup(final LocalDateTime cleanupBeforeDate) {
    CompletableFuture.runAsync(() -> processSummaryService.scheduledCleanup(cleanupBeforeDate));
    CompletableFuture.runAsync(() -> logEntryService.scheduledCleanup(cleanupBeforeDate));
    CompletableFuture.runAsync(() -> latestVersionService.scheduledCleanup(cleanupBeforeDate));
  }

  public void updateRepos(final RequestMetadata requestMetadata) {
    if (requestMetadata.getUpdateType().equals(RequestParams.UpdateType.ALL)) {
      LogCaptureUtils.start(requestMetadata.getIncludeDebugLogs());
      log.info("Started Log Capture...");
    }

    log.info("Update Repos: [{}]", requestMetadata);
    updateInit(requestMetadata);

    boolean isPrCreateRequired = false;
    boolean isPrMergeRequired = false;
    boolean isPrCreateMergeCheckRequired = false;

    switch (requestMetadata.getUpdateType()) {
      case PULL, RESET -> log.info("Pull/Reset covered in updateInit...");
      case DELETE -> executeGithubBranchDelete(requestMetadata, Boolean.FALSE);
      case SNAPSHOT ->
          executeNpmSnapshotsUpdate(requestMetadata.getBranchDate(), requestMetadata.getRepoName());
      case SPOTLESS ->
          executeGradleSpotlessUpdate(
              requestMetadata.getBranchDate(), requestMetadata.getRepoName());
      case PULL_REQ -> isPrCreateRequired = true;
      case MERGE -> isPrMergeRequired = true;
      case ALL, GRADLE, NODE, PYTHON -> {
        isPrCreateRequired = true;
        isPrMergeRequired = true;
        isPrCreateMergeCheckRequired = true;
        executeUpdateDependenciesInitExit(requestMetadata, Boolean.TRUE);
        executeUpdateDependencies(requestMetadata);
        executeUpdateDependenciesExec(requestMetadata);
      }
      default ->
          throw new IllegalArgumentException(
              String.format("Invalid Update Type: [ '%s' ]", requestMetadata.getUpdateType()));
    }

    log.debug(
        "Update Repo: isPrCreateRequired={}, isPrMergeRequired={}, isPrCreateMergeCheckRequired={}",
        isPrCreateRequired,
        isPrMergeRequired,
        isPrCreateMergeCheckRequired);

    if (isPrCreateRequired) {
      executeUpdateCreatePullRequests(requestMetadata, isPrCreateMergeCheckRequired);
    }

    if (isPrMergeRequired) {
      executeUpdateMergePullRequests(requestMetadata, isPrCreateMergeCheckRequired);
      executeGithubBranchDelete(requestMetadata, Boolean.TRUE);
    }

    updateExit(requestMetadata);
    executeTaskQueues();
  }

  private void resetCaches() {
    addTaskToQueue(
        ConstantUtils.QUEUE_RESET_LOCAL,
        ConstantUtils.TASK_RESET_LOCAL,
        CacheConfig::resetAppData,
        ConstantUtils.TASK_DELAY_ZERO);
    addTaskToQueue(
        ConstantUtils.QUEUE_RESET_LOCAL,
        ConstantUtils.TASK_RESET_GRADLE_DEPENDENCIES_LOCAL,
        CacheConfig::resetGradleDependenciesMap,
        ConstantUtils.TASK_DELAY_ZERO);
    addTaskToQueue(
        ConstantUtils.QUEUE_RESET_LOCAL,
        ConstantUtils.TASK_RESET_GRADLE_PLUGINS_LOCAL,
        CacheConfig::resetGradlePluginsMap,
        ConstantUtils.TASK_DELAY_ZERO);
    addTaskToQueue(
        ConstantUtils.QUEUE_RESET_LOCAL,
        ConstantUtils.TASK_RESET_NODE_DEPENDENCIES_LOCAL,
        CacheConfig::resetNodeDependenciesMap,
        ConstantUtils.TASK_DELAY_ZERO);
    addTaskToQueue(
        ConstantUtils.QUEUE_RESET_LOCAL,
        ConstantUtils.TASK_RESET_PYTHON_PACKAGES_LOCAL,
        CacheConfig::resetPythonPackagesMap,
        ConstantUtils.TASK_DELAY_ZERO);
    addTaskToQueue(
        ConstantUtils.QUEUE_RESET_LOCAL,
        ConstantUtils.TASK_RESET_EXCLUDED_REPOS_LOCAL,
        CacheConfig::resetExcludedReposMap,
        ConstantUtils.TASK_DELAY_ZERO);
  }

  private void setCaches() {
    addTaskToQueue(
        ConstantUtils.QUEUE_SET_LOCAL,
        ConstantUtils.TASK_SET_LOCAL,
        AppDataUtils::setAppData,
        ConstantUtils.TASK_DELAY_DEFAULT);
    addTaskToQueue(
        ConstantUtils.QUEUE_SET_LOCAL,
        ConstantUtils.TASK_SET_GRADLE_DEPENDENCIES_LOCAL,
        gradleDependencyVersionService::getGradleDependenciesMap,
        ConstantUtils.TASK_DELAY_ZERO);
    addTaskToQueue(
        ConstantUtils.QUEUE_SET_LOCAL,
        ConstantUtils.TASK_SET_GRADLE_PLUGINS_LOCAL,
        gradlePluginVersionService::getGradlePluginsMap,
        ConstantUtils.TASK_DELAY_ZERO);
    addTaskToQueue(
        ConstantUtils.QUEUE_SET_LOCAL,
        ConstantUtils.TASK_SET_NODE_DEPENDENCIES_LOCAL,
        nodeDependencyVersionService::getNodeDependenciesMap,
        ConstantUtils.TASK_DELAY_ZERO);
    addTaskToQueue(
        ConstantUtils.QUEUE_SET_LOCAL,
        ConstantUtils.TASK_SET_PYTHON_PACKAGES_LOCAL,
        pythonPackageVersionService::getPythonPackagesMap,
        ConstantUtils.TASK_DELAY_ZERO);
    addTaskToQueue(
        ConstantUtils.QUEUE_SET_LOCAL,
        ConstantUtils.TASK_SET_EXCLUDED_REPOS_LOCAL,
        excludedRepoService::getExcludedReposMap,
        ConstantUtils.TASK_DELAY_ZERO);
  }

  private void recreateScriptFiles() {
    addTaskToQueue(
        ConstantUtils.QUEUE_RECREATE_FILES,
        ConstantUtils.TASK_DELETE_SCRIPT_FILES,
        ScriptUtils::deleteTempScriptFiles,
        ConstantUtils.TASK_DELAY_ZERO);
    addTaskToQueue(
        ConstantUtils.QUEUE_RECREATE_FILES,
        ConstantUtils.TASK_CREATE_SCRIPT_FILES,
        ScriptUtils::createTempScriptFiles,
        ConstantUtils.TASK_DELAY_DEFAULT);
  }

  private void updateInit(final RequestMetadata requestMetadata) {
    // this needs to be executed before adding to tasks
    ProcessUtils.resetProcessedRepositoriesAndSummary();

    if (requestMetadata.getRecreateCaches()) {
      recreateLocalCaches(Boolean.FALSE);
    }

    if (requestMetadata.getRecreateScriptFiles()
        || ScriptUtils.isScriptFilesMissingInFileSystem()) {
      recreateScriptFiles();
    }

    if (requestMetadata.getGithubResetRequired() || requestMetadata.getIsGithubPullRequired()) {
      executeUpdateGithubResetPull(
          requestMetadata.getGithubResetRequired(),
          requestMetadata.getIsGithubPullRequired(),
          requestMetadata.getRepoName(),
          (requestMetadata.getUpdateType().equals(RequestParams.UpdateType.RESET)
              || requestMetadata.getUpdateType().equals(RequestParams.UpdateType.PULL)));

      if (requestMetadata.getRecreateCaches()) {
        recreateLocalCaches(Boolean.FALSE);
      }
    }

    getCheckedUpdatedInPastDayTask();
  }

  private void updateExit(final RequestMetadata requestMetadata) {
    if (ConstantUtils.RATE_LIMIT_UPDATE_TYPES_LIST.contains(requestMetadata.getUpdateType())) {
      checkGithubRateLimits();
      executeUpdateDependenciesInitExit(requestMetadata, Boolean.FALSE);
    }
    if (requestMetadata.getProcessSummaryRequired()) {
      addTaskToQueue(
          ConstantUtils.QUEUE_PROCESS_SUMMARY_REQUIRED,
          ConstantUtils.TASK_PROCESS_SUMMARY_REQUIRED,
          () -> makeProcessSummary(requestMetadata),
          ConstantUtils.TASK_DELAY_DEFAULT);
    }
    resetProcessedSummariesTask();
    saveStopLogCapture();
  }

  private void executeNpmSnapshotsUpdate(final LocalDate branchDate, final String repoName) {
    final AppData appData = AppDataUtils.getAppData();
    final String branchName = String.format(ConstantUtils.BRANCH_UPDATE_DEPENDENCIES, branchDate);

    final AppDataScriptFile scriptFile = getScriptFile(ConstantUtils.SCRIPT_SNAPSHOT);

    List<AppDataRepository> repositories =
        appData.getRepositories().stream()
            .filter(repository -> repository.getType().equals(RequestParams.UpdateType.NODE))
            .filter(
                repository -> {
                  if (CommonUtilities.isEmpty(repoName)) {
                    return true;
                  } else {
                    return repository.getRepoName().equals(repoName);
                  }
                })
            .toList();

    addTaskToQueue(
        ConstantUtils.QUEUE_NPM_SNAPSHOTS,
        ConstantUtils.TASK_NPM_SNAPSHOTS,
        () -> new UpdateNpmSnapshots(repositories, scriptFile, branchName).execute(),
        ConstantUtils.TASK_DELAY_ZERO);
  }

  private void executeGradleSpotlessUpdate(final LocalDate branchDate, final String repoName) {
    final AppData appData = AppDataUtils.getAppData();
    final String branchName = String.format(ConstantUtils.BRANCH_UPDATE_DEPENDENCIES, branchDate);

    final AppDataScriptFile scriptFile = getScriptFile(ConstantUtils.SCRIPT_SPOTLESS);

    List<AppDataRepository> repositories =
        appData.getRepositories().stream()
            .filter(repository -> repository.getType().equals(RequestParams.UpdateType.GRADLE))
            .filter(
                repository -> {
                  if (CommonUtilities.isEmpty(repoName)) {
                    return true;
                  } else {
                    return repository.getRepoName().equals(repoName);
                  }
                })
            .toList();

    addTaskToQueue(
        ConstantUtils.QUEUE_GRADLE_SPOTLESS,
        ConstantUtils.TASK_GRADLE_SPOTLESS,
        () -> UpdateGradleSpotless.execute(repositories, scriptFile, branchName),
        ConstantUtils.TASK_DELAY_ZERO);
  }

  private void executeGithubBranchDelete(
      final RequestMetadata requestMetadata, final boolean isCheckMergedPrBeforeDelete) {
    final AppData appData = AppDataUtils.getAppData();
    final String repoName = requestMetadata.getRepoName();
    final boolean isDeleteUpdateDependenciesOnly =
        requestMetadata.getDeleteUpdateDependenciesOnly();

    if (isCheckMergedPrBeforeDelete) {
      final AppDataScriptFile scriptFile = getScriptFile(ConstantUtils.SCRIPT_DELETE_ONE);
      final List<AppDataRepository> repositories = appData.getRepositories();
      for (AppDataRepository repository : repositories) {
        addTaskToQueue(
            ConstantUtils.QUEUE_GITHUB_BRANCH_DELETE,
            String.format(
                ConstantUtils.TASK_GITHUB_BRANCH_DELETE, repository.getRepoName().toUpperCase()),
            () ->
                UpdateBranchDelete.execute(
                    null, repository, scriptFile, Boolean.TRUE, Boolean.TRUE),
            ConstantUtils.TASK_DELAY_ZERO);
      }
    } else if (CommonUtilities.isEmpty(repoName)) {
      final String repoHome = appData.getArgsMap().get(ConstantUtils.ENV_REPO_HOME);
      final AppDataScriptFile scriptFile = getScriptFile(ConstantUtils.SCRIPT_DELETE);

      addTaskToQueue(
          ConstantUtils.QUEUE_GITHUB_BRANCH_DELETE,
          String.format(ConstantUtils.TASK_GITHUB_BRANCH_DELETE, RequestParams.UpdateType.ALL),
          () ->
              UpdateBranchDelete.execute(
                  repoHome, null, scriptFile, isDeleteUpdateDependenciesOnly, Boolean.FALSE),
          ConstantUtils.TASK_DELAY_ZERO);
    } else {
      final AppDataRepository repository = getRepository(repoName);
      final AppDataScriptFile scriptFile = getScriptFile(ConstantUtils.SCRIPT_DELETE_ONE);

      addTaskToQueue(
          ConstantUtils.QUEUE_GITHUB_BRANCH_DELETE,
          String.format(
              ConstantUtils.TASK_GITHUB_BRANCH_DELETE, repository.getRepoName().toUpperCase()),
          () ->
              UpdateBranchDelete.execute(
                  null, repository, scriptFile, isDeleteUpdateDependenciesOnly, Boolean.FALSE),
          ConstantUtils.TASK_DELAY_ZERO);
    }
  }

  private void executeUpdateGithubResetPull(
      final boolean isReset,
      final boolean isPull,
      final String repoName,
      final boolean isRunAsync) {
    final AppData appData = AppDataUtils.getAppData();

    if (CommonUtilities.isEmpty(repoName)) {
      final String repoHome = appData.getArgsMap().get(ConstantUtils.ENV_REPO_HOME);
      final AppDataScriptFile scriptFile = getScriptFile(ConstantUtils.SCRIPT_RESET_PULL);

      addTaskToQueue(
          ConstantUtils.QUEUE_GITHUB_RESET_PULL,
          String.format(ConstantUtils.TASK_GITHUB_RESET_PULL, RequestParams.UpdateType.ALL),
          () ->
              UpdateRepoResetPull.execute(repoHome, null, scriptFile, isReset, isPull, isRunAsync),
          ConstantUtils.TASK_DELAY_ZERO);
    } else {
      final AppDataRepository repository = getRepository(repoName);
      final AppDataScriptFile scriptFile = getScriptFile(ConstantUtils.SCRIPT_RESET_PULL_ONE);

      addTaskToQueue(
          ConstantUtils.QUEUE_GITHUB_RESET_PULL,
          String.format(
              ConstantUtils.TASK_GITHUB_RESET_PULL, repository.getRepoName().toUpperCase()),
          () ->
              UpdateRepoResetPull.execute(
                  null, repository, scriptFile, isReset, isPull, isRunAsync),
          ConstantUtils.TASK_DELAY_ZERO);
    }
  }

  private void executeUpdateDependenciesInitExit(
      final RequestMetadata requestMetadata, final boolean isInit) {
    final AppData appData = AppDataUtils.getAppData();
    final String repoName = requestMetadata.getRepoName();

    final List<AppDataRepository> repositories =
        appData.getRepositories().stream()
            .filter(
                repository ->
                    CommonUtilities.isEmpty(repoName) || repoName.equals(repository.getRepoName()))
            .toList();
    if (!CommonUtilities.isEmpty(repoName) && CommonUtilities.isEmpty(repositories)) {
      throw new IllegalArgumentException("Repo Not Found by Repo Name ['" + repoName + "']");
    }

    final AppDataScriptFile scriptFileInitExit = getScriptFile(ConstantUtils.SCRIPT_UPDATE_INIT);

    repositories.forEach(
        repository -> {
          addTaskToQueue(
              isInit
                  ? ConstantUtils.QUEUE_UPDATE_DEPENDENCIES_INIT
                  : ConstantUtils.QUEUE_UPDATE_DEPENDENCIES_EXIT,
              isInit
                  ? String.format(
                      ConstantUtils.TASK_UPDATE_DEPENDENCIES_INIT,
                      repository.getRepoName().toUpperCase())
                  : String.format(
                      ConstantUtils.TASK_UPDATE_DEPENDENCIES_EXIT,
                      repository.getRepoName().toUpperCase()),
              () -> UpdateDependencies.execute(repository, scriptFileInitExit, null, isInit),
              ConstantUtils.TASK_DELAY_ZERO);
        });
  }

  private void executeUpdateDependenciesExec(final RequestMetadata requestMetadata) {
    final AppData appData = AppDataUtils.getAppData();
    final String repoName = requestMetadata.getRepoName();

    final List<AppDataRepository> repositories =
        appData.getRepositories().stream()
            .filter(
                repository ->
                    CommonUtilities.isEmpty(repoName) || repoName.equals(repository.getRepoName()))
            .toList();
    if (!CommonUtilities.isEmpty(repoName) && CommonUtilities.isEmpty(repositories)) {
      throw new IllegalArgumentException("Repo Not Found by Repo Name ['" + repoName + "']");
    }

    final AppDataScriptFile scriptFileExec = getScriptFile(ConstantUtils.SCRIPT_UPDATE_EXEC);

    repositories.forEach(
        repository -> {
          final String branchName =
              String.format(
                  ConstantUtils.BRANCH_UPDATE_DEPENDENCIES, requestMetadata.getBranchDate());
          addTaskToQueue(
              ConstantUtils.QUEUE_UPDATE_DEPENDENCIES_EXEC,
              String.format(
                  ConstantUtils.TASK_UPDATE_DEPENDENCIES_EXEC,
                  repository.getRepoName().toUpperCase()),
              () ->
                  UpdateDependencies.execute(repository, scriptFileExec, branchName, Boolean.FALSE),
              ConstantUtils.TASK_DELAY_ZERO);
        });
  }

  private void executeUpdateDependencies(final RequestMetadata requestMetadata) {
    final AppData appData = AppDataUtils.getAppData();
    final String repoName = requestMetadata.getRepoName();

    final List<AppDataRepository> repositories =
        appData.getRepositories().stream()
            .filter(
                repository ->
                    CommonUtilities.isEmpty(repoName) || repoName.equals(repository.getRepoName()))
            .toList();
    if (!CommonUtilities.isEmpty(repoName) && CommonUtilities.isEmpty(repositories)) {
      throw new IllegalArgumentException("Repo Not Found by Repo Name ['" + repoName + "']");
    }

    if (requestMetadata.getUpdateType().equals(RequestParams.UpdateType.ALL)
        || requestMetadata.getUpdateType().equals(RequestParams.UpdateType.GRADLE)) {
      final List<AppDataRepository> gradleRepositories =
          repositories.stream()
              .filter(repository -> repository.getType().equals(RequestParams.UpdateType.GRADLE))
              .toList();
      gradleRepositories.forEach(
          gradleRepository ->
              executeUpdateGradleProjects(appData.getLatestVersions(), gradleRepository));
    }

    if (requestMetadata.getUpdateType().equals(RequestParams.UpdateType.ALL)
        || requestMetadata.getUpdateType().equals(RequestParams.UpdateType.NODE)) {
      final List<AppDataRepository> nodeRepositories =
          repositories.stream()
              .filter(repository -> repository.getType().equals(RequestParams.UpdateType.NODE))
              .toList();
      nodeRepositories.forEach(
          nodeRepository -> executeUpdateNodeProjects(appData.getLatestVersions(), nodeRepository));
    }

    if (requestMetadata.getUpdateType().equals(RequestParams.UpdateType.ALL)
        || requestMetadata.getUpdateType().equals(RequestParams.UpdateType.PYTHON)) {
      final List<AppDataRepository> pythonRepositories =
          repositories.stream()
              .filter(repository -> repository.getType().equals(RequestParams.UpdateType.PYTHON))
              .toList();
      pythonRepositories.forEach(
          pythonRepository ->
              executeUpdatePythonProjects(appData.getLatestVersions(), pythonRepository));
    }
  }

  private void executeUpdateGradleProjects(
      final AppDataLatestVersions latestVersions, final AppDataRepository repository) {
    addTaskToQueue(
        String.format(
            ConstantUtils.QUEUE_UPDATE_DEPENDENCIES, ConstantUtils.GRADLE_NAME.toUpperCase()),
        String.format(
            ConstantUtils.TASK_UPDATE_DEPENDENCIES, repository.getRepoName().toUpperCase()),
        () -> new GradleProjectUpdate(latestVersions, repository).execute(),
        ConstantUtils.TASK_DELAY_ZERO);
  }

  private void executeUpdateNodeProjects(
      final AppDataLatestVersions latestVersions, final AppDataRepository repository) {
    addTaskToQueue(
        String.format(
            ConstantUtils.QUEUE_UPDATE_DEPENDENCIES, ConstantUtils.NODE_NAME.toUpperCase()),
        String.format(
            ConstantUtils.TASK_UPDATE_DEPENDENCIES, repository.getRepoName().toUpperCase()),
        () -> new NodeProjectUpdate(latestVersions, repository).execute(),
        ConstantUtils.TASK_DELAY_ZERO);
  }

  private void executeUpdatePythonProjects(
      final AppDataLatestVersions latestVersions, final AppDataRepository repository) {
    addTaskToQueue(
        String.format(
            ConstantUtils.QUEUE_UPDATE_DEPENDENCIES, ConstantUtils.PYTHON_NAME.toUpperCase()),
        String.format(
            ConstantUtils.TASK_UPDATE_DEPENDENCIES, repository.getRepoName().toUpperCase()),
        () -> new PythonProjectUpdate(latestVersions, repository).execute(),
        ConstantUtils.TASK_DELAY_ZERO);
  }

  private void executeUpdateCreatePullRequests(
      final RequestMetadata requestMetadata, final boolean isCheckUpdateBranchBeforeCreate) {
    final AppData appData = AppDataUtils.getAppData();
    final String requestRepoName = requestMetadata.getRepoName();

    if (CommonUtilities.isEmpty(requestRepoName)) {
      // process all repositories
      final List<AppDataRepository> repositories = appData.getRepositories();
      for (AppDataRepository repository : repositories) {
        addTaskToQueue(
            ConstantUtils.QUEUE_PULL_REQUESTS_CREATE,
            String.format(
                ConstantUtils.TASK_PULL_REQUESTS_CREATE, repository.getRepoName().toUpperCase()),
            () ->
                githubService.createGithubPullRequest(
                    repository.getRepoName(),
                    requestMetadata.getBranchDate(),
                    isCheckUpdateBranchBeforeCreate),
            ConstantUtils.TASK_DELAY_ZERO);
      }
    } else {
      final AppDataRepository repository = getRepository(requestRepoName);
      addTaskToQueue(
          ConstantUtils.QUEUE_PULL_REQUESTS_CREATE,
          String.format(
              ConstantUtils.TASK_PULL_REQUESTS_CREATE, repository.getRepoName().toUpperCase()),
          () ->
              githubService.createGithubPullRequest(
                  repository.getRepoName(), requestMetadata.getBranchDate(), Boolean.FALSE),
          ConstantUtils.TASK_DELAY_ZERO);
    }
  }

  private void executeUpdateMergePullRequests(
      final RequestMetadata requestMetadata, final boolean isCheckPrCreatedBeforeMerge) {
    final AppData appData = AppDataUtils.getAppData();
    final String requestRepoName = requestMetadata.getRepoName();

    if (CommonUtilities.isEmpty(requestRepoName)) {
      final List<AppDataRepository> repositories = appData.getRepositories();
      for (int i = 0; i < repositories.size(); i++) {
        AppDataRepository repository = repositories.get(i);
        addTaskToQueue(
            ConstantUtils.QUEUE_PULL_REQUESTS_MERGE,
            String.format(
                ConstantUtils.TASK_PULL_REQUESTS_MERGE, repository.getRepoName().toUpperCase()),
            () ->
                githubService.mergeGithubPullRequest(
                    repository.getRepoName(),
                    requestMetadata.getBranchDate(),
                    null,
                    isCheckPrCreatedBeforeMerge),
            ConstantUtils.TASK_DELAY_ZERO);
      }
    } else {
      final AppDataRepository repository = getRepository(requestRepoName);
      addTaskToQueue(
          ConstantUtils.QUEUE_PULL_REQUESTS_MERGE,
          String.format(
              ConstantUtils.TASK_PULL_REQUESTS_MERGE, repository.getRepoName().toUpperCase()),
          () ->
              githubService.mergeGithubPullRequest(
                  repository.getRepoName(),
                  requestMetadata.getBranchDate(),
                  null,
                  isCheckPrCreatedBeforeMerge),
          ConstantUtils.TASK_DELAY_DEFAULT);
    }
  }

  private void resetProcessedSummariesTask() {
    addTaskToQueue(
        ConstantUtils.QUEUE_PROCESS_SUMMARY_RESET,
        ConstantUtils.TASK_PROCESS_SUMMARY_RESET,
        ProcessUtils::resetProcessedRepositoriesAndSummary,
        ConstantUtils.TASK_DELAY_ZERO);
  }

  private void getCheckedUpdatedInPastDayTask() {
    addTaskToQueue(
        ConstantUtils.QUEUE_MONGO_UPDATE,
        ConstantUtils.TASK_MONGO_UPDATE,
        this::getMongoRepoCheckedUpdated,
        ConstantUtils.TASK_DELAY_ZERO);
  }

  private void addTaskToQueue(
      final String queueName,
      final String taskName,
      final Runnable action,
      final long delayMillis) {
    log.debug(
        "Add Task To Queue: QueueName=[{}] TaskName=[{}] DelayMillis=[{}]",
        queueName,
        taskName,
        delayMillis);
    TaskQueues.TaskQueue taskQueue = TASK_QUEUES.getQueueByName(queueName);
    if (taskQueue == null) {
      taskQueue = new TaskQueues.TaskQueue(queueName);
      taskQueue.addTask(new TaskQueues.TaskQueue.OneTask(taskName, action, delayMillis));
      TASK_QUEUES.addQueue(taskQueue);
    } else {
      taskQueue.addTask(new TaskQueues.TaskQueue.OneTask(taskName, action, delayMillis));
    }
  }

  private void makeProcessSummary(final RequestMetadata requestMetadata) {
    final RequestParams.UpdateType updateType = requestMetadata.getUpdateType();
    boolean isSendEmail =
        "true".equals(AppDataUtils.getAppData().getArgsMap().get(ConstantUtils.ENV_SEND_EMAIL));

    log.info("Make Process Summary: IsSendEmail=[{}] | UpdateType=[{}]", isSendEmail, updateType);

    ProcessSummaries.ProcessSummary processSummary = processSummary(updateType);

    if (isSendEmail) {
      final String subject = "Dependency Management Scheduled Update Logs";
      final String html = ProcessSummaryEmailUtils.getProcessSummaryContent(processSummary);
      // log.debug(html);
      final String attachment = LogCaptureUtils.getCapturedLogs();
      final String attachmentFileName =
          String.format("dep_mgmt_scheduled_update_logs_%s.log", LocalDate.now());
      emailService.sendEmail(subject, html, attachmentFileName, attachment);
    }
  }

  private ProcessSummaries.ProcessSummary processSummary(
      final RequestParams.UpdateType updateType) {
    Map<String, ProcessSummaries.ProcessSummary.ProcessRepository> processedRepositoryMap =
        ProcessUtils.getProcessedRepositoriesMap();
    List<ProcessSummaries.ProcessSummary.ProcessRepository> processedRepositories =
        new ArrayList<>(ProcessUtils.getProcessedRepositoriesMap().values().stream().toList());
    List<AppDataRepository> allRepositories = AppDataUtils.getAppData().getRepositories();

    for (AppDataRepository repository : allRepositories) {
      if (!processedRepositoryMap.containsKey(repository.getRepoName())) {
        processedRepositories.add(
            new ProcessSummaries.ProcessSummary.ProcessRepository(
                repository.getRepoName(), repository.getType().toString()));
      }
    }

    processedRepositories.sort(
        Comparator.comparing(ProcessSummaries.ProcessSummary.ProcessRepository::getRepoName));

    final Integer totalPrCreatedCount =
        (int)
            processedRepositories.stream()
                .filter(ProcessSummaries.ProcessSummary.ProcessRepository::getPrCreated)
                .count();
    final Integer totalPrMergedCount =
        (int)
            processedRepositories.stream()
                .filter(ProcessSummaries.ProcessSummary.ProcessRepository::getPrMerged)
                .count();
    final Integer totalPrMergeErrorCount = totalPrCreatedCount - totalPrMergedCount;

    List<ProcessSummaries.ProcessSummary.ProcessTask> processedTasks =
        ProcessUtils.getProcessedTasks().values().stream()
            .sorted(Comparator.comparing(ProcessSummaries.ProcessSummary.ProcessTask::getAdded))
            .toList();
    final ProcessSummaries.ProcessSummary processSummary =
        new ProcessSummaries.ProcessSummary(
            LocalDateTime.now(),
            updateType.name(),
            ProcessUtils.getMongoGradlePluginsChecked(),
            ProcessUtils.getMongoGradleDependenciesChecked(),
            ProcessUtils.getMongoPythonPackagesChecked(),
            ProcessUtils.getMongoNodeDependenciesChecked(),
            ProcessUtils.getMongoGradlePluginsToUpdate(),
            ProcessUtils.getMongoGradleDependenciesToUpdate(),
            ProcessUtils.getMongoPythonPackagesToUpdate(),
            ProcessUtils.getMongoNodeDependenciesToUpdate(),
            totalPrCreatedCount,
            totalPrMergedCount,
            totalPrMergeErrorCount,
            processedRepositories,
            ProcessUtils.getErrorsOrExceptions(),
            processedTasks);

    // save to repository
    final ProcessSummaryEntity processSummaryEntity =
        ConvertUtils.convertProcessSummary(processSummary);
    processSummaryService.saveProcessSummary(processSummaryEntity);
    return processSummary;
  }

  private void checkGithubRateLimits() {
    addTaskToQueue(
        ConstantUtils.QUEUE_GITHUB_RATE_LIMIT,
        ConstantUtils.TASK_GITHUB_RATE_LIMIT,
        githubService::getCurrentGithubRateLimits,
        ConstantUtils.TASK_DELAY_DEFAULT);
  }

  private void saveStopLogCapture() {
    boolean isSendEmail =
        "true".equals(AppDataUtils.getAppData().getArgsMap().get(ConstantUtils.ENV_SEND_EMAIL));
    // save log capture
    addTaskToQueue(
        ConstantUtils.QUEUE_LOG_CAPTURE,
        ConstantUtils.TASK_LOG_CAPTURE_SAVE,
        () -> {
          logEntryService.saveLogEntry(null);
        },
        ConstantUtils.TASK_DELAY_DEFAULT * 5);

    // stop log capture
    addTaskToQueue(
        ConstantUtils.QUEUE_LOG_CAPTURE,
        ConstantUtils.TASK_LOG_CAPTURE_STOP,
        LogCaptureUtils::stop,
        ConstantUtils.TASK_DELAY_DEFAULT * 5);
  }

  private AppDataScriptFile getScriptFile(final String scriptFileName) {
    return AppDataUtils.getAppData().getScriptFiles().stream()
        .filter(script -> script.getScriptName().equals(scriptFileName))
        .findFirst()
        .orElseThrow(
            () -> new IllegalStateException("'" + scriptFileName + "' Script File Not Found..."));
  }

  private AppDataRepository getRepository(final String repoName) {
    return AppDataUtils.getAppData().getRepositories().stream()
        .filter(repo -> repo.getRepoName().equals(repoName))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("'" + repoName + "' Repository Not Found..."));
  }

  private void getMongoRepoCheckedUpdated() {
    ProcessUtils.setMongoGradlePluginsChecked(
        gradlePluginVersionService.getCheckedCountInPastDay());
    ProcessUtils.setMongoGradlePluginsToUpdate(
        gradlePluginVersionService.getUpdatedCountInPastDay());
    ProcessUtils.setMongoGradleDependenciesChecked(
        gradleDependencyVersionService.getCheckedCountInPastDay());
    ProcessUtils.setMongoGradleDependenciesToUpdate(
        gradleDependencyVersionService.getUpdatedCountInPastDay());
    ProcessUtils.setMongoPythonPackagesChecked(
        pythonPackageVersionService.getCheckedCountInPastDay());
    ProcessUtils.setMongoPythonPackagesToUpdate(
        pythonPackageVersionService.getUpdatedCountInPastDay());
    ProcessUtils.setMongoNodeDependenciesChecked(
        nodeDependencyVersionService.getCheckedCountInPastDay());
    ProcessUtils.setMongoNodeDependenciesToUpdate(
        nodeDependencyVersionService.getUpdatedCountInPastDay());
  }
}
