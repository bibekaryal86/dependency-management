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

  public static class ProcessSummary {
    private final LocalDateTime updateDateTime;
    private final String updateType;
    private final Integer gradlePluginsToUpdate;
    private final Integer gradleDependenciesToUpdate;
    private final Integer pythonPackagesToUpdate;
    private final Integer npmDependenciesToUpdate;
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
        @JsonProperty("npmDependenciesToUpdate") final Integer npmDependenciesToUpdate,
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
      this.npmDependenciesToUpdate = npmDependenciesToUpdate;
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

    public Integer getNpmDependenciesToUpdate() {
      return npmDependenciesToUpdate;
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

    public static class ProcessRepository implements Serializable {
      private final String repoName;
      private final Boolean isPrCreated;
      private final Boolean isPrCreateError;

      private String repoType;
      private Boolean isPrMerged;

      @JsonCreator
      public ProcessRepository(
          @JsonProperty("repoName") final String repoName,
          @JsonProperty("isPrCreated") final Boolean isPrCreated,
          @JsonProperty("isPrCreateError") final Boolean isPrCreateError,
          @JsonProperty("repoType") final String repoType,
          @JsonProperty("isPrMerged") final Boolean isPrMerged) {
        this.repoName = repoName;
        this.isPrCreated = isPrCreated;
        this.isPrCreateError = isPrCreateError;
        this.repoType = repoType;
        this.isPrMerged = isPrMerged;
      }

      public ProcessRepository(
          final String repoName, final Boolean isPrCreated, final Boolean isPrCreateError) {
        this.repoName = repoName;
        this.repoType = null;
        this.isPrCreated = isPrCreated;
        this.isPrCreateError = isPrCreateError;
        this.isPrMerged = Boolean.FALSE;
      }

      public String getRepoName() {
        return repoName;
      }

      public Boolean getPrCreated() {
        return isPrCreated;
      }

      public Boolean getPrCreateError() {
        return isPrCreateError;
      }

      public String getRepoType() {
        return repoType;
      }

      public void setRepoType(final String repoType) {
        this.repoType = repoType;
      }

      public Boolean getPrMerged() {
        return isPrMerged;
      }

      public void setPrMerged(final boolean isPrMerged) {
        this.isPrMerged = isPrMerged;
      }

      @Override
      public String toString() {
        return "Repository{"
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
}
