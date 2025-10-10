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
  private Integer gradlePluginsChecked;
  private Integer gradleDependenciesChecked;
  private Integer pythonPackagesChecked;
  private Integer nodeDependenciesChecked;
  private Integer gradlePluginsToUpdate;
  private Integer gradleDependenciesToUpdate;
  private Integer pythonPackagesToUpdate;
  private Integer nodeDependenciesToUpdate;
  private Integer totalPrCreatedCount;
  private Integer totalPrMergedCount;
  private Integer totalPrMergeErrorCount;
  private List<ProcessRepositoryEntity> processRepositories;
  private Boolean isErrorsOrExceptions;
  private List<ProcessTaskEntity> processTasks;

  public ProcessSummaryEntity() {}

  public ProcessSummaryEntity(
      final ObjectId id,
      final LocalDateTime updateDateTime,
      final String updateType,
      final Integer gradlePluginsChecked,
      final Integer gradleDependenciesChecked,
      final Integer pythonPackagesChecked,
      final Integer nodeDependenciesChecked,
      final Integer gradlePluginsToUpdate,
      final Integer gradleDependenciesToUpdate,
      final Integer pythonPackagesToUpdate,
      final Integer nodeDependenciesToUpdate,
      final Integer totalPrCreatedCount,
      final Integer totalPrMergedCount,
      final Integer totalPrMergeErrorCount,
      final List<ProcessRepositoryEntity> processRepositories,
      final Boolean isErrorsOrExceptions,
      final List<ProcessTaskEntity> processTasks) {
    this.id = id;
    this.updateDateTime = updateDateTime;
    this.updateType = updateType;
    this.gradlePluginsChecked = gradlePluginsChecked;
    this.gradleDependenciesChecked = gradleDependenciesChecked;
    this.nodeDependenciesChecked = nodeDependenciesChecked;
    this.pythonPackagesChecked = pythonPackagesChecked;
    this.gradlePluginsToUpdate = gradlePluginsToUpdate;
    this.gradleDependenciesToUpdate = gradleDependenciesToUpdate;
    this.pythonPackagesToUpdate = pythonPackagesToUpdate;
    this.nodeDependenciesToUpdate = nodeDependenciesToUpdate;
    this.totalPrCreatedCount = totalPrCreatedCount;
    this.totalPrMergedCount = totalPrMergedCount;
    this.totalPrMergeErrorCount = totalPrMergeErrorCount;
    this.processRepositories =
        processRepositories == null ? Collections.emptyList() : processRepositories;
    this.isErrorsOrExceptions = isErrorsOrExceptions;
    this.processTasks = processTasks == null ? Collections.emptyList() : processTasks;
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(final ObjectId id) {
    this.id = id;
  }

  public LocalDateTime getUpdateDateTime() {
    return updateDateTime;
  }

  public void setUpdateDateTime(final LocalDateTime updateDateTime) {
    this.updateDateTime = updateDateTime;
  }

  public String getUpdateType() {
    return updateType;
  }

  public void setUpdateType(final String updateType) {
    this.updateType = updateType;
  }

  public Integer getGradlePluginsChecked() {
    return gradlePluginsChecked;
  }

  public void setGradlePluginsChecked(final Integer gradlePluginsChecked) {
    this.gradlePluginsChecked = gradlePluginsChecked;
  }

  public Integer getGradleDependenciesChecked() {
    return gradleDependenciesChecked;
  }

  public void setGradleDependenciesChecked(final Integer gradleDependenciesChecked) {
    this.gradleDependenciesChecked = gradleDependenciesChecked;
  }

  public Integer getPythonPackagesChecked() {
    return pythonPackagesChecked;
  }

  public void setPythonPackagesChecked(final Integer pythonPackagesChecked) {
    this.pythonPackagesChecked = pythonPackagesChecked;
  }

  public Integer getNodeDependenciesChecked() {
    return nodeDependenciesChecked;
  }

  public void setNodeDependenciesChecked(final Integer nodeDependenciesChecked) {
    this.nodeDependenciesChecked = nodeDependenciesChecked;
  }

  public Integer getGradlePluginsToUpdate() {
    return gradlePluginsToUpdate;
  }

  public void setGradlePluginsToUpdate(final Integer gradlePluginsToUpdate) {
    this.gradlePluginsToUpdate = gradlePluginsToUpdate;
  }

  public Integer getGradleDependenciesToUpdate() {
    return gradleDependenciesToUpdate;
  }

  public void setGradleDependenciesToUpdate(final Integer gradleDependenciesToUpdate) {
    this.gradleDependenciesToUpdate = gradleDependenciesToUpdate;
  }

  public Integer getPythonPackagesToUpdate() {
    return pythonPackagesToUpdate;
  }

  public void setPythonPackagesToUpdate(final Integer pythonPackagesToUpdate) {
    this.pythonPackagesToUpdate = pythonPackagesToUpdate;
  }

  public Integer getNodeDependenciesToUpdate() {
    return nodeDependenciesToUpdate;
  }

  public void setNodeDependenciesToUpdate(final Integer nodeDependenciesToUpdate) {
    this.nodeDependenciesToUpdate = nodeDependenciesToUpdate;
  }

  public Integer getTotalPrCreatedCount() {
    return totalPrCreatedCount;
  }

  public void setTotalPrCreatedCount(final Integer totalPrCreatedCount) {
    this.totalPrCreatedCount = totalPrCreatedCount;
  }

  public Integer getTotalPrMergedCount() {
    return totalPrMergedCount;
  }

  public void setTotalPrMergedCount(final Integer totalPrMergedCount) {
    this.totalPrMergedCount = totalPrMergedCount;
  }

  public Integer getTotalPrMergeErrorCount() {
    return totalPrMergeErrorCount;
  }

  public void setTotalPrMergeErrorCount(final Integer totalPrMergeErrorCount) {
    this.totalPrMergeErrorCount = totalPrMergeErrorCount;
  }

  public List<ProcessRepositoryEntity> getProcessRepositories() {
    return processRepositories;
  }

  public void setProcessRepositories(final List<ProcessRepositoryEntity> processRepositories) {
    this.processRepositories = processRepositories;
  }

  public Boolean getErrorsOrExceptions() {
    return isErrorsOrExceptions;
  }

  public void setErrorsOrExceptions(final Boolean errorsOrExceptions) {
    isErrorsOrExceptions = errorsOrExceptions;
  }

  public List<ProcessTaskEntity> getProcessTasks() {
    return processTasks;
  }

  public void setProcessTasks(final List<ProcessTaskEntity> processTasks) {
    this.processTasks = processTasks;
  }

  @Override
  public String toString() {
    return "ProcessSummaryEntity{"
        + "id="
        + id
        + ", updateDateTime="
        + updateDateTime
        + ", updateType='"
        + updateType
        + '\''
        + ", gradlePluginsChecked="
        + gradlePluginsChecked
        + ", gradleDependenciesChecked="
        + gradleDependenciesChecked
        + ", pythonPackagesChecked="
        + pythonPackagesChecked
        + ", nodeDependenciesChecked="
        + nodeDependenciesChecked
        + ", gradlePluginsToUpdate="
        + gradlePluginsToUpdate
        + ", gradleDependenciesToUpdate="
        + gradleDependenciesToUpdate
        + ", pythonPackagesToUpdate="
        + pythonPackagesToUpdate
        + ", nodeDependenciesToUpdate="
        + nodeDependenciesToUpdate
        + ", totalPrCreatedCount="
        + totalPrCreatedCount
        + ", totalPrMergedCount="
        + totalPrMergedCount
        + ", totalPrMergeErrorCount="
        + totalPrMergeErrorCount
        + ", processRepositories="
        + processRepositories
        + ", isErrorsOrExceptions="
        + isErrorsOrExceptions
        + ", processTasks="
        + processTasks
        + '}';
  }

  public static class ProcessRepositoryEntity {
    private String repoName;
    private String repoType;
    private Boolean isUpdateBranchCreated;
    private Boolean isPrCreated;
    private Boolean isPrMerged;
    private Integer prNumber;

    public ProcessRepositoryEntity() {}

    public ProcessRepositoryEntity(
        final String repoName,
        final String repoType,
        final Boolean isUpdateBranchCreated,
        final Boolean isPrCreated,
        final Boolean isPrMerged,
        final Integer prNumber) {
      this.repoName = repoName;
      this.repoType = repoType;
      this.isUpdateBranchCreated = isUpdateBranchCreated;
      this.isPrCreated = isPrCreated;
      this.isPrMerged = isPrMerged;
      this.prNumber = prNumber;
    }

    public String getRepoName() {
      return repoName;
    }

    public void setRepoName(final String repoName) {
      this.repoName = repoName;
    }

    public String getRepoType() {
      return repoType;
    }

    public void setRepoType(final String repoType) {
      this.repoType = repoType;
    }

    public Boolean getUpdateBranchCreated() {
      return isUpdateBranchCreated;
    }

    public void setUpdateBranchCreated(final Boolean updateBranchCreated) {
      this.isUpdateBranchCreated = updateBranchCreated;
    }

    public Boolean getPrCreated() {
      return isPrCreated;
    }

    public void setPrCreated(final Boolean prCreated) {
      isPrCreated = prCreated;
    }

    public Boolean getPrMerged() {
      return isPrMerged;
    }

    public void setPrMerged(final Boolean prMerged) {
      isPrMerged = prMerged;
    }

    public Integer getPrNumber() {
      return prNumber;
    }

    public void setPrNumber(final Integer prNumber) {
      this.prNumber = prNumber;
    }

    @Override
    public String toString() {
      return "ProcessRepositoryEntity{"
          + "repoName='"
          + repoName
          + '\''
          + ", repoType='"
          + repoType
          + '\''
          + ", isUpdateBranchCreated="
          + isUpdateBranchCreated
          + ", isPrCreated="
          + isPrCreated
          + ", isPrMerged="
          + isPrMerged
          + ", prNumber="
          + prNumber
          + '}';
    }
  }

  public static class ProcessTaskEntity {
    private String queueName;
    private String taskName;
    private LocalDateTime added;
    private LocalDateTime started;
    private LocalDateTime ended;
    private Long delayMillis;
    private Boolean isTimedOut;

    public ProcessTaskEntity() {}

    public ProcessTaskEntity(
        final String queueName,
        final String taskName,
        final LocalDateTime added,
        final LocalDateTime started,
        final LocalDateTime ended,
        final Long delayMillis,
        final Boolean isTimedOut) {
      this.queueName = queueName;
      this.taskName = taskName;
      this.added = added;
      this.started = started;
      this.ended = ended;
      this.delayMillis = delayMillis;
      this.isTimedOut = isTimedOut;
    }

    public String getQueueName() {
      return queueName;
    }

    public void setQueueName(final String queueName) {
      this.queueName = queueName;
    }

    public String getTaskName() {
      return taskName;
    }

    public void setTaskName(final String taskName) {
      this.taskName = taskName;
    }

    public LocalDateTime getAdded() {
      return added;
    }

    public void setAdded(final LocalDateTime added) {
      this.added = added;
    }

    public LocalDateTime getStarted() {
      return started;
    }

    public void setStarted(final LocalDateTime started) {
      this.started = started;
    }

    public LocalDateTime getEnded() {
      return ended;
    }

    public void setEnded(final LocalDateTime ended) {
      this.ended = ended;
    }

    public Long getDelayMillis() {
      return delayMillis;
    }

    public void setDelayMillis(Long delayMillis) {
      this.delayMillis = delayMillis;
    }

    public Boolean getTimedOut() {
      return isTimedOut;
    }

    public void setTimedOut(final Boolean timedOut) {
      isTimedOut = timedOut;
    }

    @Override
    public String toString() {
      return "ProcessTaskEntity{"
          + "queueName='"
          + queueName
          + '\''
          + ", taskName='"
          + taskName
          + '\''
          + ", added="
          + added
          + ", started="
          + started
          + ", ended="
          + ended
          + ", delayMillis="
          + delayMillis
          + ", isTimedOut="
          + isTimedOut
          + '}';
    }
  }
}
