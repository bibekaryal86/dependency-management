package dep.mgmt.service;

import dep.mgmt.model.LatestVersion;
import dep.mgmt.util.ConstantUtils;

public class GithubActionsVersionService extends VersionLookupGithubApi {

  public LatestVersion getGithubActionsCheckoutVersion() {
    return getGithubApiLatestVersion(ConstantUtils.GITHUB_ACTIONS_NAME, ConstantUtils.GITHUB_ACTIONS_CHECKOUT);
  }

  public LatestVersion getGithubActionsSetupJavaVersion() {
    return getGithubApiLatestVersion(ConstantUtils.GITHUB_ACTIONS_NAME, ConstantUtils.GITHUB_ACTIONS_SETUP_JAVA);
  }

  public LatestVersion getGithubActionsSetupNodeVersion() {
    return getGithubApiLatestVersion(ConstantUtils.GITHUB_ACTIONS_NAME, ConstantUtils.GITHUB_ACTIONS_SETUP_NODE);
  }

  public LatestVersion getGithubActionsSetupPythonVersion() {
    return getGithubApiLatestVersion(ConstantUtils.GITHUB_ACTIONS_NAME, ConstantUtils.GITHUB_ACTIONS_SETUP_PYTHON);
  }

  public LatestVersion getGithubActionsGradleVersion() {
    return getGithubApiLatestVersion(ConstantUtils.GRADLE_NAME, ConstantUtils.GITHUB_ACTIONS_NAME);
  }

  public LatestVersion getGithubActionsCodeqlActionVersion() {
    return getGithubApiLatestVersion(ConstantUtils.GITHUB_NAME, ConstantUtils.GITHUB_ACTIONS_CODEQL);
  }
}
