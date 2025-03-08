package dep.mgmt.service;

import dep.mgmt.model.LatestVersion;

public class GithubActionsVersionService extends VersionLookupGithubApi {

  public LatestVersion getGithubActionsCheckoutVersion() {
    return getGithubApiLatestVersion("actions", "checkout", Boolean.FALSE);
  }

  public LatestVersion getGithubActionsSetupJavaVersion() {
    return getGithubApiLatestVersion("actions", "setup-java", Boolean.FALSE);
  }

  public LatestVersion getGithubActionsSetupNodeVersion() {
    return getGithubApiLatestVersion("actions", "setup-node", Boolean.FALSE);
  }

  public LatestVersion getGithubActionsSetupPythonVersion() {
    return getGithubApiLatestVersion("actions", "setup-python", Boolean.FALSE);
  }

  public LatestVersion getGithubActionsGradleVersion() {
    return getGithubApiLatestVersion("gradle", "actions", Boolean.FALSE);
  }

  public LatestVersion getGithubActionsCodeqlActionVersion() {
    return getGithubApiLatestVersion("github", "codeql-action", Boolean.FALSE);
  }
}
