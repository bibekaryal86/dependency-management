package dep.mgmt.model.entity;

import dep.mgmt.model.ProcessSummary;
import dep.mgmt.model.enums.RequestParams;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

public class ProcessSummaryEntity implements Serializable {
  @BsonId private ObjectId id;
  private LocalDateTime updateDateTime;

  // same as ProcessSummary
  private RequestParams.UpdateType updateType;
  private Integer mongoPluginsToUpdate;
  private Integer mongoDependenciesToUpdate;
  private Integer mongoPackagesToUpdate;
  private Integer mongoNpmSkipsActive;
  private Integer totalPrCreatedCount;
  private Integer totalPrCreateErrorsCount;
  private Integer totalPrMergedCount;
  private List<ProcessSummary.Summary.Repository> processedRepositories;
  private Boolean isErrorsOrExceptions;

  public ProcessSummaryEntity() {}

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
      final List<ProcessSummary.Summary.Repository> processedRepositories,
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

  public void setId(ObjectId id) {
    this.id = id;
  }

  public LocalDateTime getUpdateDateTime() {
    return updateDateTime;
  }

  public void setUpdateDateTime(LocalDateTime updateDateTime) {
    this.updateDateTime = updateDateTime;
  }

  public RequestParams.UpdateType getUpdateType() {
    return updateType;
  }

  public void setUpdateType(RequestParams.UpdateType updateType) {
    this.updateType = updateType;
  }

  public Integer getMongoPluginsToUpdate() {
    return mongoPluginsToUpdate;
  }

  public void setMongoPluginsToUpdate(Integer mongoPluginsToUpdate) {
    this.mongoPluginsToUpdate = mongoPluginsToUpdate;
  }

  public Integer getMongoDependenciesToUpdate() {
    return mongoDependenciesToUpdate;
  }

  public void setMongoDependenciesToUpdate(Integer mongoDependenciesToUpdate) {
    this.mongoDependenciesToUpdate = mongoDependenciesToUpdate;
  }

  public Integer getMongoPackagesToUpdate() {
    return mongoPackagesToUpdate;
  }

  public void setMongoPackagesToUpdate(Integer mongoPackagesToUpdate) {
    this.mongoPackagesToUpdate = mongoPackagesToUpdate;
  }

  public Integer getMongoNpmSkipsActive() {
    return mongoNpmSkipsActive;
  }

  public void setMongoNpmSkipsActive(Integer mongoNpmSkipsActive) {
    this.mongoNpmSkipsActive = mongoNpmSkipsActive;
  }

  public Integer getTotalPrCreatedCount() {
    return totalPrCreatedCount;
  }

  public void setTotalPrCreatedCount(Integer totalPrCreatedCount) {
    this.totalPrCreatedCount = totalPrCreatedCount;
  }

  public Integer getTotalPrCreateErrorsCount() {
    return totalPrCreateErrorsCount;
  }

  public void setTotalPrCreateErrorsCount(Integer totalPrCreateErrorsCount) {
    this.totalPrCreateErrorsCount = totalPrCreateErrorsCount;
  }

  public Integer getTotalPrMergedCount() {
    return totalPrMergedCount;
  }

  public void setTotalPrMergedCount(Integer totalPrMergedCount) {
    this.totalPrMergedCount = totalPrMergedCount;
  }

  public List<ProcessSummary.Summary.Repository> getProcessedRepositories() {
    return processedRepositories;
  }

  public void setProcessedRepositories(
      List<ProcessSummary.Summary.Repository> processedRepositories) {
    this.processedRepositories = processedRepositories;
  }

  public Boolean getErrorsOrExceptions() {
    return isErrorsOrExceptions;
  }

  public void setErrorsOrExceptions(Boolean errorsOrExceptions) {
    isErrorsOrExceptions = errorsOrExceptions;
  }

  @Override
  public String toString() {
    return "ProcessSummaryEntity{"
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
