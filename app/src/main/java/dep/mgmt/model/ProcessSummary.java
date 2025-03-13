package dep.mgmt.model;

import dep.mgmt.model.entity.ProcessSummaryEntity;
import java.io.Serializable;
import java.util.List;

public class ProcessSummary implements Serializable {
  private final List<ProcessSummaryEntity> processSummaries;
  private final Integer currentPage;
  private final Integer totalPages;
  private final Integer totalElements;
  private final Integer pageSize;

  public ProcessSummary(List<ProcessSummaryEntity> processSummaries) {
    this.processSummaries = processSummaries;
    this.currentPage = 1;
    this.totalPages = 0;
    this.totalElements = 0;
    this.pageSize = 100;
  }

  public ProcessSummary(
      List<ProcessSummaryEntity> processSummaries,
      Integer currentPage,
      Integer totalPages,
      Integer totalElements,
      Integer pageSize) {
    this.processSummaries = processSummaries;
    this.currentPage = currentPage;
    this.totalPages = totalPages;
    this.totalElements = totalElements;
    this.pageSize = pageSize;
  }

  public static class ProcessRepository implements Serializable {
    private final String repoName;
    private final Boolean isPrCreated;
    private final Boolean isPrCreateError;

    private String repoType;
    private Boolean isPrMerged;

    public ProcessRepository(
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
