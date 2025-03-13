package dep.mgmt.service;

import dep.mgmt.model.LatestVersion;

public class GithubActionsVersionService extends VersionLookupGithubApi {

  public LatestVersion getGithubActionsCheckoutVersion() {
    return getGithubApiLatestVersion("actions", "checkout");
  }

  public LatestVersion getGithubActionsSetupJavaVersion() {
    return getGithubApiLatestVersion("actions", "setup-java");
  }

  public LatestVersion getGithubActionsSetupNodeVersion() {
    return getGithubApiLatestVersion("actions", "setup-node");
  }

  public LatestVersion getGithubActionsSetupPythonVersion() {
    return getGithubApiLatestVersion("actions", "setup-python");
  }

  public LatestVersion getGithubActionsGradleVersion() {
    return getGithubApiLatestVersion("gradle", "actions");
  }

  public LatestVersion getGithubActionsCodeqlActionVersion() {
    return getGithubApiLatestVersion("github", "codeql-action");
  }
}
