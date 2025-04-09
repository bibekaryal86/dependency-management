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
                    processSummaryEntity.getTotalPrCreateErrorsCount(),
                    processSummaryEntity.getTotalPrMergedCount(),
                    convertProcessSummaryRepositoryEntities(
                        processSummaryEntity.getProcessRepositories()),
                    processSummaryEntity.getErrorsOrExceptions()))
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
                    processRepositoryEntity.getPrMerged()))
        .toList();
  }

  public static List<ExcludedRepos.ExcludedRepo> convertExcludedRepoEntities(
      final List<ExcludedRepoEntity> excludedRepoEntities) {
    return excludedRepoEntities.stream()
        .map(excludedRepoEntity -> new ExcludedRepos.ExcludedRepo(excludedRepoEntity.getName()))
        .toList();
  }
}
