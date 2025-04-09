package dep.mgmt.migration;

import dep.mgmt.migration.entities_old.Dependencies;
import dep.mgmt.migration.entities_old.LatestVersionsEntity;
import dep.mgmt.migration.entities_old.Packages;
import dep.mgmt.migration.entities_old.Plugins;
import dep.mgmt.migration.entities_old.ProcessSummaries;
import dep.mgmt.migration.entities_old.ProcessedRepository;
import dep.mgmt.model.entity.DependencyEntity;
import dep.mgmt.model.entity.LatestVersionEntity;
import dep.mgmt.model.entity.ProcessSummaryEntity;
import dep.mgmt.util.ConstantUtils;
import java.util.ArrayList;
import java.util.List;

public class MigrationService {

  private final MigrationRepository<ProcessSummaries> processSummaryRepoOld;
  private final MigrationRepository<ProcessSummaryEntity> processSummaryRepoNew;

  private final MigrationRepository<LatestVersionsEntity> latestVersionRepoOld;
  private final MigrationRepository<LatestVersionEntity> latestVersionRepoNew;

  private final MigrationRepository<Dependencies> gradleDependencyRepoOld;
  private final MigrationRepository<DependencyEntity> gradleDependencyRepoNew;

  private final MigrationRepository<Plugins> gradlePluginRepoOld;
  private final MigrationRepository<DependencyEntity> gradlePluginRepoNew;

  private final MigrationRepository<Packages> pythonPackageRepoOld;
  private final MigrationRepository<DependencyEntity> pythonPackageRepoNew;

  public MigrationService() {
    this.processSummaryRepoOld =
        new MigrationRepository<>(
            MigrationConfig.getOldDatabase(),
            MigrationConstants.MONGODB_COLLECTION_PROCESS_SUMMARIES,
            ProcessSummaries.class);
    this.processSummaryRepoNew =
        new MigrationRepository<>(
            MigrationConfig.getNewDatabase(),
            ConstantUtils.MONGODB_COLLECTION_PROCESS_SUMMARY,
            ProcessSummaryEntity.class);

    this.latestVersionRepoOld =
        new MigrationRepository<>(
            MigrationConfig.getOldDatabase(),
            MigrationConstants.MONGODB_COLLECTION_LATEST_VERSIONS,
            LatestVersionsEntity.class);
    this.latestVersionRepoNew =
        new MigrationRepository<>(
            MigrationConfig.getNewDatabase(),
            ConstantUtils.MONGODB_COLLECTION_LATEST_VERSION,
            LatestVersionEntity.class);

    this.gradleDependencyRepoOld =
        new MigrationRepository<>(
            MigrationConfig.getOldDatabase(),
            MigrationConstants.MONGODB_COLLECTION_DEPENDENCIES,
            Dependencies.class);
    this.gradleDependencyRepoNew =
        new MigrationRepository<>(
            MigrationConfig.getNewDatabase(),
            ConstantUtils.MONGODB_COLLECTION_GRADLE_DEPENDENCY,
            DependencyEntity.class);

    this.gradlePluginRepoOld =
        new MigrationRepository<>(
            MigrationConfig.getOldDatabase(),
            MigrationConstants.MONGODB_COLLECTION_PLUGINS,
            Plugins.class);
    this.gradlePluginRepoNew =
        new MigrationRepository<>(
            MigrationConfig.getNewDatabase(),
            ConstantUtils.MONGODB_COLLECTION_GRADLE_PLUGIN,
            DependencyEntity.class);

    this.pythonPackageRepoOld =
        new MigrationRepository<>(
            MigrationConfig.getOldDatabase(),
            MigrationConstants.MONGODB_COLLECTION_PACKAGES,
            Packages.class);
    this.pythonPackageRepoNew =
        new MigrationRepository<>(
            MigrationConfig.getNewDatabase(),
            ConstantUtils.MONGODB_COLLECTION_PYTHON_PACKAGE,
            DependencyEntity.class);
  }

  public void migrateProcessSummaries(boolean isDeleteAllNewFirst) {
    if (isDeleteAllNewFirst) {
      processSummaryRepoNew.deleteAll();
    }

    List<ProcessSummaries> processSummariesOld = processSummaryRepoOld.findAll();
    List<ProcessSummaryEntity> processSummariesNew = new ArrayList<>();

    for (ProcessSummaries processSummaryOld : processSummariesOld) {
      ProcessSummaryEntity processSummaryNew =
          new ProcessSummaryEntity(
              null,
              processSummaryOld.getUpdateDateTime(),
              getProcessSummaryUpdateType(processSummaryOld.getUpdateType()),
              processSummaryOld.getMongoPluginsToUpdate(),
              processSummaryOld.getMongoDependenciesToUpdate(),
              processSummaryOld.getMongoPackagesToUpdate(),
              0,
              processSummaryOld.getTotalPrCreatedCount(),
              processSummaryOld.getTotalPrCreateErrorsCount(),
              processSummaryOld.getTotalPrMergedCount(),
              getProcessedRepositories(processSummaryOld.getProcessedRepositories()),
              processSummaryOld.isErrorsOrExceptions());
      processSummariesNew.add(processSummaryNew);
    }

    if (!processSummariesNew.isEmpty()) {
      processSummaryRepoNew.insertAll(processSummariesNew);
    }
  }

  private String getProcessSummaryUpdateType(final String updateTypeOld) {
    final String updateTypeNew = updateTypeOld.split("_")[0];
    if (updateTypeNew.equals("NPM")) {
      return "NODE";
    }
    return updateTypeNew;
  }

  private List<ProcessSummaryEntity.ProcessRepositoryEntity> getProcessedRepositories(
      List<ProcessedRepository> processedRepositoriesOld) {
    List<ProcessSummaryEntity.ProcessRepositoryEntity> processRepositoriesNew = new ArrayList<>();
    for (ProcessedRepository processedRepositoryOld : processedRepositoriesOld) {
      ProcessSummaryEntity.ProcessRepositoryEntity processRepositoryNew =
          new ProcessSummaryEntity.ProcessRepositoryEntity(
              processedRepositoryOld.getRepoName(),
              processedRepositoryOld.getRepoType(),
              processedRepositoryOld.isPrCreated(), // use isPrCreated to check isUpdateBranchCreated
              processedRepositoryOld.isPrCreated(),
              processedRepositoryOld.isPrMerged());
      processRepositoriesNew.add(processRepositoryNew);
    }
    return processRepositoriesNew;
  }

  public void migrateLatestVersions(boolean isDeleteAllNewFirst) {
    if (isDeleteAllNewFirst) {
      latestVersionRepoNew.deleteAll();
    }

    List<LatestVersionsEntity> latestVersionsOld = latestVersionRepoOld.findAll();
    List<LatestVersionEntity> latestVersionsNew = new ArrayList<>();

    for (LatestVersionsEntity latestVersionOld : latestVersionsOld) {
      LatestVersionEntity latestVersionNew =
          new LatestVersionEntity(
              null,
              latestVersionOld.getUpdateDateTime(),
              latestVersionOld.getNginx(),
              latestVersionOld.getGradle(),
              latestVersionOld.getFlyway(),
              latestVersionOld.getCheckout(),
              latestVersionOld.getSetupJava(),
              latestVersionOld.getSetupGradle(),
              latestVersionOld.getSetupNode(),
              latestVersionOld.getSetupPython(),
              latestVersionOld.getCodeql(),
              latestVersionOld.getJava(),
              latestVersionOld.getNode(),
              latestVersionOld.getPython());
      latestVersionsNew.add(latestVersionNew);
    }

    if (!latestVersionsNew.isEmpty()) {
      latestVersionRepoNew.insertAll(latestVersionsNew);
    }
  }

  public void migrateGradleDependencies(boolean isDeleteAllNewFirst) {
    if (isDeleteAllNewFirst) {
      gradleDependencyRepoNew.deleteAll();
    }

    List<Dependencies> gradleDependenciesOld = gradleDependencyRepoOld.findAll();
    List<DependencyEntity> gradleDependenciesNew = new ArrayList<>();

    for (Dependencies gradleDependencyOld : gradleDependenciesOld) {
      DependencyEntity gradleDependencyNew =
          new DependencyEntity(
              gradleDependencyOld.getMavenId(),
              gradleDependencyOld.getLatestVersion(),
              gradleDependencyOld.isSkipVersion());
      gradleDependenciesNew.add(gradleDependencyNew);
    }

    if (!gradleDependenciesNew.isEmpty()) {
      gradleDependencyRepoNew.insertAll(gradleDependenciesNew);
    }
  }

  public void migrateGradlePlugins(boolean isDeleteAllNewFirst) {
    if (isDeleteAllNewFirst) {
      gradlePluginRepoNew.deleteAll();
    }

    List<Plugins> gradlePluginsOld = gradlePluginRepoOld.findAll();
    List<DependencyEntity> gradlePluginsNew = new ArrayList<>();

    for (Plugins gradlePluginOld : gradlePluginsOld) {
      DependencyEntity gradlePluginNew =
          new DependencyEntity(
              gradlePluginOld.getGroup(),
              gradlePluginOld.getVersion(),
              gradlePluginOld.isSkipVersion());
      gradlePluginsNew.add(gradlePluginNew);
    }

    if (!gradlePluginsNew.isEmpty()) {
      gradlePluginRepoNew.insertAll(gradlePluginsNew);
    }
  }

  public void migratePythonPlugins(boolean isDeleteAllNewFirst) {
    if (isDeleteAllNewFirst) {
      pythonPackageRepoNew.deleteAll();
    }

    List<Packages> pythonPackagesOld = pythonPackageRepoOld.findAll();
    List<DependencyEntity> pythonPackagesNew = new ArrayList<>();

    for (Packages pythonPackageOld : pythonPackagesOld) {
      DependencyEntity pythonPackageNew =
          new DependencyEntity(
              pythonPackageOld.getName(),
              pythonPackageOld.getVersion(),
              pythonPackageOld.isSkipVersion());
      pythonPackagesNew.add(pythonPackageNew);
    }

    if (!pythonPackagesNew.isEmpty()) {
      pythonPackageRepoNew.insertAll(pythonPackagesNew);
    }
  }
}
