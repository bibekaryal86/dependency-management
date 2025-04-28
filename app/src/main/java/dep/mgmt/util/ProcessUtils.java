package dep.mgmt.util;

import dep.mgmt.model.AppDataRepository;
import dep.mgmt.model.ProcessSummaries;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ProcessUtils {

  private static final AtomicBoolean errorsOrExceptions = new AtomicBoolean(false);
  private static final AtomicInteger mongoGradlePluginsToUpdate = new AtomicInteger(0);
  private static final AtomicInteger mongoGradleDependenciesToUpdate = new AtomicInteger(0);
  private static final AtomicInteger mongoPythonPackagesToUpdate = new AtomicInteger(0);
  private static final AtomicInteger mongoNodeDependenciesToUpdate = new AtomicInteger(0);

  private static ConcurrentMap<String, ProcessSummaries.ProcessSummary.ProcessTask> processedTasks =
      new ConcurrentHashMap<>();
  private static ConcurrentMap<String, ProcessSummaries.ProcessSummary.ProcessRepository>
      processedRepositories = new ConcurrentHashMap<>();
  private static Set<String> repositoriesToRetryMerge = new HashSet<>();

  public static void setErrorsOrExceptions(boolean value) {
    errorsOrExceptions.set(value);
  }

  public static void setMongoGradlePluginsToUpdate(int count) {
    mongoGradlePluginsToUpdate.set(count);
  }

  public static void setMongoGradleDependenciesToUpdate(int count) {
    mongoGradleDependenciesToUpdate.set(count);
  }

  public static void setMongoPythonPackagesToUpdate(int count) {
    mongoPythonPackagesToUpdate.set(count);
  }

  public static void setMongoNodeDependenciesToUpdate(int count) {
    mongoNodeDependenciesToUpdate.set(count);
  }

  public static boolean getErrorsOrExceptions() {
    return errorsOrExceptions.get();
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

  public static synchronized void addRepositoriesToRetryMerge(final String repository) {
    repositoriesToRetryMerge.add(repository);
  }

  public static synchronized void removeRepositoriesToRetryMerge(final String repository) {
    repositoriesToRetryMerge.remove(repository);
  }

  public static synchronized Set<String> getRepositoriesToRetryMerge() {
    return repositoriesToRetryMerge;
  }

  public static void addProcessedTasks(final String queueName, final String taskName) {
    processedTasks.put(
        taskName,
        new ProcessSummaries.ProcessSummary.ProcessTask(queueName, taskName, LocalDateTime.now()));
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

  public static ConcurrentMap<String, ProcessSummaries.ProcessSummary.ProcessTask>
      getProcessedTasks() {
    return processedTasks;
  }

  public static void resetProcessedRepositoriesAndSummary() {
    processedRepositories = new ConcurrentHashMap<>();
    repositoriesToRetryMerge = new HashSet<>();
    processedTasks = new ConcurrentHashMap<>();
    setMongoGradlePluginsToUpdate(0);
    setMongoGradleDependenciesToUpdate(0);
    setMongoPythonPackagesToUpdate(0);
    setMongoNodeDependenciesToUpdate(0);
    setErrorsOrExceptions(false);
  }

  public static boolean isRepoUpdateBranchCreatedCheck(final String repoName) {
    return getProcessedRepositoriesMap().values().stream()
        .anyMatch(
            processRepository ->
                processRepository.getRepoName().equals(repoName)
                    && processRepository.getUpdateBranchCreated());
  }

  public static boolean isRepoPrCreatedCheck(final String repoName) {
    return getProcessedRepositoriesMap().values().stream()
        .anyMatch(
            processRepository ->
                processRepository.getRepoName().equals(repoName)
                    && processRepository.getPrCreated());
  }

  public static boolean isRepoPrMergedCheck(final AppDataRepository repository) {
    return getProcessedRepositoriesMap().values().stream()
        .anyMatch(
            processRepository ->
                processRepository.getRepoName().equals(repository.getRepoName())
                    && processRepository.getPrMerged());
  }
}
