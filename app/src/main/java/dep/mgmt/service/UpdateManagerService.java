package dep.mgmt.service;

import dep.mgmt.config.CacheConfig;
import dep.mgmt.model.AppData;
import dep.mgmt.model.AppDataRepository;
import dep.mgmt.model.AppDataScriptFile;
import dep.mgmt.model.TaskQueues;
import dep.mgmt.model.enums.RequestParams;
import dep.mgmt.update.UpdateBranchDelete;
import dep.mgmt.update.UpdateDependencies;
import dep.mgmt.update.UpdateGradleSpotless;
import dep.mgmt.update.UpdateNpmSnapshots;
import dep.mgmt.update.UpdateRepoResetPull;
import dep.mgmt.util.AppDataUtils;
import dep.mgmt.util.ConstantUtils;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
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

  public UpdateManagerService() {
    this.gradleDependencyVersionService = new GradleDependencyVersionService();
    this.gradlePluginVersionService = new GradlePluginVersionService();
    this.nodeDependencyVersionService = new NodeDependencyVersionService();
    this.pythonPackageVersionService = new PythonPackageVersionService();
  }

  public void executeTaskQueues() {
    if (!taskQueues.isProcessing()) {
      taskQueues.processQueues();
    }
  }

  public void scheduledUpdates() {

  }

  public void resetAllCaches() {
    addTaskToQueue("RESET_APP_DATA", CacheConfig::resetAppData);
    addTaskToQueue("RESET_GRADLE_DEPENDENCIES", CacheConfig::resetGradleDependenciesMap);
    addTaskToQueue("RESET_GRADLE_PLUGINS", CacheConfig::resetGradlePluginsMap);
    addTaskToQueue("RESET_NODE_DEPENDENCIES", CacheConfig::resetNodeDependenciesMap);
    addTaskToQueue("RESET_PYTHON_PACKAGES", CacheConfig::resetPythonPackagesMap);
  }

  public void setAllCaches() {
    addTaskToQueue("SET_APP_DATA", AppDataUtils::setAppData);
    addTaskToQueue("SET_GRADLE_DEPENDENCIES", gradleDependencyVersionService::getGradleDependenciesMap);
    addTaskToQueue("SET_GRADLE_PLUGINS", gradlePluginVersionService::getGradlePluginsMap);
    addTaskToQueue("SET_NODE_DEPENDENCIES", nodeDependencyVersionService::getNodeDependenciesMap);
    addTaskToQueue("SET_PYTHON_PACKAGES", pythonPackageVersionService::getPythonPackagesMap);
  }

  private void executeNpmSnapshotsUpdate(final String branchDate, final String repoName) {
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

    new UpdateNpmSnapshots(repositories, scriptFile, branchName).execute();
  }

  private void executeGradleSpotlessUpdate(final String branchDate, final String repoName) {
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

    new UpdateGradleSpotless(repositories, scriptFile, branchName).execute();
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
      new UpdateBranchDelete(repoHome, scriptFile, isDeleteUpdateDependenciesOnly).execute();
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
      new UpdateBranchDelete(repository, scriptFile, isDeleteUpdateDependenciesOnly).execute();
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
      new UpdateRepoResetPull(repoHome, scriptFile, isReset, isPull).execute();
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
      new UpdateRepoResetPull(repository, scriptFile, isReset, isPull).execute();
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

  // TODO create process summary
}
