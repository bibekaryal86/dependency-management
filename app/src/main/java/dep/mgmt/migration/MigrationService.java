package dep.mgmt.migration;

import dep.mgmt.migration.entities_new.LatestVersionEntity;
import dep.mgmt.migration.entities_new.ProcessSummaryEntity;
import dep.mgmt.migration.entities_old.LatestVersionsEntity;
import dep.mgmt.migration.entities_old.ProcessSummaries;
import dep.mgmt.migration.entities_old.ProcessedRepository;
import dep.mgmt.model.ProcessSummary;
import dep.mgmt.model.enums.RequestParams;
import dep.mgmt.util.ConstantUtils;

import java.util.ArrayList;
import java.util.List;

public class MigrationService {

    private final MigrationRepository<ProcessSummaries> processSummaryRepoOld;
    private final MigrationRepository<ProcessSummaryEntity> processSummaryRepoNew;

    private final MigrationRepository<LatestVersionsEntity> latestVersionRepoOld;
    private final MigrationRepository<LatestVersionEntity> latestVersionRepoNew;

    public MigrationService() {
        this.processSummaryRepoOld = new MigrationRepository<>(MigrationConfig.getOldDatabase(), MigrationConstants.MONGODB_COLLECTION_PROCESS_SUMMARIES, ProcessSummaries.class);
        this.processSummaryRepoNew = new MigrationRepository<>(MigrationConfig.getNewDatabase(), ConstantUtils.MONGODB_COLLECTION_PROCESS_SUMMARY, ProcessSummaryEntity.class);

        this.latestVersionRepoOld = new MigrationRepository<>(MigrationConfig.getOldDatabase(), MigrationConstants.MONGODB_COLLECTION_LATEST_VERSIONS, LatestVersionsEntity.class);
        this.latestVersionRepoNew = new MigrationRepository<>(MigrationConfig.getNewDatabase(), ConstantUtils.MONGODB_COLLECTION_LATEST_VERSION, LatestVersionEntity.class);
    }

    public void migrateProcessSummaries(boolean isDeleteAllNewFirst) {
        if (isDeleteAllNewFirst) {
            processSummaryRepoNew.deleteAll();
        }

        List<ProcessSummaries> processSummariesOld = processSummaryRepoOld.findAll();
        List<ProcessSummaryEntity> processSummariesNew = new ArrayList<>();

        for (ProcessSummaries processSummaryOld : processSummariesOld) {
            ProcessSummaryEntity processSummaryNew = new ProcessSummaryEntity(
                    null,
                    processSummaryOld.getUpdateDateTime(),
                    RequestParams.UpdateType.valueOf(processSummaryOld.getUpdateType()),
                    processSummaryOld.getMongoPluginsToUpdate(),
                    processSummaryOld.getMongoDependenciesToUpdate(),
                    processSummaryOld.getMongoPackagesToUpdate(),
                    0,
                    processSummaryOld.getTotalPrCreatedCount(),
                    processSummaryOld.getTotalPrCreateErrorsCount(),
                    processSummaryOld.getTotalPrMergedCount(),
                    getProcessedRepositories(processSummaryOld.getProcessedRepositories()),
                    processSummaryOld.isErrorsOrExceptions()
            );
            processSummariesNew.add(processSummaryNew);
        }

        if (!processSummariesNew.isEmpty()) {
            processSummaryRepoNew.insertAll(processSummariesNew);
        }
    }

    private List<ProcessSummary.ProcessRepository> getProcessedRepositories(List<ProcessedRepository> processedRepositoriesOld) {
        List<ProcessSummary.ProcessRepository> processRepositoriesNew = new ArrayList<>();
        for (ProcessedRepository processedRepositoryOld : processedRepositoriesOld) {
            ProcessSummary.ProcessRepository processRepositoryNew = new ProcessSummary.ProcessRepository(
                    processedRepositoryOld.getRepoName(),
                    processedRepositoryOld.isPrCreated(),
                    processedRepositoryOld.isPrCreateError(),
                    processedRepositoryOld.getRepoType(),
                    processedRepositoryOld.isPrMerged()
            );
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
            LatestVersionEntity latestVersionNew = new LatestVersionEntity(
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
                    latestVersionOld.getPython()
            );
            latestVersionsNew.add(latestVersionNew);
        }

        if (!latestVersionsNew.isEmpty()) {
            latestVersionRepoNew.insertAll(latestVersionsNew);
        }
    }
}
