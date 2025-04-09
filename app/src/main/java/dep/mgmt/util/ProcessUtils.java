package dep.mgmt.util;

import dep.mgmt.model.ProcessSummaries;
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

  private static ConcurrentMap<String, ProcessSummaries.ProcessSummary.ProcessRepository> processedRepositories = new ConcurrentHashMap<>();

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

  public static int getNodeDependenciesToUpdate() {
    return mongoNodeDependenciesToUpdate.get();
  }

  public static void addProcessedRepositories(final String repoName, final String repoType, boolean isUpdateBranchCreated) {
    processedRepositories.put(
        repoName,
        new ProcessSummaries.ProcessSummary.ProcessRepository(repoName, repoType, isUpdateBranchCreated));
  }

  public static void updateProcessedRepositoriesPrCreated(final String repoName, final Integer prNumber) {
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
              processedRepository.setPrCreated(true);
              return processedRepository;
            });
  }

  public static ConcurrentMap<String, ProcessSummaries.ProcessSummary.ProcessRepository> getProcessedRepositoriesMap() {
    return processedRepositories;
  }

  public static void resetProcessedRepositoriesAndSummary() {
    processedRepositories = new ConcurrentHashMap<>();
    setMongoGradlePluginsToUpdate(0);
    setMongoGradleDependenciesToUpdate(0);
    setMongoPythonPackagesToUpdate(0);
    setMongoNodeDependenciesToUpdate(0);
    setErrorsOrExceptions(false);
  }
}
