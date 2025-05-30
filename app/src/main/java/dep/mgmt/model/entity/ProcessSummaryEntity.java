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

  public Integer getNodeDependenciesToUpdate() {
    return nodeDependenciesToUpdate;
  }

  public void setNodeDependenciesToUpdate(Integer nodeDependenciesToUpdate) {
    this.nodeDependenciesToUpdate = nodeDependenciesToUpdate;
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

  public Integer getTotalPrMergeErrorCount() {
    return totalPrMergeErrorCount;
  }

  public void setTotalPrMergeErrorCount(Integer totalPrMergeErrorCount) {
    this.totalPrMergeErrorCount = totalPrMergeErrorCount;
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

  public List<ProcessTaskEntity> getProcessTasks() {
    return processTasks;
  }

  public void setProcessTasks(List<ProcessTaskEntity> processTasks) {
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
        String repoName,
        String repoType,
        Boolean isUpdateBranchCreated,
        Boolean isPrCreated,
        Boolean isPrMerged,
        Integer prNumber) {
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

    public void setRepoName(String repoName) {
      this.repoName = repoName;
    }

    public String getRepoType() {
      return repoType;
    }

    public void setRepoType(String repoType) {
      this.repoType = repoType;
    }

    public Boolean getUpdateBranchCreated() {
      return isUpdateBranchCreated;
    }

    public void setUpdateBranchCreated(Boolean updateBranchCreated) {
      this.isUpdateBranchCreated = updateBranchCreated;
    }

    public Boolean getPrCreated() {
      return isPrCreated;
    }

    public void setPrCreated(Boolean prCreated) {
      isPrCreated = prCreated;
    }

    public Boolean getPrMerged() {
      return isPrMerged;
    }

    public void setPrMerged(Boolean prMerged) {
      isPrMerged = prMerged;
    }

    public Integer getPrNumber() {
      return prNumber;
    }

    public void setPrNumber(Integer prNumber) {
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
    private Boolean isTimedOut;

    public ProcessTaskEntity() {}

    public ProcessTaskEntity(
        String queueName,
        String taskName,
        LocalDateTime added,
        LocalDateTime started,
        LocalDateTime ended,
        Boolean isTimedOut) {
      this.queueName = queueName;
      this.taskName = taskName;
      this.added = added;
      this.started = started;
      this.ended = ended;
      this.isTimedOut = isTimedOut;
    }

    public String getQueueName() {
      return queueName;
    }

    public void setQueueName(String queueName) {
      this.queueName = queueName;
    }

    public String getTaskName() {
      return taskName;
    }

    public void setTaskName(String taskName) {
      this.taskName = taskName;
    }

    public LocalDateTime getAdded() {
      return added;
    }

    public void setAdded(LocalDateTime added) {
      this.added = added;
    }

    public LocalDateTime getStarted() {
      return started;
    }

    public void setStarted(LocalDateTime started) {
      this.started = started;
    }

    public LocalDateTime getEnded() {
      return ended;
    }

    public void setEnded(LocalDateTime ended) {
      this.ended = ended;
    }

    public Boolean getTimedOut() {
      return isTimedOut;
    }

    public void setTimedOut(Boolean timedOut) {
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
          + ", isTimedOut="
          + isTimedOut
          + '}';
    }
  }
}
