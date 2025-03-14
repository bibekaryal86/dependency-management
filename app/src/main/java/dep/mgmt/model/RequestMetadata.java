package dep.mgmt.model;

import dep.mgmt.model.enums.RequestParams;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class RequestMetadata implements Serializable {

  private final RequestParams.UpdateType updateType;
  private final Boolean isRecreateCaches;
  private final Boolean isRecreateScriptFiles;
  private final Boolean isGithubResetPullRequired;
  private final Boolean isProcessSummaryRequired;
  private final Boolean isForceCreatePr;
  private final Boolean isDeleteUpdateDependenciesOnly;
  private final Boolean isIncludeDebugLogs;
  private final LocalDate branchDate;
  private final String repoName;

  public RequestMetadata(
      RequestParams.UpdateType updateType,
      boolean isRecreateCaches,
      boolean isRecreateScriptFiles,
      boolean isGithubResetPullRequired,
      boolean isProcessSummaryRequired,
      boolean isForceCreatePr,
      boolean isDeleteUpdateDependenciesOnly,
      boolean isIncludeDebugLogs,
      LocalDate branchDate,
      String repoName) {
    this.updateType = updateType;
    this.isRecreateCaches = isRecreateCaches;
    this.isRecreateScriptFiles = isRecreateScriptFiles;
    this.isGithubResetPullRequired = isGithubResetPullRequired;
    this.isProcessSummaryRequired = isProcessSummaryRequired;
    this.isForceCreatePr = isForceCreatePr;
    this.isDeleteUpdateDependenciesOnly = isDeleteUpdateDependenciesOnly;
    this.isIncludeDebugLogs = isIncludeDebugLogs;
    this.branchDate = branchDate;
    this.repoName = repoName;
  }

  public RequestParams.UpdateType getUpdateType() {
    return updateType;
  }

  public Boolean getRecreateCaches() {
    return isRecreateCaches;
  }

  public Boolean getRecreateScriptFiles() {
    return isRecreateScriptFiles;
  }

  public Boolean getGithubResetPullRequired() {
    return isGithubResetPullRequired;
  }

  public Boolean getProcessSummaryRequired() {
    return isProcessSummaryRequired;
  }

  public Boolean getForceCreatePr() {
    return isForceCreatePr;
  }

  public Boolean getDeleteUpdateDependenciesOnly() {
    return isDeleteUpdateDependenciesOnly;
  }

  public Boolean getIncludeDebugLogs() {
    return isIncludeDebugLogs;
  }

  public LocalDate getBranchDate() {
    return branchDate;
  }

  public String getRepoName() {
    return repoName;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (!(object instanceof RequestMetadata that)) return false;
      return updateType == that.updateType && Objects.equals(isRecreateCaches, that.isRecreateCaches) && Objects.equals(isRecreateScriptFiles, that.isRecreateScriptFiles) && Objects.equals(isGithubResetPullRequired, that.isGithubResetPullRequired) && Objects.equals(isProcessSummaryRequired, that.isProcessSummaryRequired) && Objects.equals(isForceCreatePr, that.isForceCreatePr) && Objects.equals(isDeleteUpdateDependenciesOnly, that.isDeleteUpdateDependenciesOnly) && Objects.equals(isIncludeDebugLogs, that.isIncludeDebugLogs) && Objects.equals(branchDate, that.branchDate) && Objects.equals(repoName, that.repoName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(updateType, isRecreateCaches, isRecreateScriptFiles, isGithubResetPullRequired, isProcessSummaryRequired, isForceCreatePr, isDeleteUpdateDependenciesOnly, isIncludeDebugLogs, branchDate, repoName);
  }

  @Override
  public String toString() {
    return "RequestMetadata{" +
            "updateType=" + updateType +
            ", isRecreateCaches=" + isRecreateCaches +
            ", isRecreateScriptFiles=" + isRecreateScriptFiles +
            ", isGithubResetPullRequired=" + isGithubResetPullRequired +
            ", isProcessSummaryRequired=" + isProcessSummaryRequired +
            ", isForceCreatePr=" + isForceCreatePr +
            ", isDeleteUpdateDependenciesOnly=" + isDeleteUpdateDependenciesOnly +
            ", isIncludeDebugLogs=" + isIncludeDebugLogs +
            ", branchDate=" + branchDate +
            ", repoName='" + repoName + '\'' +
            '}';
  }
}
