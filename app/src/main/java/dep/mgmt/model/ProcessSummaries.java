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
    private final Integer gradlePluginsToUpdate;
    private final Integer gradleDependenciesToUpdate;
    private final Integer pythonPackagesToUpdate;
    private final Integer nodeDependenciesToUpdate;
    private final Integer totalPrCreatedCount;
    private final Integer totalPrMergedCount;
    private final Integer totalPrMergeErrorsCount;
    private final List<ProcessRepository> processRepositories;
    private final Boolean isErrorsOrExceptions;
    private final List<ProcessTask> processTasks;

    @JsonCreator
    public ProcessSummary(
        @JsonProperty("updateDateTime") final LocalDateTime updateDateTime,
        @JsonProperty("updateType") final String updateType,
        @JsonProperty("gradlePluginsToUpdate") final Integer gradlePluginsToUpdate,
        @JsonProperty("gradleDependenciesToUpdate") final Integer gradleDependenciesToUpdate,
        @JsonProperty("pythonPackagesToUpdate") final Integer pythonPackagesToUpdate,
        @JsonProperty("nodeDependenciesToUpdate") final Integer nodeDependenciesToUpdate,
        @JsonProperty("totalPrCreatedCount") final Integer totalPrCreatedCount,
        @JsonProperty("totalPrMergedCount") final Integer totalPrMergedCount,
        @JsonProperty("totalPrMergeErrorsCount") final Integer totalPrMergeErrorsCount,
        @JsonProperty("processRepositories") final List<ProcessRepository> processRepositories,
        @JsonProperty("isErrorsOrExceptions") final Boolean isErrorsOrExceptions,
        @JsonProperty("processTasks") final List<ProcessTask> processTasks) {
      this.updateDateTime = updateDateTime;
      this.updateType = updateType;
      this.gradlePluginsToUpdate = gradlePluginsToUpdate;
      this.gradleDependenciesToUpdate = gradleDependenciesToUpdate;
      this.pythonPackagesToUpdate = pythonPackagesToUpdate;
      this.nodeDependenciesToUpdate = nodeDependenciesToUpdate;
      this.totalPrCreatedCount = totalPrCreatedCount;
      this.totalPrMergedCount = totalPrMergedCount;
      this.totalPrMergeErrorsCount = totalPrMergeErrorsCount;
      this.processRepositories = processRepositories;
      this.isErrorsOrExceptions = isErrorsOrExceptions;
      this.processTasks = processTasks;
    }

    public LocalDateTime getUpdateDateTime() {
      return updateDateTime;
    }

    public String getUpdateType() {
      return updateType;
    }

    public Integer getGradlePluginsToUpdate() {
      return gradlePluginsToUpdate;
    }

    public Integer getGradleDependenciesToUpdate() {
      return gradleDependenciesToUpdate;
    }

    public Integer getPythonPackagesToUpdate() {
      return pythonPackagesToUpdate;
    }

    public Integer getNodeDependenciesToUpdate() {
      return nodeDependenciesToUpdate;
    }

    public Integer getTotalPrCreatedCount() {
      return totalPrCreatedCount;
    }

    public Integer getTotalPrMergedCount() {
      return totalPrMergedCount;
    }

    public Integer getTotalPrMergeErrorsCount() {
      return totalPrMergeErrorsCount;
    }

    public List<ProcessRepository> getProcessRepositories() {
      return processRepositories;
    }

    public Boolean getErrorsOrExceptions() {
      return isErrorsOrExceptions;
    }

    public List<ProcessTask> getProcessTasks() {
      return processTasks;
    }

    @Override
    public String toString() {
      return "ProcessSummary{"
          + "updateDateTime="
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
          + ", totalPrMergeErrorsCount="
          + totalPrMergeErrorsCount
          + ", processRepositories="
          + processRepositories
          + ", isErrorsOrExceptions="
          + isErrorsOrExceptions
          + ", processTasks="
          + processTasks
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
      private Boolean isTimedOut;

      @JsonCreator
      public ProcessTask(
          @JsonProperty("queueName") String queueName,
          @JsonProperty("taskName") String taskName,
          @JsonProperty("added") LocalDateTime added,
          @JsonProperty("started") LocalDateTime started,
          @JsonProperty("ended") LocalDateTime ended,
          @JsonProperty("isTimedOut") Boolean isTimedOut) {
        this.queueName = queueName;
        this.taskName = taskName;
        this.added = added;
        this.started = started;
        this.ended = ended;
        this.isTimedOut = isTimedOut;
      }

      public ProcessTask(final String queueName, final String taskName, final LocalDateTime added) {
        this.queueName = queueName;
        this.taskName = taskName;
        this.added = added;
        this.isTimedOut = Boolean.FALSE;
      }

      public ProcessTask(final String queueName, final String taskName) {
        this.queueName = queueName;
        this.taskName = taskName;
        this.added = null;
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

      public Boolean getTimedOut() {
        return isTimedOut;
      }

      public void setTimedOut(final boolean isTimedOut) {
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
            + ", isTimedOut="
            + isTimedOut
            + '}';
      }
    }
  }
}
