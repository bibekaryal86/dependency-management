package dep.mgmt.util;

import dep.mgmt.model.Dependencies;
import dep.mgmt.model.ExcludedRepos;
import dep.mgmt.model.LogEntry;
import dep.mgmt.model.ProcessSummaries;
import dep.mgmt.model.entity.DependencyEntity;
import dep.mgmt.model.entity.ExcludedRepoEntity;
import dep.mgmt.model.entity.LogEntryEntity;
import dep.mgmt.model.entity.ProcessSummaryEntity;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import java.util.Collections;
import java.util.List;

public class ConvertUtils {

  public static List<Dependencies.Dependency> convertDependencyEntities(
      final List<DependencyEntity> dependencyEntities) {
    if (CommonUtilities.isEmpty(dependencyEntities)) {
      return Collections.emptyList();
    }
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
    if (CommonUtilities.isEmpty(processSummaryEntities)) {
      return Collections.emptyList();
    }
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
    if (CommonUtilities.isEmpty(processRepositoryEntities)) {
      return Collections.emptyList();
    }
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
    if (CommonUtilities.isEmpty(processTaskEntities)) {
      return Collections.emptyList();
    }
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
    if (processSummary == null) {
      return null;
    }
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
    if (CommonUtilities.isEmpty(processRepositories)) {
      return Collections.emptyList();
    }
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
    if (CommonUtilities.isEmpty(processTasks)) {
      return Collections.emptyList();
    }
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
    if (CommonUtilities.isEmpty(excludedRepoEntities)) {
      return Collections.emptyList();
    }
    return excludedRepoEntities.stream()
        .map(excludedRepoEntity -> new ExcludedRepos.ExcludedRepo(excludedRepoEntity.getName()))
        .toList();
  }

  public static List<LogEntry> convertLogEntryEntities(
      final List<LogEntryEntity> logEntryEntities) {
    if (CommonUtilities.isEmpty(logEntryEntities)) {
      return Collections.emptyList();
    }
    return logEntryEntities.stream()
        .map(
            logEntryEntity ->
                new LogEntry(logEntryEntity.getUpdateDateTime(), logEntryEntity.getLogEntries()))
        .toList();
  }
}
