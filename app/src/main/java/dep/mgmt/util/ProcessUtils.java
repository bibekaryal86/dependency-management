package dep.mgmt.util;

import dep.mgmt.model.ProcessSummaries;
import dep.mgmt.model.TaskQueues;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ProcessUtils {

  public static final TaskQueues TASK_QUEUES;

  static {
    TASK_QUEUES = new TaskQueues();
  }

  private static final AtomicBoolean errorsOrExceptions = new AtomicBoolean(Boolean.FALSE);
  private static final AtomicBoolean isFirstPullRequestMerge = new AtomicBoolean(Boolean.TRUE);

  private static final AtomicInteger mongoGradlePluginsChecked = new AtomicInteger(0);
  private static final AtomicInteger mongoGradleDependenciesChecked = new AtomicInteger(0);
  private static final AtomicInteger mongoPythonPackagesChecked = new AtomicInteger(0);
  private static final AtomicInteger mongoNodeDependenciesChecked = new AtomicInteger(0);

  private static final AtomicInteger mongoGradlePluginsToUpdate = new AtomicInteger(0);
  private static final AtomicInteger mongoGradleDependenciesToUpdate = new AtomicInteger(0);
  private static final AtomicInteger mongoPythonPackagesToUpdate = new AtomicInteger(0);
  private static final AtomicInteger mongoNodeDependenciesToUpdate = new AtomicInteger(0);

  private static ConcurrentMap<String, ProcessSummaries.ProcessSummary.ProcessTask> processedTasks =
      new ConcurrentHashMap<>();
  private static ConcurrentMap<String, ProcessSummaries.ProcessSummary.ProcessRepository>
      processedRepositories = new ConcurrentHashMap<>();

  public static void setErrorsOrExceptions(final boolean value) {
    errorsOrExceptions.set(value);
  }

  public static void setIsFirstPullRequestMerge(final boolean value) {
    isFirstPullRequestMerge.set(value);
  }

  public static void setMongoGradlePluginsChecked(final int count) {
    mongoGradlePluginsChecked.set(count);
  }

  public static void setMongoGradleDependenciesChecked(final int count) {
    mongoGradleDependenciesChecked.set(count);
  }

  public static void setMongoPythonPackagesChecked(final int count) {
    mongoPythonPackagesChecked.set(count);
  }

  public static void setMongoNodeDependenciesChecked(final int count) {
    mongoNodeDependenciesChecked.set(count);
  }

  public static void setMongoGradlePluginsToUpdate(final int count) {
    mongoGradlePluginsToUpdate.set(count);
  }

  public static void setMongoGradleDependenciesToUpdate(final int count) {
    mongoGradleDependenciesToUpdate.set(count);
  }

  public static void setMongoPythonPackagesToUpdate(final int count) {
    mongoPythonPackagesToUpdate.set(count);
  }

  public static void setMongoNodeDependenciesToUpdate(final int count) {
    mongoNodeDependenciesToUpdate.set(count);
  }

  public static boolean getErrorsOrExceptions() {
    return errorsOrExceptions.get();
  }

  public static int getMongoGradlePluginsChecked() {
    return mongoGradlePluginsChecked.get();
  }

  public static int getMongoGradleDependenciesChecked() {
    return mongoGradleDependenciesChecked.get();
  }

  public static int getMongoPythonPackagesChecked() {
    return mongoPythonPackagesChecked.get();
  }

  public static int getMongoNodeDependenciesChecked() {
    return mongoNodeDependenciesChecked.get();
  }

  public static int getMongoGradlePluginsToUpdate() {
    return mongoGradlePluginsToUpdate.get();
  }

  public static int getMongoGradleDependenciesToUpdate() {
    return mongoGradleDependenciesToUpdate.get();
  }

  public static int getMongoPythonPackagesToUpdate() {
    return mongoPythonPackagesToUpdate.get();
  }

  public static int getMongoNodeDependenciesToUpdate() {
    return mongoNodeDependenciesToUpdate.get();
  }

  public static void addProcessedRepositories(
      final String repoName, final String repoType, boolean isUpdateBranchCreated) {
    processedRepositories.put(
        repoName,
        new ProcessSummaries.ProcessSummary.ProcessRepository(
            repoName, repoType, isUpdateBranchCreated));
    if (isUpdateBranchCreated) {
      updatePrCreateDelay(repoName);
    }
  }

  public static void updateProcessedRepositoriesPrCreated(
      final String repoName, final Integer prNumber) {
    processedRepositories.computeIfPresent(
        repoName,
        (key, processedRepository) -> {
          processedRepository.setPrCreated(true);
          processedRepository.setPrNumber(prNumber);
          return processedRepository;
        });
    updatePrMergeDelay(repoName);
  }

  public static void updateProcessedRepositoriesPrMerged(final String repoName) {
    processedRepositories.computeIfPresent(
        repoName,
        (key, processedRepository) -> {
          processedRepository.setPrMerged(true);
          return processedRepository;
        });
  }

  public static ConcurrentMap<String, ProcessSummaries.ProcessSummary.ProcessRepository>
      getProcessedRepositoriesMap() {
    return processedRepositories;
  }

  public static void addProcessedTasks(
      final String queueName, final String taskName, final long delayMillis) {
    processedTasks.put(
        taskName,
        new ProcessSummaries.ProcessSummary.ProcessTask(
            queueName, taskName, LocalDateTime.now(), delayMillis));
  }

  public static void updateProcessedTasksStarted(final String taskName) {
    processedTasks.computeIfPresent(
        taskName,
        (key, processedTask) -> {
          processedTask.setStarted(LocalDateTime.now());
          return processedTask;
        });
  }

  public static void updateProcessedTasksEnded(final String taskName) {
    processedTasks.computeIfPresent(
        taskName,
        (key, processedTask) -> {
          processedTask.setEnded(LocalDateTime.now());
          return processedTask;
        });
  }

  public static void updateProcessedTasksTimedOut(final String taskName) {
    processedTasks.computeIfPresent(
        taskName,
        (key, processedTask) -> {
          processedTask.setTimedOut(Boolean.TRUE);
          return processedTask;
        });
  }

  public static void updateProcessedTasksDelayMillis(
      final String taskName, final long newDelayMillis) {
    processedTasks.computeIfPresent(
        taskName,
        (key, processedTask) -> {
          processedTask.setDelayMills(newDelayMillis);
          return processedTask;
        });
  }

  public static ConcurrentMap<String, ProcessSummaries.ProcessSummary.ProcessTask>
      getProcessedTasks() {
    return processedTasks;
  }

  public static void resetProcessedRepositoriesAndSummary() {
    processedRepositories = new ConcurrentHashMap<>();
    processedTasks = new ConcurrentHashMap<>();
    setMongoGradlePluginsChecked(0);
    setMongoGradleDependenciesChecked(0);
    setMongoPythonPackagesChecked(0);
    setMongoNodeDependenciesChecked(0);
    setMongoGradlePluginsToUpdate(0);
    setMongoGradleDependenciesToUpdate(0);
    setMongoPythonPackagesToUpdate(0);
    setMongoNodeDependenciesToUpdate(0);
    setErrorsOrExceptions(Boolean.FALSE);
    setIsFirstPullRequestMerge(Boolean.TRUE);
  }

  public static ProcessSummaries.ProcessSummary.ProcessRepository getProcessedRepositoryFromMap(
      final String repoName) {
    return getProcessedRepositoriesMap().values().stream()
        .filter(processRepository -> repoName.equals(processRepository.getRepoName()))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("'" + repoName + "' Repository Not Found..."));
  }

  public static void updatePrCreateDelay(final String repoName) {
    final String queueName = ConstantUtils.QUEUE_PULL_REQUESTS_CREATE;
    final String taskName =
        String.format(ConstantUtils.TASK_PULL_REQUESTS_CREATE, repoName.toUpperCase());
    final long newDelayMillis = ConstantUtils.TASK_DELAY_PULL_REQUEST;
    TASK_QUEUES.updateOneTaskDelay(queueName, taskName, newDelayMillis);
  }

  public static void updatePrMergeDelay(final String repoName) {
    final String queueName = ConstantUtils.QUEUE_PULL_REQUESTS_MERGE;
    final String taskName =
        String.format(ConstantUtils.TASK_PULL_REQUESTS_MERGE, repoName.toUpperCase());
    final long newDelayMillis =
        isFirstPullRequestMerge.get()
            ? ConstantUtils.TASK_DELAY_PULL_REQUEST_TRY
            : ConstantUtils.TASK_DELAY_PULL_REQUEST;
    TASK_QUEUES.updateOneTaskDelay(queueName, taskName, newDelayMillis);
    setIsFirstPullRequestMerge(Boolean.FALSE);
  }
}
