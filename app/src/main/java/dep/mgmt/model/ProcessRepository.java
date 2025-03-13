package dep.mgmt.model;

import java.io.Serializable;

public class ProcessRepository implements Serializable {
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
