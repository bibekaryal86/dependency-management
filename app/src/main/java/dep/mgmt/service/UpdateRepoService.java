package dep.mgmt.service;

import dep.mgmt.config.CacheConfig;
import dep.mgmt.model.AppData;
import dep.mgmt.model.AppDataRepository;
import dep.mgmt.model.AppDataScriptFile;
import dep.mgmt.model.ProcessSummaries;
import dep.mgmt.model.RequestMetadata;
import dep.mgmt.model.TaskQueues;
import dep.mgmt.model.entity.ProcessSummaryEntity;
import dep.mgmt.model.enums.RequestParams;
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
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateRepoService {
  private static final Logger log = LoggerFactory.getLogger(UpdateRepoService.class);
  private static final TaskQueues taskQueues = new TaskQueues();

  private final GradleDependencyVersionService gradleDependencyVersionService;
  private final GradlePluginVersionService gradlePluginVersionService;
  private final NodeDependencyVersionService nodeDependencyVersionService;
  private final PythonPackageVersionService pythonPackageVersionService;
  private final ExcludedRepoService excludedRepoService;
  private final GithubService githubService;
  private final EmailService emailService;
  private final ProcessSummaryService processSummaryService;
  private final ScriptUtils scriptUtils;

  public UpdateRepoService() {
    this.gradleDependencyVersionService = new GradleDependencyVersionService();
    this.gradlePluginVersionService = new GradlePluginVersionService();
    this.nodeDependencyVersionService = new NodeDependencyVersionService();
    this.pythonPackageVersionService = new PythonPackageVersionService();
    this.excludedRepoService = new ExcludedRepoService();
    this.githubService = new GithubService();
    this.emailService = new EmailService();
    this.processSummaryService = new ProcessSummaryService();
    this.scriptUtils = new ScriptUtils();
  }

  public Map<String, List<ProcessSummaries.ProcessSummary.ProcessTask>> getAllProcessTaskQueues() {
    final List<ProcessSummaries.ProcessSummary.ProcessTask> processTasks =
        ProcessUtils.getProcessedTasks().values().stream().toList();
    List<ProcessSummaries.ProcessSummary.ProcessTask> queueTasks = new ArrayList<>();

    for (TaskQueues.TaskQueue taskQueue : taskQueues.getQueueOfQueues()) {
      List<TaskQueues.TaskQueue.OneTask> oneTaskList = taskQueue.getTaskQueue();
      for (TaskQueues.TaskQueue.OneTask oneTask : oneTaskList) {
        queueTasks.add(
            new ProcessSummaries.ProcessSummary.ProcessTask(
                taskQueue.getName(), oneTask.getName()));
      }
    }

    return Map.of("processTasks", processTasks, "queueTasks", queueTasks);
  }

  public void executeTaskQueues() {
    log.info("Execute Task Queues...");
    if (!taskQueues.isProcessing()) {
      taskQueues.processQueues();
    }
  }

  public void clearTaskQueues() {
    log.info("Clear Task Queues...");
    taskQueues.clearQueue();
    ProcessUtils.resetProcessedTasks();
  }

  public void scheduledUpdate() {
    log.info("Scheduled Update...");
    final RequestMetadata requestMetadata =
        new RequestMetadata(
            RequestParams.UpdateType.ALL,
            Boolean.FALSE,
            Boolean.FALSE,
            Boolean.TRUE,
            Boolean.TRUE,
            Boolean.TRUE,
            Boolean.TRUE,
            Boolean.FALSE,
            LocalDate.now(),
            null);
    updateRepos(requestMetadata, Boolean.TRUE);
  }

  public void updateRepos(final RequestMetadata requestMetadata, final boolean isScheduledUpdate) {
    log.info("Update Repos: [{}] | [{}]", requestMetadata, isScheduledUpdate);
    updateInit(requestMetadata);

    boolean isPrCreateRequired = false;
    boolean isPrMergeRequired = false;

    switch (requestMetadata.getUpdateType()) {
      case PULL, RESET -> log.info("Pull/Reset covered in updateInit...");
      case DELETE ->
          executeGithubBranchDelete(
              requestMetadata.getDeleteUpdateDependenciesOnly(), requestMetadata.getRepoName());
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
        executeUpdateDependencies(requestMetadata, Boolean.FALSE);
      }
      default ->
          throw new IllegalArgumentException(
              String.format("Invalid Update Type: [ '%s' ]", requestMetadata.getUpdateType()));
    }

    if (isPrCreateRequired) {
      executeUpdateCreatePullRequests(requestMetadata, isScheduledUpdate);
    }

    if (isPrMergeRequired) {
      executeUpdateMergePullRequests(requestMetadata, isScheduledUpdate);
    }

    logGithubRateLimit();
    makeProcessSummaryTask(requestMetadata);
    executeUpdateContinuedForMergeRetry(requestMetadata);
    updateExit(requestMetadata);
    executeTaskQueues();
  }

  public void recreateLocalCaches() {
    resetCaches();
    setCaches();
  }

  private void resetCaches() {
    addTaskToQueue(
        ConstantUtils.QUEUE_RESET,
        ConstantUtils.TASK_RESET_APP_DATA,
        CacheConfig::resetAppData,
        Long.MIN_VALUE);
    addTaskToQueue(
        ConstantUtils.QUEUE_RESET,
        ConstantUtils.TASK_RESET_GRADLE_DEPENDENCIES,
        CacheConfig::resetGradleDependenciesMap,
        Long.MIN_VALUE);
    addTaskToQueue(
        ConstantUtils.QUEUE_RESET,
        ConstantUtils.TASK_RESET_GRADLE_PLUGINS,
        CacheConfig::resetGradlePluginsMap,
        Long.MIN_VALUE);
    addTaskToQueue(
        ConstantUtils.QUEUE_RESET,
        ConstantUtils.TASK_RESET_NODE_DEPENDENCIES,
        CacheConfig::resetNodeDependenciesMap,
        Long.MIN_VALUE);
    addTaskToQueue(
        ConstantUtils.QUEUE_RESET,
        ConstantUtils.TASK_RESET_PYTHON_PACKAGES,
        CacheConfig::resetPythonPackagesMap,
        Long.MIN_VALUE);
    addTaskToQueue(
        ConstantUtils.QUEUE_RESET,
        ConstantUtils.TASK_RESET_EXCLUDED_REPOS,
        CacheConfig::resetExcludedReposMap,
        Long.MIN_VALUE);
  }

  private void setCaches() {
    addTaskToQueue(
        ConstantUtils.QUEUE_SET,
        ConstantUtils.TASK_SET_APP_DATA,
        AppDataUtils::setAppData,
        ConstantUtils.TASK_DELAY_DEFAULT);
    addTaskToQueue(
        ConstantUtils.QUEUE_SET,
        ConstantUtils.TASK_SET_GRADLE_DEPENDENCIES,
        gradleDependencyVersionService::getGradleDependenciesMap,
        Long.MIN_VALUE);
    addTaskToQueue(
        ConstantUtils.QUEUE_SET,
        ConstantUtils.TASK_SET_GRADLE_PLUGINS,
        gradlePluginVersionService::getGradlePluginsMap,
        Long.MIN_VALUE);
    addTaskToQueue(
        ConstantUtils.QUEUE_SET,
        ConstantUtils.TASK_SET_NODE_DEPENDENCIES,
        nodeDependencyVersionService::getNodeDependenciesMap,
        Long.MIN_VALUE);
    addTaskToQueue(
        ConstantUtils.QUEUE_SET,
        ConstantUtils.TASK_SET_PYTHON_PACKAGES,
        pythonPackageVersionService::getPythonPackagesMap,
        Long.MIN_VALUE);
    addTaskToQueue(
        ConstantUtils.QUEUE_SET,
        ConstantUtils.TASK_SET_EXCLUDED_REPOS,
        excludedRepoService::getExcludedReposMap,
        Long.MIN_VALUE);
  }

  public void recreateRemoteCaches() {
    // clear and set caches after pull (gradle version in repo could have changed)
    resetCaches();
    gradleDependencyVersionService.updateGradleDependencies();
    gradlePluginVersionService.updateGradlePlugins();
    nodeDependencyVersionService.updateNodeDependencies();
    pythonPackageVersionService.updatePythonPackages();
    setCaches();
  }

  private void recreateScriptFiles() {
    addTaskToQueue(
        ConstantUtils.QUEUE_FILES,
        ConstantUtils.TASK_DELETE_SCRIPT_FILES,
        scriptUtils::deleteTempScriptFiles,
        Long.MIN_VALUE);
    addTaskToQueue(
        ConstantUtils.QUEUE_FILES,
        ConstantUtils.TASK_CREATE_SCRIPT_FILES,
        scriptUtils::createTempScriptFiles,
        ConstantUtils.TASK_DELAY_DEFAULT);
  }

  private void updateInit(final RequestMetadata requestMetadata) {
    resetProcessedSummaries(Boolean.TRUE);

    if (requestMetadata.getRecreateCaches()) {
      recreateLocalCaches();
    }

    if (requestMetadata.getRecreateScriptFiles()
        || scriptUtils.isScriptFilesMissingInFileSystem()) {
      recreateScriptFiles();
    }

    if (requestMetadata.getGithubResetRequired() || requestMetadata.getIsGithubPullRequired()) {
      executeUpdateGithubResetPull(
          requestMetadata.getGithubResetRequired(),
          requestMetadata.getIsGithubPullRequired(),
          requestMetadata.getRepoName());

      if (requestMetadata.getRecreateCaches()) {
        recreateRemoteCaches();
      }
    }
  }

  private void updateExit(final RequestMetadata requestMetadata) {
    executeUpdateDependencies(requestMetadata, Boolean.TRUE);
    resetProcessedSummaries(Boolean.FALSE);
  }

  private void executeNpmSnapshotsUpdate(final LocalDate branchDate, final String repoName) {
    final AppData appData = AppDataUtils.getAppData();
    final String branchName = String.format(ConstantUtils.BRANCH_UPDATE_DEPENDENCIES, branchDate);

    final AppDataScriptFile scriptFile =
        appData.getScriptFiles().stream()
            .filter(script -> script.getScriptName().equals(ConstantUtils.SCRIPT_SNAPSHOT))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("NPM Snapshot Script File Not Found"));

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
        ConstantUtils.TASK_NPM_SNAPSHOTS + ConstantUtils.APPENDER_QUEUE_NAME,
        ConstantUtils.TASK_NPM_SNAPSHOTS + ConstantUtils.APPENDER_TASK_NAME,
        () -> new UpdateNpmSnapshots(repositories, scriptFile, branchName).execute(),
        Long.MIN_VALUE);
  }

  private void executeGradleSpotlessUpdate(final LocalDate branchDate, final String repoName) {
    final AppData appData = AppDataUtils.getAppData();
    final String branchName = String.format(ConstantUtils.BRANCH_UPDATE_DEPENDENCIES, branchDate);

    final AppDataScriptFile scriptFile =
        appData.getScriptFiles().stream()
            .filter(script -> script.getScriptName().equals(ConstantUtils.SCRIPT_SPOTLESS))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Gradle Spotless Script File Not Found"));

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
        ConstantUtils.TASK_GRADLE_SPOTLESS + ConstantUtils.APPENDER_QUEUE_NAME,
        ConstantUtils.TASK_GRADLE_SPOTLESS + ConstantUtils.APPENDER_TASK_NAME,
        () -> new UpdateGradleSpotless(repositories, scriptFile, branchName).execute(),
        Long.MIN_VALUE);
  }

  private void executeGithubBranchDelete(
      final boolean isDeleteUpdateDependenciesOnly, final String repoName) {
    final AppData appData = AppDataUtils.getAppData();

    if (CommonUtilities.isEmpty(repoName)) {
      final String repoHome = appData.getArgsMap().get(ConstantUtils.ENV_REPO_HOME);
      final AppDataScriptFile scriptFile =
          appData.getScriptFiles().stream()
              .filter(script -> script.getScriptName().equals(ConstantUtils.SCRIPT_DELETE))
              .findFirst()
              .orElseThrow(
                  () -> new IllegalStateException("Gradle Spotless Script File Not Found"));

      addTaskToQueue(
          ConstantUtils.TASK_GITHUB_BRANCH_DELETE + ConstantUtils.APPENDER_QUEUE_NAME,
          ConstantUtils.TASK_GITHUB_BRANCH_DELETE + ConstantUtils.APPENDER_TASK_NAME,
          () ->
              new UpdateBranchDelete(repoHome, scriptFile, isDeleteUpdateDependenciesOnly)
                  .execute(),
          Long.MIN_VALUE);
    } else {
      final AppDataRepository repository =
          appData.getRepositories().stream()
              .filter(repo -> repo.getRepoName().equals(repoName))
              .findFirst()
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Repo Not Found by Repo Name ['" + repoName + "']"));
      final AppDataScriptFile scriptFile =
          appData.getScriptFiles().stream()
              .filter(script -> script.getScriptName().equals(ConstantUtils.SCRIPT_DELETE_ONE))
              .findFirst()
              .orElseThrow(
                  () -> new IllegalStateException("Gradle Spotless One Script File Not Found"));

      addTaskToQueue(
          ConstantUtils.TASK_GITHUB_BRANCH_DELETE + ConstantUtils.APPENDER_QUEUE_NAME,
          ConstantUtils.TASK_GITHUB_BRANCH_DELETE + ConstantUtils.APPENDER_TASK_NAME,
          () ->
              new UpdateBranchDelete(repository, scriptFile, isDeleteUpdateDependenciesOnly)
                  .execute(),
          Long.MIN_VALUE);
    }
  }

  private void executeUpdateGithubResetPull(
      final boolean isReset, final boolean isPull, final String repoName) {
    final AppData appData = AppDataUtils.getAppData();

    if (CommonUtilities.isEmpty(repoName)) {
      final String repoHome = appData.getArgsMap().get(ConstantUtils.ENV_REPO_HOME);
      final AppDataScriptFile scriptFile =
          appData.getScriptFiles().stream()
              .filter(script -> script.getScriptName().equals(ConstantUtils.SCRIPT_RESET_PULL))
              .findFirst()
              .orElseThrow(() -> new IllegalStateException("GitHub Reset Pull Script Not Found"));

      addTaskToQueue(
          ConstantUtils.TASK_GITHUB_RESET_PULL + ConstantUtils.APPENDER_QUEUE_NAME,
          ConstantUtils.TASK_GITHUB_RESET_PULL + ConstantUtils.APPENDER_TASK_NAME,
          () -> new UpdateRepoResetPull(repoHome, scriptFile, isReset, isPull).execute(),
          Long.MIN_VALUE);
    } else {
      final AppDataRepository repository =
          appData.getRepositories().stream()
              .filter(repo -> repo.getRepoName().equals(repoName))
              .findFirst()
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Repo Not Found by Repo Name ['" + repoName + "']"));
      final AppDataScriptFile scriptFile =
          appData.getScriptFiles().stream()
              .filter(script -> script.getScriptName().equals(ConstantUtils.SCRIPT_RESET_PULL_ONE))
              .findFirst()
              .orElseThrow(
                  () -> new IllegalStateException("GitHub Reset Pull One Script Not Found"));

      addTaskToQueue(
          ConstantUtils.TASK_GITHUB_RESET_PULL + ConstantUtils.APPENDER_QUEUE_NAME,
          ConstantUtils.TASK_GITHUB_RESET_PULL + ConstantUtils.APPENDER_TASK_NAME,
          () -> new UpdateRepoResetPull(repository, scriptFile, isReset, isPull).execute(),
          Long.MIN_VALUE);
    }
  }

  private void executeUpdateDependencies(
      final RequestMetadata requestMetadata, final boolean isExit) {
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

    final AppDataScriptFile scriptFileInit =
        appData.getScriptFiles().stream()
            .filter(script -> script.getScriptName().equals(ConstantUtils.SCRIPT_UPDATE_INIT))
            .findFirst()
            .orElseThrow(
                () -> new IllegalStateException("Update Dependencies Init/Exit Script Not Found"));
    final AppDataScriptFile scriptFileExec =
        appData.getScriptFiles().stream()
            .filter(script -> script.getScriptName().equals(ConstantUtils.SCRIPT_UPDATE_EXEC))
            .findFirst()
            .orElseThrow(
                () -> new IllegalStateException("Update Dependencies Execute Script Not Found"));

    if (isExit) {
      repositories.forEach(
          repository ->
              executeUpdateDependenciesInitExit(repository, scriptFileInit, Boolean.FALSE));
    } else {
      repositories.forEach(
          repository ->
              executeUpdateDependenciesInitExit(repository, scriptFileInit, Boolean.TRUE));
      repositories.forEach(
          repository ->
              executeUpdateDependenciesExec(
                  repository, scriptFileExec, requestMetadata.getBranchDate()));
    }
  }

  private void executeUpdateDependenciesInitExit(
      final AppDataRepository repository,
      final AppDataScriptFile scriptFile,
      final boolean isInit) {
    addTaskToQueue(
        getUpdateDependenciesQueueName(
            isInit ? ConstantUtils.APPENDER_INIT : ConstantUtils.APPENDER_EXIT),
        getUpdateDependenciesTaskName(
            repository.getRepoName(),
            isInit ? ConstantUtils.APPENDER_INIT : ConstantUtils.APPENDER_EXIT),
        () -> new UpdateDependencies(repository, scriptFile, isInit).execute(),
        Long.MIN_VALUE);
  }

  private void executeUpdateDependenciesExec(
      final AppDataRepository repository,
      final AppDataScriptFile scriptFile,
      final LocalDate branchDate) {
    final String branchName = String.format(ConstantUtils.BRANCH_UPDATE_DEPENDENCIES, branchDate);
    addTaskToQueue(
        getUpdateDependenciesQueueName(ConstantUtils.APPENDER_EXEC),
        getUpdateDependenciesTaskName(repository.getRepoName(), ConstantUtils.APPENDER_EXEC),
        () -> new UpdateDependencies(repository, scriptFile, branchName).execute(),
        Long.MIN_VALUE);
  }

  private void executeUpdateCreatePullRequests(
      final RequestMetadata requestMetadata, final boolean isScheduledUpdate) {
    final AppData appData = AppDataUtils.getAppData();
    final String requestRepoName = requestMetadata.getRepoName();

    if (isScheduledUpdate) {
      // process the processed repositories
      final List<ProcessSummaries.ProcessSummary.ProcessRepository> repositories =
          ProcessUtils.getProcessedRepositoriesMap().values().stream().toList();
      for (ProcessSummaries.ProcessSummary.ProcessRepository repository : repositories) {
        if (repository.getUpdateBranchCreated()) {
          addTaskToQueue(
              ConstantUtils.QUEUE_CREATE_PULL_REQUESTS,
              getPullRequestsTaskName(repository.getRepoName(), ConstantUtils.APPENDER_INIT),
              () ->
                  githubService.createGithubPullRequest(
                      repository.getRepoName(), requestMetadata.getBranchDate()),
              ConstantUtils.TASK_DELAY_PULL_REQUEST);
        }
      }
    } else if (CommonUtilities.isEmpty(requestRepoName)) {
      // process all repositories
      final List<AppDataRepository> repositories = appData.getRepositories();
      for (AppDataRepository repository : repositories) {
        addTaskToQueue(
            ConstantUtils.QUEUE_CREATE_PULL_REQUESTS,
            getPullRequestsTaskName(repository.getRepoName(), ConstantUtils.APPENDER_INIT),
            () ->
                githubService.createGithubPullRequest(
                    repository.getRepoName(), requestMetadata.getBranchDate()),
            ConstantUtils.TASK_DELAY_PULL_REQUEST);
      }
    } else {
      // process requested repository
      final AppDataRepository repository =
          appData.getRepositories().stream()
              .filter(repo -> repo.getRepoName().equals(requestRepoName))
              .findFirst()
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Repo Not Found by Repo Name ['" + requestRepoName + "']"));
      addTaskToQueue(
          ConstantUtils.QUEUE_CREATE_PULL_REQUESTS,
          getPullRequestsTaskName(requestRepoName, ConstantUtils.APPENDER_INIT),
          () ->
              githubService.createGithubPullRequest(
                  requestRepoName, requestMetadata.getBranchDate()),
          Long.MIN_VALUE);
    }
  }

  private void executeUpdateMergePullRequests(
      final RequestMetadata requestMetadata, final boolean isScheduledUpdate) {
    final AppData appData = AppDataUtils.getAppData();
    final String requestRepoName = requestMetadata.getRepoName();

    if (isScheduledUpdate) {
      // process the processed repositories
      final List<ProcessSummaries.ProcessSummary.ProcessRepository> repositories =
          ProcessUtils.getProcessedRepositoriesMap().values().stream().toList();
      for (int i = 0; i < repositories.size(); i++) {
        ProcessSummaries.ProcessSummary.ProcessRepository repository = repositories.get(i);
        if (repository.getPrCreated()) {
          addTaskToQueue(
              ConstantUtils.QUEUE_MERGE_PULL_REQUESTS,
              getPullRequestsTaskName(repository.getRepoName(), ConstantUtils.APPENDER_EXIT),
              () ->
                  githubService.mergeGithubPullRequest(
                      repository.getRepoName(),
                      requestMetadata.getBranchDate(),
                      repository.getPrNumber()),
              i == 0
                  ? ConstantUtils.TASK_DELAY_PULL_REQUEST_TRY
                  : ConstantUtils.TASK_DELAY_PULL_REQUEST);
        }
      }
    } else if (CommonUtilities.isEmpty(requestRepoName)) {
      // process all repositories
      final List<AppDataRepository> repositories = appData.getRepositories();
      for (int i = 0; i < repositories.size(); i++) {
        AppDataRepository repository = repositories.get(i);
        addTaskToQueue(
            ConstantUtils.QUEUE_MERGE_PULL_REQUESTS,
            getPullRequestsTaskName(repository.getRepoName(), ConstantUtils.APPENDER_EXIT),
            () ->
                githubService.mergeGithubPullRequest(
                    repository.getRepoName(), requestMetadata.getBranchDate(), null),
            i == 0
                ? ConstantUtils.TASK_DELAY_PULL_REQUEST_TRY
                : ConstantUtils.TASK_DELAY_PULL_REQUEST);
      }
    } else {
      // process requested repository
      final AppDataRepository repository =
          appData.getRepositories().stream()
              .filter(repo -> repo.getRepoName().equals(requestRepoName))
              .findFirst()
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Repo Not Found by Repo Name ['" + requestRepoName + "']"));
      addTaskToQueue(
          ConstantUtils.QUEUE_MERGE_PULL_REQUESTS,
          getPullRequestsTaskName(repository.getRepoName(), ConstantUtils.APPENDER_EXIT),
          () ->
              githubService.mergeGithubPullRequest(
                  repository.getRepoName(), requestMetadata.getBranchDate(), null),
          ConstantUtils.TASK_DELAY_PULL_REQUEST);
    }
  }

  private void executeUpdateContinuedForMergeRetry(final RequestMetadata requestMetadata) {
    Set<String> repositoriesToRetryMerge = ProcessUtils.getRepositoriesToRetryMerge();
    if (!repositoriesToRetryMerge.isEmpty()) {
      final List<ProcessSummaries.ProcessSummary.ProcessRepository> repositories =
          ProcessUtils.getProcessedRepositoriesMap().values().stream()
              .filter(pr -> repositoriesToRetryMerge.contains(pr.getRepoName()))
              .toList();
      for (int i = 0; i < repositories.size(); i++) {
        ProcessSummaries.ProcessSummary.ProcessRepository repository = repositories.get(i);
        if (repository.getPrCreated()) {
          addTaskToQueue(
              ConstantUtils.QUEUE_MERGE_PULL_REQUESTS_RETRY,
              getPullRequestsTaskName(
                  repository.getRepoName(),
                  ConstantUtils.APPENDER_EXIT + ConstantUtils.APPENDER_RETRY),
              () ->
                  githubService.mergeGithubPullRequest(
                      repository.getRepoName(),
                      requestMetadata.getBranchDate(),
                      repository.getPrNumber()),
              i == 0
                  ? ConstantUtils.TASK_DELAY_PULL_REQUEST_RETRY
                  : ConstantUtils.TASK_DELAY_PULL_REQUEST);
        }
      }
    }
  }

  private String getUpdateDependenciesQueueName(final String appender) {
    return String.format(ConstantUtils.QUEUE_UPDATE_DEPENDENCIES, appender);
  }

  private void resetProcessedSummaries(final boolean isInit) {
    addTaskToQueue(
        ConstantUtils.TASK_RESET_PROCESS_SUMMARIES
            + ConstantUtils.APPENDER_QUEUE_NAME
            + "_"
            + (isInit ? ConstantUtils.APPENDER_INIT : ConstantUtils.APPENDER_EXIT),
        ConstantUtils.TASK_RESET_PROCESS_SUMMARIES
            + ConstantUtils.APPENDER_TASK_NAME
            + "_"
            + (isInit ? ConstantUtils.APPENDER_INIT : ConstantUtils.APPENDER_EXIT),
        ProcessUtils::resetProcessedRepositoriesAndSummary,
        Long.MIN_VALUE);
  }

  private String getUpdateDependenciesTaskName(final String repoName, final String appender) {
    return String.format(ConstantUtils.TASK_UPDATE_DEPENDENCIES, repoName, appender);
  }

  private String getPullRequestsTaskName(final String repoName, final String appender) {
    return String.format(ConstantUtils.TASK_PULL_REQUESTS, repoName, appender);
  }

  private void addTaskToQueue(
      final String queueName,
      final String taskName,
      final Runnable action,
      final long delayMillis) {
    TaskQueues.TaskQueue taskQueue = taskQueues.getQueueByName(queueName);
    if (taskQueue == null) {
      taskQueue = new TaskQueues.TaskQueue(queueName);
      taskQueue.addTask(new TaskQueues.TaskQueue.OneTask(taskName, action, delayMillis));
      taskQueues.addQueue(taskQueue);
    } else {
      taskQueue.addTask(new TaskQueues.TaskQueue.OneTask(taskName, action, delayMillis));
    }
    ProcessUtils.addProcessedTasks(queueName, taskName);
  }

  private void makeProcessSummaryTask(final RequestMetadata requestMetadata) {
    if (requestMetadata.getProcessSummaryRequired()) {
      addTaskToQueue(
          ConstantUtils.QUEUE_PROCESS_SUMMARY,
          ConstantUtils.QUEUE_PROCESS_SUMMARY,
          () -> makeProcessSummary(requestMetadata),
          ConstantUtils.TASK_DELAY_DEFAULT);
    }
  }

  private void makeProcessSummary(final RequestMetadata requestMetadata) {
    final boolean isProcessSummaryRequired = requestMetadata.getProcessSummaryRequired();
    final RequestParams.UpdateType updateType = requestMetadata.getUpdateType();
    boolean isSendEmail =
        "true".equals(AppDataUtils.getAppData().getArgsMap().get(ConstantUtils.ENV_SEND_EMAIL));

    log.info(
        "Make Process Summary: [ {} ] | [ {} ] | [ {} ]",
        isSendEmail,
        isProcessSummaryRequired,
        updateType);

    ProcessSummaries.ProcessSummary processSummary = null;
    if (isProcessSummaryRequired) {
      processSummary = processSummary(updateType);
    }

    if (isSendEmail && processSummary != null) {
      String subject = "Dependency Management Scheduled Update Logs";
      String html = ProcessSummaryEmailUtils.getProcessSummaryContent(processSummary);
      // log.debug(html);
      String attachmentFileName =
          String.format("dep_mgmt_scheduled_update_logs_%s.log", LocalDate.now());
      String attachment = LogCaptureUtils.getCapturedLogs();
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
    final Integer totalPrMergeErrorCount = ProcessUtils.getRepositoriesToRetryMerge().size();

    List<ProcessSummaries.ProcessSummary.ProcessTask> processedTasks =
        ProcessUtils.getProcessedTasks().values().stream().toList();
    final ProcessSummaries.ProcessSummary processSummary =
        new ProcessSummaries.ProcessSummary(
            LocalDateTime.now(),
            updateType.name(),
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

  private void logGithubRateLimit() {
    addTaskToQueue(
        ConstantUtils.TASK_GITHUB_RATE_LIMIT + ConstantUtils.APPENDER_QUEUE_NAME,
        ConstantUtils.TASK_GITHUB_RATE_LIMIT + ConstantUtils.APPENDER_TASK_NAME,
        githubService::getCurrentGithubRateLimits,
        ConstantUtils.TASK_DELAY_DEFAULT);
  }
}
