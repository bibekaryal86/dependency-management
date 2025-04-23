package dep.mgmt.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dep.mgmt.model.enums.RequestParams;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class RequestMetadata implements Serializable {

  private final RequestParams.UpdateType updateType;
  private final Boolean isRecreateCaches;
  private final Boolean isRecreateScriptFiles;
  private final Boolean isGithubResetRequired;
  private final Boolean isGithubPullRequired;
  private final Boolean isProcessSummaryRequired;
  private final Boolean isDeleteUpdateDependenciesOnly;
  private final Boolean isIncludeDebugLogs;
  private final LocalDate branchDate;
  private final String repoName;

  @JsonCreator
  public RequestMetadata(
      @JsonProperty("updateType") RequestParams.UpdateType updateType,
      @JsonProperty("isRecreateCaches") Boolean isRecreateCaches,
      @JsonProperty("isRecreateScriptFiles") Boolean isRecreateScriptFiles,
      @JsonProperty("isGithubResetRequired") Boolean isGithubResetRequired,
      @JsonProperty("isGithubPullRequired") Boolean isGithubPullRequired,
      @JsonProperty("isProcessSummaryRequired") Boolean isProcessSummaryRequired,
      @JsonProperty("isDeleteUpdateDependenciesOnly") Boolean isDeleteUpdateDependenciesOnly,
      @JsonProperty("isIncludeDebugLogs") Boolean isIncludeDebugLogs,
      @JsonProperty("branchDate") LocalDate branchDate,
      @JsonProperty("repoName") String repoName) {
    this.updateType = updateType;
    this.isRecreateCaches = isRecreateCaches;
    this.isRecreateScriptFiles = isRecreateScriptFiles;
    this.isGithubResetRequired = isGithubResetRequired;
    this.isGithubPullRequired = isGithubPullRequired;
    this.isProcessSummaryRequired = isProcessSummaryRequired;
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

  public Boolean getGithubResetRequired() {
    return isGithubResetRequired;
  }

  public Boolean getIsGithubPullRequired() {
    return isGithubPullRequired;
  }

  public Boolean getProcessSummaryRequired() {
    return isProcessSummaryRequired;
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
    return updateType == that.updateType
        && Objects.equals(isRecreateCaches, that.isRecreateCaches)
        && Objects.equals(isRecreateScriptFiles, that.isRecreateScriptFiles)
        && Objects.equals(isGithubResetRequired, that.isGithubResetRequired)
        && Objects.equals(isGithubPullRequired, that.isGithubPullRequired)
        && Objects.equals(isProcessSummaryRequired, that.isProcessSummaryRequired)
        && Objects.equals(isDeleteUpdateDependenciesOnly, that.isDeleteUpdateDependenciesOnly)
        && Objects.equals(isIncludeDebugLogs, that.isIncludeDebugLogs)
        && Objects.equals(branchDate, that.branchDate)
        && Objects.equals(repoName, that.repoName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        updateType,
        isRecreateCaches,
        isRecreateScriptFiles,
        isGithubResetRequired,
        isGithubPullRequired,
        isProcessSummaryRequired,
        isDeleteUpdateDependenciesOnly,
        isIncludeDebugLogs,
        branchDate,
        repoName);
  }

  @Override
  public String toString() {
    return "RequestMetadata{"
        + "updateType="
        + updateType
        + ", isRecreateCaches="
        + isRecreateCaches
        + ", isRecreateScriptFiles="
        + isRecreateScriptFiles
        + ", isGithubResetRequired="
        + isGithubResetRequired
        + ", isGithubPullRequired="
        + isGithubPullRequired
        + ", isProcessSummaryRequired="
        + isProcessSummaryRequired
        + ", isDeleteUpdateDependenciesOnly="
        + isDeleteUpdateDependenciesOnly
        + ", isIncludeDebugLogs="
        + isIncludeDebugLogs
        + ", branchDate="
        + branchDate
        + ", repoName='"
        + repoName
        + '\''
        + '}';
  }
}
