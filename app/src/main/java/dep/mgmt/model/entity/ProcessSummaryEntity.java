package dep.mgmt.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

public class ProcessSummaryEntity implements Serializable {
  @BsonId private ObjectId id;
  private LocalDateTime updateDateTime;

  private String updateType;
  private Integer gradlePluginsToUpdate;
  private Integer gradleDependenciesToUpdate;
  private Integer pythonPackagesToUpdate;
  private Integer npmDependenciesToUpdate;
  private Integer totalPrCreatedCount;
  private Integer totalPrCreateErrorsCount;
  private Integer totalPrMergedCount;
  private List<ProcessRepositoryEntity> processRepositories;
  private Boolean isErrorsOrExceptions;

  public ProcessSummaryEntity() {}

  public ProcessSummaryEntity(
      final ObjectId id,
      final LocalDateTime updateDateTime,
      final String updateType,
      final Integer gradlePluginsToUpdate,
      final Integer gradleDependenciesToUpdate,
      final Integer pythonPackagesToUpdate,
      final Integer npmDependenciesToUpdate,
      final Integer totalPrCreatedCount,
      final Integer totalPrCreateErrorsCount,
      final Integer totalPrMergedCount,
      final List<ProcessRepositoryEntity> processRepositories,
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
    this.processRepositories =
        processRepositories == null ? Collections.emptyList() : processRepositories;
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

  public String getUpdateType() {
    return updateType;
  }

  public void setUpdateType(String updateType) {
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

  public List<ProcessRepositoryEntity> getProcessRepositories() {
    return processRepositories;
  }

  public void setProcessRepositories(List<ProcessRepositoryEntity> processRepositories) {
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
    return "ProcessSummaryEntity{"
        + "id="
        + id
        + ", updateDateTime="
        + updateDateTime
        + ", updateType="
        + updateType
        + ", gradlePluginsToUpdate="
        + gradlePluginsToUpdate
        + ", gradleDependenciesToUpdate="
        + gradleDependenciesToUpdate
        + ", pythonPackagesToUpdate="
        + pythonPackagesToUpdate
        + ", npmDependenciesToUpdate="
        + npmDependenciesToUpdate
        + ", totalPrCreatedCount="
        + totalPrCreatedCount
        + ", totalPrCreateErrorsCount="
        + totalPrCreateErrorsCount
        + ", totalPrMergedCount="
        + totalPrMergedCount
        + ", processRepositories="
        + processRepositories
        + ", isErrorsOrExceptions="
        + isErrorsOrExceptions
        + '}';
  }

  public static class ProcessRepositoryEntity {
    private String repoName;
    private Boolean isPrCreated;
    private Boolean isPrCreateError;
    private String repoType;
    private Boolean isPrMerged;

    public ProcessRepositoryEntity() {}

    public ProcessRepositoryEntity(
        String repoName,
        Boolean isPrCreated,
        Boolean isPrCreateError,
        String repoType,
        Boolean isPrMerged) {
      this.repoName = repoName;
      this.isPrCreated = isPrCreated;
      this.isPrCreateError = isPrCreateError;
      this.repoType = repoType;
      this.isPrMerged = isPrMerged;
    }

    public String getRepoName() {
      return repoName;
    }

    public void setRepoName(String repoName) {
      this.repoName = repoName;
    }

    public Boolean getPrCreated() {
      return isPrCreated;
    }

    public void setPrCreated(Boolean prCreated) {
      isPrCreated = prCreated;
    }

    public Boolean getPrCreateError() {
      return isPrCreateError;
    }

    public void setPrCreateError(Boolean prCreateError) {
      isPrCreateError = prCreateError;
    }

    public String getRepoType() {
      return repoType;
    }

    public void setRepoType(String repoType) {
      this.repoType = repoType;
    }

    public Boolean getPrMerged() {
      return isPrMerged;
    }

    public void setPrMerged(Boolean prMerged) {
      isPrMerged = prMerged;
    }

    @Override
    public String toString() {
      return "ProcessRepositoryEntity{"
          + "repoName='"
          + repoName
          + '\''
          + ", isPrCreated="
          + isPrCreated
          + ", isPrCreateError="
          + isPrCreateError
          + ", repoType='"
          + repoType
          + '\''
          + ", isPrMerged="
          + isPrMerged
          + '}';
    }
  }
}
