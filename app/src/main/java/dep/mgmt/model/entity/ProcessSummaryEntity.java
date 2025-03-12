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
  private Integer gradlePluginsToUpdate;
  private Integer gradleDependenciesToUpdate;
  private Integer pythonPackagesToUpdate;
  private Integer npmDependenciesToUpdate;
  private Integer totalPrCreatedCount;
  private Integer totalPrCreateErrorsCount;
  private Integer totalPrMergedCount;
  private List<ProcessSummary.ProcessRepository> processRepositories;
  private Boolean isErrorsOrExceptions;

  public ProcessSummaryEntity() {}

  public ProcessSummaryEntity(
      final ObjectId id,
      final LocalDateTime updateDateTime,
      final RequestParams.UpdateType updateType,
      final Integer gradlePluginsToUpdate,
  final Integer gradleDependenciesToUpdate,
  final Integer pythonPackagesToUpdate,
  final Integer npmDependenciesToUpdate,
      final Integer totalPrCreatedCount,
      final Integer totalPrCreateErrorsCount,
      final Integer totalPrMergedCount,
      final List<ProcessSummary.ProcessRepository> processRepositories,
      final Boolean isErrorsOrExceptions) {
    this.id = id;
    this.updateDateTime = updateDateTime;
    this.updateType = updateType;
    this.gradlePluginsToUpdate = gradlePluginsToUpdate;
    this.gradleDependenciesToUpdate = gradleDependenciesToUpdate;
    this.pythonPackagesToUpdate = pythonPackagesToUpdate;
    this.npmDependenciesToUpdate = npmDependenciesToUpdate;
    this.totalPrCreatedCount = totalPrCreatedCount;
    this.totalPrCreateErrorsCount = totalPrCreateErrorsCount;
    this.totalPrMergedCount = totalPrMergedCount;
    this.processRepositories = processRepositories == null ? Collections.emptyList() : processRepositories;
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

  public Integer getGradlePluginsToUpdate() {
    return gradlePluginsToUpdate;
  }

  public void setGradlePluginsToUpdate(Integer gradlePluginsToUpdate) {
    this.gradlePluginsToUpdate = gradlePluginsToUpdate;
  }

  public Integer getGradleDependenciesToUpdate() {
    return gradleDependenciesToUpdate;
  }

  public void setGradleDependenciesToUpdate(Integer gradleDependenciesToUpdate) {
    this.gradleDependenciesToUpdate = gradleDependenciesToUpdate;
  }

  public Integer getPythonPackagesToUpdate() {
    return pythonPackagesToUpdate;
  }

  public void setPythonPackagesToUpdate(Integer pythonPackagesToUpdate) {
    this.pythonPackagesToUpdate = pythonPackagesToUpdate;
  }

  public Integer getNpmDependenciesToUpdate() {
    return npmDependenciesToUpdate;
  }

  public void setNpmDependenciesToUpdate(Integer npmDependenciesToUpdate) {
    this.npmDependenciesToUpdate = npmDependenciesToUpdate;
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

  public List<ProcessSummary.ProcessRepository> getProcessRepositories() {
    return processRepositories;
  }

  public void setProcessRepositories(List<ProcessSummary.ProcessRepository> processRepositories) {
    this.processRepositories = processRepositories;
  }

  public Boolean getErrorsOrExceptions() {
    return isErrorsOrExceptions;
  }

  public void setErrorsOrExceptions(Boolean errorsOrExceptions) {
    isErrorsOrExceptions = errorsOrExceptions;
  }

  @Override
  public String toString() {
    return "ProcessSummaryEntity{" +
            "id=" + id +
            ", updateDateTime=" + updateDateTime +
            ", updateType=" + updateType +
            ", gradlePluginsToUpdate=" + gradlePluginsToUpdate +
            ", gradleDependenciesToUpdate=" + gradleDependenciesToUpdate +
            ", pythonPackagesToUpdate=" + pythonPackagesToUpdate +
            ", npmDependenciesToUpdate=" + npmDependenciesToUpdate +
            ", totalPrCreatedCount=" + totalPrCreatedCount +
            ", totalPrCreateErrorsCount=" + totalPrCreateErrorsCount +
            ", totalPrMergedCount=" + totalPrMergedCount +
            ", processRepositories=" + processRepositories +
            ", isErrorsOrExceptions=" + isErrorsOrExceptions +
            '}';
  }
}
