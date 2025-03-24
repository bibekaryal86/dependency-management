package dep.mgmt.util;

import dep.mgmt.model.Dependency;
import dep.mgmt.model.ProcessSummaries;
import dep.mgmt.model.entity.DependencyEntity;
import dep.mgmt.model.entity.ProcessSummaryEntity;
import java.util.List;

public class ConvertUtils {

  public static List<Dependency> convertDependencyEntities(
      final List<DependencyEntity> dependencyEntities) {
    return dependencyEntities.stream()
        .map(
            dependencyEntity ->
                new Dependency(
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
                    processRepositoryEntity.getPrCreated(),
                    processRepositoryEntity.getPrCreateError(),
                    processRepositoryEntity.getRepoType(),
                    processRepositoryEntity.getPrMerged()))
        .toList();
  }
}
