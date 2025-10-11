package dep.mgmt.model.entity;

import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
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
  private Integer totalPrCreatedCount;
  private Integer totalPrMergedCount;
  private Boolean isErrorsOrExceptions;
  private List<ProcessRepositoryEntity> processRepositories;
  private List<ProcessTaskEntity> processTasks;
  private List<ProcessDependencyEntity> processDependencies;

  public ProcessSummaryEntity() {}

  public ProcessSummaryEntity(
      final ObjectId id,
      final LocalDateTime updateDateTime,
      final String updateType,
      final Integer totalPrCreatedCount,
      final Integer totalPrMergedCount,
      final Boolean isErrorsOrExceptions,
      final List<ProcessRepositoryEntity> processRepositories,
      final List<ProcessTaskEntity> processTasks,
      final List<ProcessDependencyEntity> processDependencies) {
    this.id = id;
    this.updateDateTime = updateDateTime;
    this.updateType = updateType;
    this.totalPrCreatedCount = totalPrCreatedCount;
    this.totalPrMergedCount = totalPrMergedCount;
    this.isErrorsOrExceptions = isErrorsOrExceptions;
    this.processRepositories =
        CommonUtilities.isEmpty(processRepositories)
            ? Collections.emptyList()
            : processRepositories;
    this.processTasks =
        CommonUtilities.isEmpty(processTasks) ? Collections.emptyList() : processTasks;
    this.processDependencies =
        CommonUtilities.isEmpty(processDependencies)
            ? Collections.emptyList()
            : processDependencies;
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

  public Integer getTotalPrCreatedCount() {
    return totalPrCreatedCount;
  }

  public void setTotalPrCreatedCount(Integer totalPrCreatedCount) {
    this.totalPrCreatedCount = totalPrCreatedCount;
  }

  public Integer getTotalPrMergedCount() {
    return totalPrMergedCount;
  }

  public void setTotalPrMergedCount(Integer totalPrMergedCount) {
    this.totalPrMergedCount = totalPrMergedCount;
  }

  public Boolean getErrorsOrExceptions() {
    return isErrorsOrExceptions;
  }

  public void setErrorsOrExceptions(Boolean errorsOrExceptions) {
    isErrorsOrExceptions = errorsOrExceptions;
  }

  public List<ProcessRepositoryEntity> getProcessRepositories() {
    return processRepositories;
  }

  public void setProcessRepositories(List<ProcessRepositoryEntity> processRepositories) {
    this.processRepositories = processRepositories;
  }

  public List<ProcessTaskEntity> getProcessTasks() {
    return processTasks;
  }

  public void setProcessTasks(List<ProcessTaskEntity> processTasks) {
    this.processTasks = processTasks;
  }

  public List<ProcessDependencyEntity> getProcessDependencies() {
    return processDependencies;
  }

  public void setProcessDependencies(List<ProcessDependencyEntity> processDependencies) {
    this.processDependencies = processDependencies;
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
        + ", totalPrCreatedCount="
        + totalPrCreatedCount
        + ", totalPrMergedCount="
        + totalPrMergedCount
        + ", isErrorsOrExceptions="
        + isErrorsOrExceptions
        + ", processRepositories="
        + processRepositories
        + ", processTasks="
        + processTasks
        + ", processDependencies="
        + processDependencies
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

  public static class ProcessDependencyEntity {
    private String type;
    private String name;
    private String version;

    public ProcessDependencyEntity() {}

    public ProcessDependencyEntity(final String type, final String name, final String version) {
      this.type = type;
      this.name = name;
      this.version = version;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getVersion() {
      return version;
    }

    public void setVersion(String version) {
      this.version = version;
    }

    @Override
    public String toString() {
      return "ProcessDependencyEntity{"
          + "type='"
          + type
          + '\''
          + ", name='"
          + name
          + '\''
          + ", version='"
          + version
          + '\''
          + '}';
    }
  }
}
