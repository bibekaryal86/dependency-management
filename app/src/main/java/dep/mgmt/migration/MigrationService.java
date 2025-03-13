package dep.mgmt.migration;

import dep.mgmt.migration.entities_new.ProcessSummaryEntity;
import dep.mgmt.migration.entities_old.ProcessSummaries;
import dep.mgmt.util.ConstantUtils;

public class MigrationService {

    private final MigrationRepository processSummaryRepoOld;
    private final MigrationRepository processSummaryRepoNew;

    public MigrationService() {
        this.processSummaryRepoOld = new MigrationRepository<>(MigrationConfig.getOldDatabase(), MigrationConstants.MONGODB_COLLECTION_PROCESS_SUMMARIES, ProcessSummaries.class);
        this.processSummaryRepoNew = new MigrationRepository<>(MigrationConfig.getNewDatabase(), ConstantUtils.MONGODB_COLLECTION_PROCESS_SUMMARY, ProcessSummaryEntity.class);
    }



}
