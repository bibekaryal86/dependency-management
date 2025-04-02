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
import dep.mgmt.util.LogCaptureUtils;
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
  private final ScriptUtils scriptUtils;

  public UpdateManagerService() {
    this.gradleDependencyVersionService = new GradleDependencyVersionService();
    this.gradlePluginVersionService = new GradlePluginVersionService();
    this.nodeDependencyVersionService = new NodeDependencyVersionService();
    this.pythonPackageVersionService = new PythonPackageVersionService();
    this.scriptUtils = new ScriptUtils();
  }

  public void executeTaskQueues() {
    if (!taskQueues.isProcessing()) {
      taskQueues.processQueues();
    }
  }

  public void scheduledUpdates() {

  }

  public void updateNpmSnapshots(final RequestMetadata requestMetadata) {
    updateInit(requestMetadata);
    executeNpmSnapshotsUpdate(requestMetadata.getBranchDate(), requestMetadata.getRepoName());
    executeTaskQueues();
  }

  public void updateGradleSpotless(final RequestMetadata requestMetadata) {
    updateInit(requestMetadata);
    executeGradleSpotlessUpdate(requestMetadata.getBranchDate(), requestMetadata.getRepoName());
    executeTaskQueues();
  }

  public void updateGithubBranchDelete(final RequestMetadata requestMetadata) {
    updateInit(requestMetadata);
    executeGithubBranchDelete(requestMetadata.getDeleteUpdateDependenciesOnly(), requestMetadata.getRepoName());
    executeTaskQueues();
  }

  public void updateGithubPullReset(final RequestMetadata requestMetadata) {
    updateInit(requestMetadata);
    executeUpdateGithubResetPull(requestMetadata.getGithubResetRequired(), requestMetadata.getIsGithubPullRequired(), requestMetadata.getRepoName());
    executeTaskQueues();
  }

  public void recreateCaches() {
    // reset
    addTaskToQueue(ConstantUtils.TASK_RESET_APP_DATA, CacheConfig::resetAppData);
    addTaskToQueue(ConstantUtils.TASK_RESET_GRADLE_DEPENDENCIES, CacheConfig::resetGradleDependenciesMap);
    addTaskToQueue(ConstantUtils.TASK_RESET_GRADLE_PLUGINS, CacheConfig::resetGradlePluginsMap);
    addTaskToQueue(ConstantUtils.TASK_RESET_NODE_DEPENDENCIES, CacheConfig::resetNodeDependenciesMap);
    addTaskToQueue(ConstantUtils.TASK_RESET_PYTHON_PACKAGES, CacheConfig::resetPythonPackagesMap);

    // set
    addTaskToQueue(ConstantUtils.TASK_SET_APP_DATA, AppDataUtils::setAppData, 1000);
    addTaskToQueue(ConstantUtils.TASK_SET_GRADLE_DEPENDENCIES, gradleDependencyVersionService::getGradleDependenciesMap);
    addTaskToQueue(ConstantUtils.TASK_SET_GRADLE_PLUGINS, gradlePluginVersionService::getGradlePluginsMap);
    addTaskToQueue(ConstantUtils.TASK_SET_NODE_DEPENDENCIES, nodeDependencyVersionService::getNodeDependenciesMap);
    addTaskToQueue(ConstantUtils.TASK_SET_PYTHON_PACKAGES, pythonPackageVersionService::getPythonPackagesMap);

    executeTaskQueues();
  }

  private void recreateScriptFiles() {
    addTaskToQueue(ConstantUtils.TASK_DELETE_SCRIPT_FILES, scriptUtils::deleteTempScriptFiles);
    addTaskToQueue(ConstantUtils.TASK_CREATE_SCRIPT_FILES, scriptUtils::createTempScriptFiles);
  }

  private void updateInit(final RequestMetadata requestMetadata) {
    resetProcessedSummaries();

    if (requestMetadata.getRecreateCaches()) {
      recreateCaches();
    }

    if (requestMetadata.getRecreateScriptFiles() || scriptUtils.isScriptFilesMissingInFileSystem()) {
      recreateScriptFiles();
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

    addTaskToQueue(ConstantUtils.TASK_NPM_SNAPSHOTS, () -> new UpdateNpmSnapshots(repositories, scriptFile, branchName).execute());
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

    addTaskToQueue(ConstantUtils.TASK_GRADLE_SPOTLESS, () -> new UpdateGradleSpotless(repositories, scriptFile, branchName).execute());
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

      addTaskToQueue(ConstantUtils.TASK_GITHUB_BRANCH_DELETE, () -> new UpdateBranchDelete(repoHome, scriptFile, isDeleteUpdateDependenciesOnly).execute());
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

      addTaskToQueue(ConstantUtils.TASK_GITHUB_BRANCH_DELETE, () -> new UpdateBranchDelete(repository, scriptFile, isDeleteUpdateDependenciesOnly).execute());
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

      addTaskToQueue(ConstantUtils.TASK_GITHUB_RESET_PULL, () -> new UpdateRepoResetPull(repoHome, scriptFile, isReset, isPull).execute());
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

      addTaskToQueue(ConstantUtils.TASK_GITHUB_RESET_PULL, () -> new UpdateRepoResetPull(repository, scriptFile, isReset, isPull).execute());
    }
  }

  private void executeUpdateDependencies(final String repoName, final String branchName, final boolean isInit, final boolean isExit) {
    log.info("Execute Update Dependencies: [{}] | [{}] | [{}] | [{}]", repoName, branchName, isInit, isExit);
    final AppData appData = AppDataUtils.appData();
    final AppDataRepository repository =
            appData.getRepositories().stream()
                    .filter(repo -> repo.getRepoName().equals(repoName))
                    .findFirst()
                    .orElseThrow(
                            () ->
                                    new IllegalArgumentException(
                                            "Repo Not Found by Repo Name ['" + repoName + "']"));
    final AppDataScriptFile scriptFile;
    final boolean isInitOrExit = isInit || isExit;
    if (isInitOrExit) {
      scriptFile =
              appData.getScriptFiles().stream()
                      .filter(script -> script.getScriptName().equals(ConstantUtils.SCRIPT_UPDATE_INIT))
                      .findFirst()
                      .orElseThrow(
                              () -> new IllegalStateException("Update Dependencies Init/Exit Script Not Found"));
      new UpdateDependencies(repository, scriptFile, isInit).execute();
    } else {
      scriptFile = appData.getScriptFiles().stream()
              .filter(script -> script.getScriptName().equals(ConstantUtils.SCRIPT_UPDATE_EXEC))
              .findFirst()
              .orElseThrow(
                      () -> new IllegalStateException("Update Dependencies Execute Script Not Found"));
      new UpdateDependencies(repository, scriptFile, branchName).execute();
    }
  }

  private void addTaskToQueue(final String name, final Runnable action) {
    TaskQueues.TaskQueue taskQueue = new TaskQueues.TaskQueue(name + "_QUEUE");
    taskQueue.addTask(new TaskQueues.TaskQueue.OneTask(name + "_TASK", action));
    taskQueues.addQueue(taskQueue);
  }

  private void addTaskToQueue(final String name, final Runnable action, final long delayMillis) {
    TaskQueues.TaskQueue taskQueue = new TaskQueues.TaskQueue(name + "_QUEUE");
    taskQueue.addTask(new TaskQueues.TaskQueue.OneTask(name + "_TASK", action));
    if (delayMillis > 0) {
      taskQueue.setDelay(delayMillis);
    }
    taskQueues.addQueue(taskQueue);
  }

  private void resetProcessedSummaries() {
    addTaskToQueue(ConstantUtils.TASK_RESET_PROCESS_SUMMARIES, ProcessUtils::resetProcessedRepositoriesAndSummary);
  }

  // TODO create process summary
}
