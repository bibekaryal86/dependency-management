package dep.mgmt.migration.entities_old;

import java.io.Serializable;

public class ProcessedRepository implements Serializable {
  private String repoName;
  private String repoType;
  private boolean isPrCreated;
  private boolean isPrCreateError;
  private boolean isPrMerged;

  public ProcessedRepository() {}

  public ProcessedRepository(
      String repoName,
      String repoType,
      boolean isPrCreated,
      boolean isPrCreateError,
      boolean isPrMerged) {
    this.repoName = repoName;
    this.repoType = repoType;
    this.isPrCreated = isPrCreated;
    this.isPrCreateError = isPrCreateError;
    this.isPrMerged = isPrMerged;
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

  public boolean isPrCreated() {
    return isPrCreated;
  }

  public void setPrCreated(boolean prCreated) {
    isPrCreated = prCreated;
  }

  public boolean isPrCreateError() {
    return isPrCreateError;
  }

  public void setPrCreateError(boolean prCreateError) {
    isPrCreateError = prCreateError;
  }

  public boolean isPrMerged() {
    return isPrMerged;
  }

  public void setPrMerged(boolean prMerged) {
    isPrMerged = prMerged;
  }
}
