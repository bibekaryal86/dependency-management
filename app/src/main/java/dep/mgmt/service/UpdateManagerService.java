package dep.mgmt.service;

import dep.mgmt.config.CacheConfig;
import dep.mgmt.model.AppData;
import dep.mgmt.model.AppDataRepository;
import dep.mgmt.model.AppDataScriptFile;
import dep.mgmt.model.RequestMetadata;
import dep.mgmt.model.TaskQueues;
import dep.mgmt.model.enums.RequestParams;
import dep.mgmt.update.UpdateBranchDelete;
import dep.mgmt.update.UpdateDependencies;
import dep.mgmt.update.UpdateGradleSpotless;
import dep.mgmt.update.UpdateNpmSnapshots;
import dep.mgmt.update.UpdateRepoResetPull;
import dep.mgmt.util.AppDataUtils;
import dep.mgmt.util.ConstantUtils;
import dep.mgmt.util.ProcessUtils;
import dep.mgmt.util.ScriptUtils;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateManagerService {
  private static final Logger log = LoggerFactory.getLogger(UpdateManagerService.class);
  private final TaskQueues taskQueues = new TaskQueues();

  private final GradleDependencyVersionService gradleDependencyVersionService;
  private final GradlePluginVersionService gradlePluginVersionService;
  private final NodeDependencyVersionService nodeDependencyVersionService;
  private final PythonPackageVersionService pythonPackageVersionService;
  private final ExcludedRepoService excludedRepoService;
  private final ScriptUtils scriptUtils;

  public UpdateManagerService() {
    this.gradleDependencyVersionService = new GradleDependencyVersionService();
    this.gradlePluginVersionService = new GradlePluginVersionService();
    this.nodeDependencyVersionService = new NodeDependencyVersionService();
    this.pythonPackageVersionService = new PythonPackageVersionService();
    this.excludedRepoService = new ExcludedRepoService();
    this.scriptUtils = new ScriptUtils();
  }

  public void executeTaskQueues() {
    if (!taskQueues.isProcessing()) {
      taskQueues.processQueues();
    }
  }

  public void updateScheduled() {
    final RequestMetadata requestMetadata =
        new RequestMetadata(
            RequestParams.UpdateType.ALL,
            Boolean.FALSE,
            Boolean.FALSE,
            Boolean.TRUE,
            Boolean.TRUE,
            Boolean.TRUE,
            Boolean.TRUE,
            Boolean.TRUE,
            null,
            null);
    updateRepos(requestMetadata);
  }

  public void updateRepos(final RequestMetadata requestMetadata) {
    updateInit(requestMetadata);

    boolean isPrCreateRequired = false;
    boolean isPrMergeRequired = false;
    boolean isExitRequired = false;

    switch (requestMetadata.getUpdateType()) {
      case PULL, RESET -> log.info("Pull/Reset covered in updateInit...");
      case DELETE -> executeGithubBranchDelete(requestMetadata.getDeleteUpdateDependenciesOnly(), requestMetadata.getRepoName());
      case SNAPSHOT -> executeNpmSnapshotsUpdate(requestMetadata.getBranchDate(), requestMetadata.getRepoName());
      case SPOTLESS -> executeGradleSpotlessUpdate(requestMetadata.getBranchDate(), requestMetadata.getRepoName());
      case PULL_REQ -> isPrCreateRequired = true;
      case MERGE -> isPrMergeRequired = true;
      case ALL, GRADLE, NODE, PYTHON -> {
        isPrCreateRequired = true;
        isPrMergeRequired = true;
        isExitRequired = true;
        executeUpdateDependencies(requestMetadata, Boolean.FALSE);
      }
      default -> throw new IllegalArgumentException(String.format("Invalid Update Type: [ '%s' ]", requestMetadata.getUpdateType()));
    }

    updateExit(requestMetadata, isPrCreateRequired, isPrMergeRequired, isExitRequired);
    executeTaskQueues();
  }

  public void recreateLocalCaches() {
    resetCaches();
    setCaches();
  }

  private void resetCaches() {
    log.info("Reset Caches...");
    addTaskToQueue(ConstantUtils.QUEUE_RESET, ConstantUtils.TASK_RESET_APP_DATA, CacheConfig::resetAppData, Long.MIN_VALUE);
    addTaskToQueue(ConstantUtils.QUEUE_RESET, ConstantUtils.TASK_RESET_GRADLE_DEPENDENCIES, CacheConfig::resetGradleDependenciesMap, Long.MIN_VALUE);
    addTaskToQueue(ConstantUtils.QUEUE_RESET, ConstantUtils.TASK_RESET_GRADLE_PLUGINS, CacheConfig::resetGradlePluginsMap, Long.MIN_VALUE);
    addTaskToQueue(ConstantUtils.QUEUE_RESET, ConstantUtils.TASK_RESET_NODE_DEPENDENCIES, CacheConfig::resetNodeDependenciesMap, Long.MIN_VALUE);
    addTaskToQueue(ConstantUtils.QUEUE_RESET, ConstantUtils.TASK_RESET_PYTHON_PACKAGES, CacheConfig::resetPythonPackagesMap, Long.MIN_VALUE);
    addTaskToQueue(ConstantUtils.QUEUE_RESET, ConstantUtils.TASK_RESET_EXCLUDED_REPOS, CacheConfig::resetExcludedReposMap, Long.MIN_VALUE);
  }

  private void setCaches() {
    log.info("Set Caches...");
    addTaskToQueue(ConstantUtils.QUEUE_SET, ConstantUtils.TASK_SET_APP_DATA, AppDataUtils::setAppData, 1000);
    addTaskToQueue(ConstantUtils.QUEUE_SET, ConstantUtils.TASK_SET_GRADLE_DEPENDENCIES, gradleDependencyVersionService::getGradleDependenciesMap, Long.MIN_VALUE);
    addTaskToQueue(ConstantUtils.QUEUE_SET, ConstantUtils.TASK_SET_GRADLE_PLUGINS, gradlePluginVersionService::getGradlePluginsMap, Long.MIN_VALUE);
    addTaskToQueue(ConstantUtils.QUEUE_SET, ConstantUtils.TASK_SET_NODE_DEPENDENCIES, nodeDependencyVersionService::getNodeDependenciesMap, Long.MIN_VALUE);
    addTaskToQueue(ConstantUtils.QUEUE_SET, ConstantUtils.TASK_SET_PYTHON_PACKAGES, pythonPackageVersionService::getPythonPackagesMap, Long.MIN_VALUE);
    addTaskToQueue(ConstantUtils.QUEUE_SET, ConstantUtils.TASK_SET_EXCLUDED_REPOS, excludedRepoService::getExcludedReposMap, Long.MIN_VALUE);
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
    addTaskToQueue(ConstantUtils.QUEUE_FILES, ConstantUtils.TASK_DELETE_SCRIPT_FILES, scriptUtils::deleteTempScriptFiles, Long.MIN_VALUE);
    addTaskToQueue(ConstantUtils.QUEUE_FILES, ConstantUtils.TASK_CREATE_SCRIPT_FILES, scriptUtils::createTempScriptFiles, 1000);
  }

  private void updateInit(final RequestMetadata requestMetadata) {
    resetProcessedSummaries();

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

  private void updateExit(final RequestMetadata requestMetadata,
                          final boolean isPrCreateRequired,
                          final boolean isPrMergeRequired,
                          final boolean isExitRequired) {
    // TODO
    if (isPrCreateRequired) {
      executeUpdateCreatePullRequests(requestMetadata);
    }
    if (isPrMergeRequired) {
      executeUpdateMergePullRequests(requestMetadata);
    }
    if (isExitRequired) {
      executeUpdateDependencies(requestMetadata, Boolean.TRUE);
    }
  }

  private void executeNpmSnapshotsUpdate(final LocalDate branchDate, final String repoName) {
    log.info("Execute Npm Snapshots: [{}] | [{}]", branchDate, repoName);
    final AppData appData = AppDataUtils.appData();
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
        ConstantUtils.TASK_NPM_SNAPSHOTS + "_QUEUE",
        ConstantUtils.TASK_NPM_SNAPSHOTS + "_TASK",
        () -> new UpdateNpmSnapshots(repositories, scriptFile, branchName).execute(),
        Long.MIN_VALUE);
  }

  private void executeGradleSpotlessUpdate(final LocalDate branchDate, final String repoName) {
    log.info("Execute Gradle Spotless Update: [{}] | [{}]", branchDate, repoName);
    final AppData appData = AppDataUtils.appData();
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
            ConstantUtils.TASK_GRADLE_SPOTLESS + "_QUEUE",
        ConstantUtils.TASK_GRADLE_SPOTLESS + "_TASK",
        () -> new UpdateGradleSpotless(repositories, scriptFile, branchName).execute()
            , Long.MIN_VALUE);
  }

  private void executeGithubBranchDelete(final boolean isDeleteUpdateDependenciesOnly, final String repoName) {
    log.info(
        "Execute Update Repos GitHub Branch Delete: [{}] | [{}]",
        isDeleteUpdateDependenciesOnly,
        repoName);
    final AppData appData = AppDataUtils.appData();

    if (CommonUtilities.isEmpty(repoName)) {
      final String repoHome = appData.getArgsMap().get(ConstantUtils.ENV_REPO_HOME);
      final AppDataScriptFile scriptFile =
          appData.getScriptFiles().stream()
              .filter(script -> script.getScriptName().equals(ConstantUtils.SCRIPT_DELETE))
              .findFirst()
              .orElseThrow(
                  () -> new IllegalStateException("Gradle Spotless Script File Not Found"));

      addTaskToQueue(
          ConstantUtils.TASK_GITHUB_BRANCH_DELETE + "_QUEUE",
          ConstantUtils.TASK_GITHUB_BRANCH_DELETE + "_TASK",
          () ->
              new UpdateBranchDelete(repoHome, scriptFile, isDeleteUpdateDependenciesOnly)
                  .execute(), Long.MIN_VALUE);
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
              ConstantUtils.TASK_GITHUB_BRANCH_DELETE + "_QUEUE",
          ConstantUtils.TASK_GITHUB_BRANCH_DELETE + "_TASK",
          () ->
              new UpdateBranchDelete(repository, scriptFile, isDeleteUpdateDependenciesOnly)
                  .execute(), Long.MIN_VALUE);
    }
  }

  private void executeUpdateGithubResetPull(final boolean isReset, final boolean isPull, final String repoName) {
    log.info("Execute Update GitHub Reset Pull: [{}] | [{}] | [{}]", isReset, isPull, repoName);
    final AppData appData = AppDataUtils.appData();

    if (CommonUtilities.isEmpty(repoName)) {
      final String repoHome = appData.getArgsMap().get(ConstantUtils.ENV_REPO_HOME);
      final AppDataScriptFile scriptFile =
          appData.getScriptFiles().stream()
              .filter(script -> script.getScriptName().equals(ConstantUtils.SCRIPT_RESET_PULL))
              .findFirst()
              .orElseThrow(() -> new IllegalStateException("GitHub Reset Pull Script Not Found"));

      addTaskToQueue(
          ConstantUtils.TASK_GITHUB_RESET_PULL + "_QUEUE",
          ConstantUtils.TASK_GITHUB_RESET_PULL + "_TASK",
          () -> new UpdateRepoResetPull(repoHome, scriptFile, isReset, isPull).execute(), Long.MIN_VALUE);
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
              ConstantUtils.TASK_GITHUB_RESET_PULL + "_QUEUE",
              ConstantUtils.TASK_GITHUB_RESET_PULL + "_TASK",
          () -> new UpdateRepoResetPull(repository, scriptFile, isReset, isPull).execute(), Long.MIN_VALUE);
    }
  }

  private void executeUpdateDependencies(final RequestMetadata requestMetadata, final boolean isExit) {
    log.info("Execute Update Dependencies: [{}]", requestMetadata);
    final AppData appData = AppDataUtils.appData();
    final String repoName = requestMetadata.getRepoName();

    final List<AppDataRepository> repositories = appData.getRepositories().stream().filter(repository -> CommonUtilities.isEmpty(repoName) || repoName.equals(repository.getRepoName())).toList();
    if (!CommonUtilities.isEmpty(repoName) && CommonUtilities.isEmpty(repositories)) {
      throw new IllegalArgumentException("Repo Not Found by Repo Name ['" + repoName + "']");
    }

    final AppDataScriptFile scriptFileInit = appData.getScriptFiles().stream()
            .filter(script -> script.getScriptName().equals(ConstantUtils.SCRIPT_UPDATE_INIT))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Update Dependencies Init/Exit Script Not Found"));
    final AppDataScriptFile scriptFileExec = appData.getScriptFiles().stream()
            .filter(script -> script.getScriptName().equals(ConstantUtils.SCRIPT_UPDATE_EXEC))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Update Dependencies Execute Script Not Found"));

    if (isExit) {
      repositories.forEach(repository -> executeUpdateDependenciesInitExit(repository, scriptFileInit, Boolean.FALSE));
    } else {
      repositories.forEach(repository -> executeUpdateDependenciesInitExit(repository, scriptFileInit, Boolean.TRUE));
      repositories.forEach(repository -> executeUpdateDependenciesExec(repository, scriptFileExec, requestMetadata.getBranchDate()));
    }
  }

  private void executeUpdateCreatePullRequests(final RequestMetadata requestMetadata) {
    // repoName is mandatory, enforced in controller
    // if repoName is given:
    // 1. lookup branches
    // 2. most recent update_dependencies branch, create pull request
    // if repoName not given
    // look up processedRepositories where new branch was pushed
  }

  private void executeUpdateMergePullRequests(final RequestMetadata requestMetadata) {
    // repoName is mandatory, enforced in controller
    // if repoName is given
    // 1. lookup pull requests
    // 2. most recent pull request which is open, merge it
    // if repoName not given
    // look up processedRepositories
  }

  private String getUpdateDependenciesQueueName(final String appender) {
    return String.format(ConstantUtils.QUEUE_UPDATE_DEPENDENCIES, appender);
  }

  private String getUpdateDependenciesTaskName(final String repoName, final String appender) {
    return String.format(ConstantUtils.TASK_UPDATE_DEPENDENCIES, repoName, appender);
  }

  private void executeUpdateDependenciesInitExit(final AppDataRepository repository, final AppDataScriptFile scriptFile, final boolean isInit) {
    log.info("Execute Update Dependencies Init/Exit: [{}] | [{}]", repository.getRepoName(), scriptFile.getScriptName());
    addTaskToQueue(
            getUpdateDependenciesQueueName(isInit ? "INIT" : "EXIT"),
            getUpdateDependenciesTaskName(repository.getRepoName(), isInit ? "INIT" : "EXIT"),
            () -> new UpdateDependencies(repository, scriptFile, isInit).execute(), Long.MIN_VALUE);
  }

  private void executeUpdateDependenciesExec(final AppDataRepository repository, final AppDataScriptFile scriptFile, final LocalDate branchDate) {
    log.info("Execute Update Dependencies: [{}] | [{}] | [{}]", repository.getRepoName(), scriptFile.getScriptName(), branchDate);
    final String branchName = String.format(ConstantUtils.BRANCH_UPDATE_DEPENDENCIES, branchDate);
    addTaskToQueue(
            getUpdateDependenciesQueueName("EXEC"),
            getUpdateDependenciesTaskName(repository.getRepoName(), "EXEC"),
            () -> new UpdateDependencies(repository, scriptFile, branchName).execute(), Long.MIN_VALUE);
  }

  public void addTaskToQueue(final String queueName, final String taskName, final Runnable action, final long delayMillis) {
    TaskQueues.TaskQueue taskQueue = taskQueues.getQueueByName(queueName);
    if (taskQueue == null) {
      taskQueue = new TaskQueues.TaskQueue(queueName);
      taskQueue.addTask(new TaskQueues.TaskQueue.OneTask(taskName, action, delayMillis));
      taskQueues.addQueue(taskQueue);
    } else {
      taskQueue.addTask(new TaskQueues.TaskQueue.OneTask(taskName, action, delayMillis));
    }
  }

  private void resetProcessedSummaries() {
    addTaskToQueue(
        ConstantUtils.TASK_RESET_PROCESS_SUMMARIES + "_QUEUE",
        ConstantUtils.TASK_RESET_PROCESS_SUMMARIES + "_TASK",
        ProcessUtils::resetProcessedRepositoriesAndSummary, Long.MIN_VALUE);
  }

  // TODO create process summary
}
