package dep.mgmt.model;

import dep.mgmt.model.enums.RequestParams;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class ProcessSummary implements Serializable {
  private final List<Summary> summaries;

  public ProcessSummary(List<Summary> summaries) {
    this.summaries = summaries;
  }

  public List<Summary> getSummaries() {
    return summaries;
  }

  @Override
  public String toString() {
    return "ProcessSummary{" + "summaries=" + summaries + '}';
  }

  public static class Summary implements Serializable {
    private final RequestParams.UpdateType updateType;
    private final Integer mongoPluginsToUpdate;
    private final Integer mongoDependenciesToUpdate;
    private final Integer mongoPackagesToUpdate;
    private final Integer mongoNpmSkipsActive;
    private final Integer totalPrCreatedCount;
    private final Integer totalPrCreateErrorsCount;
    private final Integer totalPrMergedCount;
    private final List<Repository> repositories;
    private final Boolean isErrorsOrExceptions;

    public Summary(
        final RequestParams.UpdateType updateType,
        final Integer mongoPluginsToUpdate,
        final Integer mongoDependenciesToUpdate,
        final Integer mongoPackagesToUpdate,
        final Integer mongoNpmSkipsActive,
        final Integer totalPrCreatedCount,
        final Integer totalPrCreateErrorsCount,
        final Integer totalPrMergedCount,
        final List<Repository> repositories,
        final Boolean isErrorsOrExceptions) {
      this.updateType = updateType;
      this.mongoPluginsToUpdate = mongoPluginsToUpdate;
      this.mongoDependenciesToUpdate = mongoDependenciesToUpdate;
      this.mongoPackagesToUpdate = mongoPackagesToUpdate;
      this.mongoNpmSkipsActive = mongoNpmSkipsActive;
      this.totalPrCreatedCount = totalPrCreatedCount;
      this.totalPrCreateErrorsCount = totalPrCreateErrorsCount;
      this.totalPrMergedCount = totalPrMergedCount;
      this.repositories = repositories == null ? Collections.emptyList() : repositories;
      this.isErrorsOrExceptions = isErrorsOrExceptions;
    }

    public RequestParams.UpdateType getUpdateType() {
      return updateType;
    }

    public Integer getMongoPluginsToUpdate() {
      return mongoPluginsToUpdate;
    }

    public Integer getMongoDependenciesToUpdate() {
      return mongoDependenciesToUpdate;
    }

    public Integer getMongoPackagesToUpdate() {
      return mongoPackagesToUpdate;
    }

    public Integer getMongoNpmSkipsActive() {
      return mongoNpmSkipsActive;
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

    public List<Repository> getRepositories() {
      return repositories;
    }

    public Boolean getErrorsOrExceptions() {
      return isErrorsOrExceptions;
    }

    @Override
    public String toString() {
      return "Summary{"
          + "updateType="
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
          + ", repositories="
          + repositories.size()
          + ", isErrorsOrExceptions="
          + isErrorsOrExceptions
          + '}';
    }

    public static class Repository implements Serializable {
      private final String repoName;
      private final Boolean isPrCreated;
      private final Boolean isPrCreateError;

      private String repoType;
      private Boolean isPrMerged;

      public Repository(
          final String repoName,
          final Boolean isPrCreated,
          final Boolean isPrCreateError,
          final String repoType,
          final Boolean isPrMerged) {
        this.repoName = repoName;
        this.isPrCreated = isPrCreated;
        this.isPrCreateError = isPrCreateError;
        this.repoType = repoType;
        this.isPrMerged = isPrMerged;
      }

      public Repository(
              final String repoName,
              final Boolean isPrCreated,
              final Boolean isPrCreateError) {
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
            + ", repoType='"
            + repoType
            + '\''
            + ", isPrCreated="
            + isPrCreated
            + ", isPrCreateError="
            + isPrCreateError
            + ", isPrMerged="
            + isPrMerged
            + '}';
      }
    }
  }
}
