package dep.mgmt.model;

import dep.mgmt.model.enums.RequestParams;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class ProcessSummary implements Serializable {
  private final List<Summary> summaries;
    private final Integer currentPage;
    private final Integer totalPages;
    private final Integer totalElements;
    private final Integer pageSize;

  public ProcessSummary(List<Summary> summaries) {
    this.summaries = summaries;
    this.currentPage = 1;
    this.totalPages = 0;
    this.totalElements = 0;
    this.pageSize = 100;
  }

    public ProcessSummary(List<Summary> summaries, Integer currentPage, Integer totalPages, Integer totalElements, Integer pageSize) {
        this.summaries = summaries;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.pageSize = pageSize;
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
      private final Integer gradlePluginsToUpdate;
      private final Integer gradleDependenciesToUpdate;
      private final Integer pythonPackagesToUpdate;
      private final Integer npmDependenciesToUpdate;
      private final Integer totalPrCreatedCount;
      private final Integer totalPrCreateErrorsCount;
      private final Integer totalPrMergedCount;
      private final List<Repository> repositories;
      private final Boolean isErrorsOrExceptions;

      public Summary(
              final RequestParams.UpdateType updateType,
              final Integer gradlePluginsToUpdate,
              final Integer gradleDependenciesToUpdate,
              final Integer pythonPackagesToUpdate,
              final Integer npmDependenciesToUpdate,
              final Integer totalPrCreatedCount,
              final Integer totalPrCreateErrorsCount,
              final Integer totalPrMergedCount,
              final List<Repository> repositories,
              final Boolean isErrorsOrExceptions) {
          this.updateType = updateType;
          this.gradlePluginsToUpdate = gradlePluginsToUpdate;
          this.gradleDependenciesToUpdate = gradleDependenciesToUpdate;
          this.pythonPackagesToUpdate = pythonPackagesToUpdate;
          this.npmDependenciesToUpdate = npmDependenciesToUpdate;
          this.totalPrCreatedCount = totalPrCreatedCount;
          this.totalPrCreateErrorsCount = totalPrCreateErrorsCount;
          this.totalPrMergedCount = totalPrMergedCount;
          this.repositories = repositories == null ? Collections.emptyList() : repositories;
          this.isErrorsOrExceptions = isErrorsOrExceptions;
      }

      public RequestParams.UpdateType getUpdateType() {
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

      public List<Repository> getRepositories() {
          return repositories;
      }

      public Boolean getErrorsOrExceptions() {
          return isErrorsOrExceptions;
      }

      @Override
      public String toString() {
          return "Summary{" +
                  "updateType=" + updateType +
                  ", gradlePluginsToUpdate=" + gradlePluginsToUpdate +
                  ", gradleDependenciesToUpdate=" + gradleDependenciesToUpdate +
                  ", pythonPackagesToUpdate=" + pythonPackagesToUpdate +
                  ", npmDependenciesToUpdate=" + npmDependenciesToUpdate +
                  ", totalPrCreatedCount=" + totalPrCreatedCount +
                  ", totalPrCreateErrorsCount=" + totalPrCreateErrorsCount +
                  ", totalPrMergedCount=" + totalPrMergedCount +
                  ", repositories=" + repositories +
                  ", isErrorsOrExceptions=" + isErrorsOrExceptions +
                  '}';
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
              return "Repository{" +
                      "repoName='" + repoName + '\'' +
                      ", isPrCreated=" + isPrCreated +
                      ", isPrCreateError=" + isPrCreateError +
                      ", repoType='" + repoType + '\'' +
                      ", isPrMerged=" + isPrMerged +
                      '}';
          }
      }
  }
}
