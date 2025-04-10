package dep.mgmt.util;

import dep.mgmt.model.Dependencies;
import dep.mgmt.model.ExcludedRepos;
import dep.mgmt.model.ProcessSummaries;
import dep.mgmt.model.entity.DependencyEntity;
import dep.mgmt.model.entity.ExcludedRepoEntity;
import dep.mgmt.model.entity.ProcessSummaryEntity;
import java.util.List;

public class ConvertUtils {

  public static List<Dependencies.Dependency> convertDependencyEntities(
      final List<DependencyEntity> dependencyEntities) {
    return dependencyEntities.stream()
        .map(
            dependencyEntity ->
                new Dependencies.Dependency(
                    dependencyEntity.getName(),
                    dependencyEntity.getVersion(),
                    dependencyEntity.getSkipVersion()))
        .toList();
  }

  public static List<ProcessSummaries.ProcessSummary> convertProcessSummaryEntities(
      final List<ProcessSummaryEntity> processSummaryEntities) {
    return processSummaryEntities.stream()
        .map(
            processSummaryEntity ->
                new ProcessSummaries.ProcessSummary(
                    processSummaryEntity.getUpdateDateTime(),
                    processSummaryEntity.getUpdateType(),
                    processSummaryEntity.getGradlePluginsToUpdate(),
                    processSummaryEntity.getGradleDependenciesToUpdate(),
                    processSummaryEntity.getPythonPackagesToUpdate(),
                    processSummaryEntity.getNodeDependenciesToUpdate(),
                    processSummaryEntity.getTotalPrCreatedCount(),
                    processSummaryEntity.getTotalPrMergedCount(),
                    processSummaryEntity.getTotalPrMergeErrorCount(),
                    convertProcessSummaryRepositoryEntities(
                        processSummaryEntity.getProcessRepositories()),
                    processSummaryEntity.getErrorsOrExceptions(),
                    convertProcessSummaryTaskEntities(processSummaryEntity.getProcessTasks())))
        .toList();
  }

  private static List<ProcessSummaries.ProcessSummary.ProcessRepository>
      convertProcessSummaryRepositoryEntities(
          final List<ProcessSummaryEntity.ProcessRepositoryEntity> processRepositoryEntities) {
    return processRepositoryEntities.stream()
        .map(
            processRepositoryEntity ->
                new ProcessSummaries.ProcessSummary.ProcessRepository(
                    processRepositoryEntity.getRepoName(),
                    processRepositoryEntity.getRepoType(),
                    processRepositoryEntity.getUpdateBranchCreated(),
                    processRepositoryEntity.getPrCreated(),
                    processRepositoryEntity.getPrMerged(),
                    processRepositoryEntity.getPrNumber()))
        .toList();
  }

  private static List<ProcessSummaries.ProcessSummary.ProcessTask>
      convertProcessSummaryTaskEntities(
          final List<ProcessSummaryEntity.ProcessTaskEntity> processTaskEntities) {
    return processTaskEntities.stream()
        .map(
            processTaskEntity ->
                new ProcessSummaries.ProcessSummary.ProcessTask(
                    processTaskEntity.getQueueName(),
                    processTaskEntity.getTaskName(),
                    processTaskEntity.getAdded(),
                    processTaskEntity.getStarted(),
                    processTaskEntity.getEnded()))
        .toList();
  }

  public static ProcessSummaryEntity convertProcessSummary(
      final ProcessSummaries.ProcessSummary processSummary) {
    return new ProcessSummaryEntity(
        null,
        processSummary.getUpdateDateTime(),
        processSummary.getUpdateType(),
        processSummary.getGradlePluginsToUpdate(),
        processSummary.getGradleDependenciesToUpdate(),
        processSummary.getPythonPackagesToUpdate(),
        processSummary.getNodeDependenciesToUpdate(),
        processSummary.getTotalPrCreatedCount(),
        processSummary.getTotalPrMergedCount(),
        processSummary.getTotalPrMergeErrorsCount(),
        convertProcessSummaryRepositories(processSummary.getProcessRepositories()),
        processSummary.getErrorsOrExceptions(),
        convertProcessSummaryTasks(processSummary.getProcessTasks()));
  }

  private static List<ProcessSummaryEntity.ProcessRepositoryEntity>
      convertProcessSummaryRepositories(
          final List<ProcessSummaries.ProcessSummary.ProcessRepository> processRepositories) {
    return processRepositories.stream()
        .map(
            processRepository ->
                new ProcessSummaryEntity.ProcessRepositoryEntity(
                    processRepository.getRepoName(),
                    processRepository.getRepoType(),
                    processRepository.getUpdateBranchCreated(),
                    processRepository.getPrCreated(),
                    processRepository.getPrMerged(),
                    processRepository.getPrNumber()))
        .toList();
  }

  private static List<ProcessSummaryEntity.ProcessTaskEntity> convertProcessSummaryTasks(
      final List<ProcessSummaries.ProcessSummary.ProcessTask> processTasks) {
    return processTasks.stream()
        .map(
            processTask ->
                new ProcessSummaryEntity.ProcessTaskEntity(
                    processTask.getQueueName(),
                    processTask.getTaskName(),
                    processTask.getAdded(),
                    processTask.getStarted(),
                    processTask.getEnded()))
        .toList();
  }

  public static List<ExcludedRepos.ExcludedRepo> convertExcludedRepoEntities(
      final List<ExcludedRepoEntity> excludedRepoEntities) {
    return excludedRepoEntities.stream()
        .map(excludedRepoEntity -> new ExcludedRepos.ExcludedRepo(excludedRepoEntity.getName()))
        .toList();
  }
}
