package dep.mgmt.util;

import dep.mgmt.model.ProcessSummaries;
import dep.mgmt.model.TaskQueues;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProcessUtils {

  public static final TaskQueues TASK_QUEUES;

  static {
    TASK_QUEUES = new TaskQueues();
  }

  private static final AtomicBoolean isErrorsOrExceptions = new AtomicBoolean(Boolean.FALSE);
  private static final AtomicBoolean isFirstPullRequestMerge = new AtomicBoolean(Boolean.TRUE);

  private static ConcurrentMap<String, ProcessSummaries.ProcessSummary.ProcessTask> processedTasks =
      new ConcurrentHashMap<>();
  private static ConcurrentMap<String, ProcessSummaries.ProcessSummary.ProcessRepository>
      processedRepositories = new ConcurrentHashMap<>();
  private static CopyOnWriteArrayList<ProcessSummaries.ProcessSummary.ProcessDependency>
      processedDependencies = new CopyOnWriteArrayList<>();

  public static boolean getIsErrorsOrExceptions() {
    return isErrorsOrExceptions.get();
  }

  public static void setIsErrorsOrExceptions(final boolean value) {
    isErrorsOrExceptions.set(value);
  }

  public static void setIsFirstPullRequestMerge(final boolean value) {
    isFirstPullRequestMerge.set(value);
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

  public static void addProcessedDependencies(
      final List<ProcessSummaries.ProcessSummary.ProcessDependency> processDependencies) {
    processedDependencies.addAll(processDependencies);
  }

  public static void updateProcessedDependencies(
      ProcessSummaries.ProcessSummary.ProcessDependency dependency) {
    processedDependencies.addIfAbsent(dependency);
  }

  public static List<ProcessSummaries.ProcessSummary.ProcessDependency> getProcessedDependencies() {
    return processedDependencies.stream().toList();
  }

  public static void resetProcessedRepositoriesAndSummary() {
    processedRepositories = new ConcurrentHashMap<>();
    processedTasks = new ConcurrentHashMap<>();
    processedDependencies = new CopyOnWriteArrayList<>();
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
