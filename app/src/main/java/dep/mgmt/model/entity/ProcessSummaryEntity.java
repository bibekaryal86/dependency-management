package dep.mgmt.model.entity;

import dep.mgmt.model.enums.RequestParams;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

public class ProcessSummaryEntity implements Serializable {
  @BsonId private final ObjectId id;
  private final LocalDateTime updateDateTime;

  // same as ProcessSummary
  private final RequestParams.UpdateType updateType;
  private final Integer mongoPluginsToUpdate;
  private final Integer mongoDependenciesToUpdate;
  private final Integer mongoPackagesToUpdate;
  private final Integer mongoNpmSkipsActive;
  private final Integer totalPrCreatedCount;
  private final Integer totalPrCreateErrorsCount;
  private final Integer totalPrMergedCount;
  private final List<ProcessedRepositories> processedRepositories;
  private final Boolean isErrorsOrExceptions;

  public ProcessSummaryEntity(
      final ObjectId id,
      final LocalDateTime updateDateTime,
      final RequestParams.UpdateType updateType,
      final Integer mongoPluginsToUpdate,
      final Integer mongoDependenciesToUpdate,
      final Integer mongoPackagesToUpdate,
      final Integer mongoNpmSkipsActive,
      final Integer totalPrCreatedCount,
      final Integer totalPrCreateErrorsCount,
      final Integer totalPrMergedCount,
      final List<ProcessedRepositories> processedRepositories,
      final Boolean isErrorsOrExceptions) {
    this.id = id;
    this.updateDateTime = updateDateTime;
    this.updateType = updateType;
    this.mongoPluginsToUpdate = mongoPluginsToUpdate;
    this.mongoDependenciesToUpdate = mongoDependenciesToUpdate;
    this.mongoPackagesToUpdate = mongoPackagesToUpdate;
    this.mongoNpmSkipsActive = mongoNpmSkipsActive;
    this.totalPrCreatedCount = totalPrCreatedCount;
    this.totalPrCreateErrorsCount = totalPrCreateErrorsCount;
    this.totalPrMergedCount = totalPrMergedCount;
    this.processedRepositories =
        processedRepositories == null ? Collections.emptyList() : processedRepositories;
    this.isErrorsOrExceptions = isErrorsOrExceptions;
  }

  public ObjectId getId() {
    return id;
  }

  public LocalDateTime getUpdateDateTime() {
    return updateDateTime;
  }

  public RequestParams.UpdateType getUpdateType() {
    return updateType;
  }

  public Integer getMongoPluginsToUpdate() {
    return mongoPluginsToUpdate;
  }

  public Integer getMongoDependenciesToUpdate() {
    return mongoDependenciesToUpdate;
  }

  public Integer getMongoPackagesToUpdate() {
    return mongoPackagesToUpdate;
  }

  public Integer getMongoNpmSkipsActive() {
    return mongoNpmSkipsActive;
  }

  public Integer getTotalPrCreatedCount() {
    return totalPrCreatedCount;
  }

  public Integer getTotalPrCreateErrorsCount() {
    return totalPrCreateErrorsCount;
  }

  public Integer getTotalPrMergedCount() {
    return totalPrMergedCount;
  }

  public List<ProcessedRepositories> getProcessedRepositories() {
    return processedRepositories;
  }

  public Boolean getErrorsOrExceptions() {
    return isErrorsOrExceptions;
  }

  @Override
  public String toString() {
    return "ProcessSummaries{"
        + "id="
        + id
        + ", updateDateTime="
        + updateDateTime
        + ", updateType="
        + updateType
        + ", mongoPluginsToUpdate="
        + mongoPluginsToUpdate
        + ", mongoDependenciesToUpdate="
        + mongoDependenciesToUpdate
        + ", mongoPackagesToUpdate="
        + mongoPackagesToUpdate
        + ", mongoNpmSkipsActive="
        + mongoNpmSkipsActive
        + ", totalPrCreatedCount="
        + totalPrCreatedCount
        + ", totalPrCreateErrorsCount="
        + totalPrCreateErrorsCount
        + ", totalPrMergedCount="
        + totalPrMergedCount
        + ", processedRepositories="
        + processedRepositories.size()
        + ", isErrorsOrExceptions="
        + isErrorsOrExceptions
        + '}';
  }
}
