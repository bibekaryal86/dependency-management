package dep.mgmt.service;

import dep.mgmt.model.LatestVersion;

public class FlywayVersionService extends VersionLookupGithubApi {

  public LatestVersion getFlywayVersion() {
    return getGithubApiLatestVersion("flyway", "flyway", Boolean.FALSE);
  }
}
