package dep.mgmt.service;

import dep.mgmt.model.LatestVersion;
import dep.mgmt.util.ConstantUtils;

public class FlywayVersionService extends VersionLookupGithubApi {

  public LatestVersion getFlywayVersion() {
    return getGithubApiLatestVersion(ConstantUtils.FLYWAY_NAME, ConstantUtils.FLYWAY_NAME);
  }
}
