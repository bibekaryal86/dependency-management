package dep.mgmt.migration.entities_old;

import java.time.LocalDateTime;
import java.util.List;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

public class ProcessSummaries extends ProcessSummary {
  @BsonId private ObjectId id;
  private LocalDateTime updateDateTime;

  public ProcessSummaries() {}

  public ProcessSummaries(ObjectId id, LocalDateTime updateDateTime) {
    this.id = id;
    this.updateDateTime = updateDateTime;
  }

  public ProcessSummaries(
      String updateType,
      int mongoPluginsToUpdate,
      int mongoDependenciesToUpdate,
      int mongoPackagesToUpdate,
      int mongoNpmSkipsActive,
      int totalPrCreatedCount,
      int totalPrCreateErrorsCount,
      int totalPrMergedCount,
      List<ProcessedRepository> processedRepositories,
      boolean isErrorsOrExceptions,
      ObjectId id,
      LocalDateTime updateDateTime) {
    super(
        updateType,
        mongoPluginsToUpdate,
        mongoDependenciesToUpdate,
        mongoPackagesToUpdate,
        mongoNpmSkipsActive,
        totalPrCreatedCount,
        totalPrCreateErrorsCount,
        totalPrMergedCount,
        processedRepositories,
        isErrorsOrExceptions);
    this.id = id;
    this.updateDateTime = updateDateTime;
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public LocalDateTime getUpdateDateTime() {
    return updateDateTime;
  }

  public void setUpdateDateTime(LocalDateTime updateDateTime) {
    this.updateDateTime = updateDateTime;
  }
}
