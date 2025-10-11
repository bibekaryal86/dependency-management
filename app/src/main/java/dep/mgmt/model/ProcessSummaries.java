package dep.mgmt.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class ProcessSummaries implements Serializable {
  private final List<ProcessSummary> processSummaries;
  private final Integer currentPage;
  private final Integer totalPages;
  private final Integer totalElements;
  private final Integer pageSize;

  @JsonCreator
  public ProcessSummaries(
      @JsonProperty("processSummaries") List<ProcessSummary> processSummaries,
      @JsonProperty("currentPage") Integer currentPage,
      @JsonProperty("totalPages") Integer totalPages,
      @JsonProperty("totalElements") Integer totalElements,
      @JsonProperty("pageSize") Integer pageSize) {
    this.processSummaries = processSummaries;
    this.currentPage = currentPage;
    this.totalPages = totalPages;
    this.totalElements = totalElements;
    this.pageSize = pageSize;
  }

  public List<ProcessSummary> getProcessSummaries() {
    return processSummaries;
  }

  public Integer getCurrentPage() {
    return currentPage;
  }

  public Integer getTotalPages() {
    return totalPages;
  }

  public Integer getTotalElements() {
    return totalElements;
  }

  public Integer getPageSize() {
    return pageSize;
  }

  @Override
  public String toString() {
    return "ProcessSummaries{"
        + "processSummaries="
        + processSummaries
        + ", currentPage="
        + currentPage
        + ", totalPages="
        + totalPages
        + ", totalElements="
        + totalElements
        + ", pageSize="
        + pageSize
        + '}';
  }

  public static class ProcessSummary implements Serializable {
    private final LocalDateTime updateDateTime;
    private final String updateType;
    private final Integer totalPrCreatedCount;
    private final Integer totalPrMergedCount;
    private final Boolean isErrorsOrExceptions;
    private final List<ProcessRepository> processRepositories;
    private final List<ProcessTask> processTasks;
    private final List<ProcessDependency> processDependencies;

    @JsonCreator
    public ProcessSummary(
        @JsonProperty("updateDateTime") final LocalDateTime updateDateTime,
        @JsonProperty("updateType") final String updateType,
        @JsonProperty("totalPrCreatedCount") final Integer totalPrCreatedCount,
        @JsonProperty("totalPrMergedCount") final Integer totalPrMergedCount,
        @JsonProperty("isErrorsOrExceptions") final Boolean isErrorsOrExceptions,
        @JsonProperty("processRepositories") final List<ProcessRepository> processRepositories,
        @JsonProperty("processTasks") final List<ProcessTask> processTasks,
        @JsonProperty("processDependencies") final List<ProcessDependency> processDependencies) {
      this.updateDateTime = updateDateTime;
      this.updateType = updateType;
      this.totalPrCreatedCount = totalPrCreatedCount;
      this.totalPrMergedCount = totalPrMergedCount;
      this.isErrorsOrExceptions = isErrorsOrExceptions;
      this.processRepositories = processRepositories;
      this.processTasks = processTasks;
      this.processDependencies = processDependencies;
    }

    public LocalDateTime getUpdateDateTime() {
      return updateDateTime;
    }

    public String getUpdateType() {
      return updateType;
    }

    public Integer getTotalPrCreatedCount() {
      return totalPrCreatedCount;
    }

    public Integer getTotalPrMergedCount() {
      return totalPrMergedCount;
    }

    public Boolean getErrorsOrExceptions() {
      return isErrorsOrExceptions;
    }

    public List<ProcessRepository> getProcessRepositories() {
      return processRepositories;
    }

    public List<ProcessTask> getProcessTasks() {
      return processTasks;
    }

    public List<ProcessDependency> getProcessDependencies() {
      return processDependencies;
    }

    @Override
    public String toString() {
      return "ProcessSummary{"
          + "updateDateTime="
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

    public static class ProcessRepository implements Serializable {
      private final String repoName;
      private final String repoType;
      private final Boolean isUpdateBranchCreated;

      private Boolean isPrCreated;
      private Boolean isPrMerged;
      private Integer prNumber;

      @JsonCreator
      public ProcessRepository(
          @JsonProperty("repoName") final String repoName,
          @JsonProperty("repoType") final String repoType,
          @JsonProperty("isUpdateBranchCreated") final Boolean isUpdateBranchCreated,
          @JsonProperty("isPrCreated") final Boolean isPrCreated,
          @JsonProperty("isPrMerged") final Boolean isPrMerged,
          @JsonProperty("prNumber") final Integer prNumber) {
        this.repoName = repoName;
        this.repoType = repoType;
        this.isUpdateBranchCreated = isUpdateBranchCreated;
        this.isPrCreated = isPrCreated;
        this.isPrMerged = isPrMerged;
        this.prNumber = prNumber;
      }

      public ProcessRepository(
          final String repoName, final String repoType, final Boolean isUpdateBranchCreated) {
        this.repoName = repoName;
        this.repoType = repoType;
        this.isUpdateBranchCreated = isUpdateBranchCreated;
        this.isPrCreated = Boolean.FALSE;
        this.isPrMerged = Boolean.FALSE;
      }

      public ProcessRepository(final String repoName, final String repoType) {
        this.repoName = repoName;
        this.repoType = repoType;
        this.isUpdateBranchCreated = Boolean.FALSE;
        this.isPrCreated = Boolean.FALSE;
        this.isPrMerged = Boolean.FALSE;
      }

      public String getRepoName() {
        return repoName;
      }

      public String getRepoType() {
        return repoType;
      }

      public Boolean getUpdateBranchCreated() {
        return isUpdateBranchCreated;
      }

      public Boolean getPrCreated() {
        return isPrCreated;
      }

      public Boolean getPrMerged() {
        return isPrMerged;
      }

      public Integer getPrNumber() {
        return prNumber;
      }

      public void setPrCreated(final Boolean prCreated) {
        this.isPrCreated = prCreated;
      }

      public void setPrMerged(final Boolean prMerged) {
        this.isPrMerged = prMerged;
      }

      public void setPrNumber(final Integer number) {
        this.prNumber = number;
      }

      @Override
      public String toString() {
        return "ProcessRepository{"
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

    public static class ProcessTask implements Serializable {
      private final String queueName;
      private final String taskName;
      private final LocalDateTime added;
      private LocalDateTime started;
      private LocalDateTime ended;
      private Long delayMills;
      private Boolean isTimedOut;

      @JsonCreator
      public ProcessTask(
          @JsonProperty("queueName") String queueName,
          @JsonProperty("taskName") String taskName,
          @JsonProperty("added") LocalDateTime added,
          @JsonProperty("started") LocalDateTime started,
          @JsonProperty("ended") LocalDateTime ended,
          @JsonProperty("delayMillis") Long delayMills,
          @JsonProperty("isTimedOut") Boolean isTimedOut) {
        this.queueName = queueName;
        this.taskName = taskName;
        this.added = added;
        this.started = started;
        this.ended = ended;
        this.delayMills = delayMills;
        this.isTimedOut = isTimedOut;
      }

      public ProcessTask(
          final String queueName,
          final String taskName,
          final LocalDateTime added,
          final long delayMills) {
        this.queueName = queueName;
        this.taskName = taskName;
        this.added = added;
        this.delayMills = delayMills;
        this.isTimedOut = Boolean.FALSE;
      }

      public String getQueueName() {
        return queueName;
      }

      public String getTaskName() {
        return taskName;
      }

      public LocalDateTime getAdded() {
        return added;
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

      public Long getDelayMills() {
        return delayMills;
      }

      public void setDelayMills(final Long delayMills) {
        this.delayMills = delayMills;
      }

      public Boolean getTimedOut() {
        return isTimedOut;
      }

      public void setTimedOut(final Boolean isTimedOut) {
        this.isTimedOut = isTimedOut;
      }

      @Override
      public String toString() {
        return "ProcessTask{"
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
            + ", delayMills="
            + delayMills
            + ", isTimedOut="
            + isTimedOut
            + '}';
      }
    }

    public static class ProcessDependency implements Serializable {
      private final String type;
      private final String name;
      private final String version;

      @JsonCreator
      public ProcessDependency(
          @JsonProperty("type") String type,
          @JsonProperty("name") String name,
          @JsonProperty("version") String version) {
        this.type = type;
        this.name = name;
        this.version = version;
      }

      public String getType() {
        return type;
      }

      public String getName() {
        return name;
      }

      public String getVersion() {
        return version;
      }

      @Override
      public String toString() {
        return "ProcessDependency{"
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
}
