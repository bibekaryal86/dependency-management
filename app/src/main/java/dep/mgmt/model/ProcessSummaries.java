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
    private final Integer totalPrCreateErrorsCount;
    private final Integer totalPrMergedCount;
    private final List<ProcessRepository> processRepositories;
    private final Boolean isErrorsOrExceptions;

    @JsonCreator
    public ProcessSummary(
        @JsonProperty("updateDateTime") final LocalDateTime updateDateTime,
        @JsonProperty("updateType") final String updateType,
        @JsonProperty("gradlePluginsToUpdate") final Integer gradlePluginsToUpdate,
        @JsonProperty("gradleDependenciesToUpdate") final Integer gradleDependenciesToUpdate,
        @JsonProperty("pythonPackagesToUpdate") final Integer pythonPackagesToUpdate,
        @JsonProperty("nodeDependenciesToUpdate") final Integer nodeDependenciesToUpdate,
        @JsonProperty("totalPrCreatedCount") final Integer totalPrCreatedCount,
        @JsonProperty("totalPrCreateErrorsCount") final Integer totalPrCreateErrorsCount,
        @JsonProperty("totalPrMergedCount") final Integer totalPrMergedCount,
        @JsonProperty("processRepositories") final List<ProcessRepository> processRepositories,
        @JsonProperty("isErrorsOrExceptions") final Boolean isErrorsOrExceptions) {
      this.updateDateTime = updateDateTime;
      this.updateType = updateType;
      this.gradlePluginsToUpdate = gradlePluginsToUpdate;
      this.gradleDependenciesToUpdate = gradleDependenciesToUpdate;
      this.pythonPackagesToUpdate = pythonPackagesToUpdate;
      this.nodeDependenciesToUpdate = nodeDependenciesToUpdate;
      this.totalPrCreatedCount = totalPrCreatedCount;
      this.totalPrCreateErrorsCount = totalPrCreateErrorsCount;
      this.totalPrMergedCount = totalPrMergedCount;
      this.processRepositories = processRepositories;
      this.isErrorsOrExceptions = isErrorsOrExceptions;
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

    public Integer getTotalPrCreateErrorsCount() {
      return totalPrCreateErrorsCount;
    }

    public Integer getTotalPrMergedCount() {
      return totalPrMergedCount;
    }

    public List<ProcessRepository> getProcessRepositories() {
      return processRepositories;
    }

    public Boolean getErrorsOrExceptions() {
      return isErrorsOrExceptions;
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

    public static class ProcessRepository implements Serializable {
      private final String repoName;
      private final String repoType;
      private final Boolean isUpdateBranchCreated;

      private Boolean isPrCreated;
      private Boolean isPrMerged;

      @JsonCreator
      public ProcessRepository(
          @JsonProperty("repoName") final String repoName,
          @JsonProperty("repoType") final String repoType,
          @JsonProperty("isUpdateBranchCreated") final Boolean isUpdateBranchCreated,
          @JsonProperty("isPrCreated") final Boolean isPrCreated,
          @JsonProperty("isPrMerged") final Boolean isPrMerged) {
        this.repoName = repoName;
        this.repoType = repoType;
        this.isUpdateBranchCreated = isUpdateBranchCreated;
        this.isPrCreated = isPrCreated;
        this.isPrMerged = isPrMerged;
      }

      public ProcessRepository(final String repoName, final String repoType, final Boolean isUpdateBranchCreated) {
        this.repoName = repoName;
        this.isUpdateBranchCreated = isUpdateBranchCreated;
        this.isPrCreated = Boolean.FALSE;
        this.repoType = repoType;
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

      public void setPrCreated(final Boolean prCreated) {
        isPrCreated = prCreated;
      }

      public void setPrMerged(final Boolean prMerged) {
        isPrMerged = prMerged;
      }

      @Override
      public String toString() {
        return "ProcessRepository{" +
                "repoName='" + repoName + '\'' +
                ", repoType='" + repoType + '\'' +
                ", isUpdateBranchCreated=" + isUpdateBranchCreated +
                ", isPrCreated=" + isPrCreated +
                ", isPrMerged=" + isPrMerged +
                '}';
      }
    }
  }
}
