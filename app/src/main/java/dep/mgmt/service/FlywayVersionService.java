package dep.mgmt.service;

import dep.mgmt.model.LatestVersion;
import dep.mgmt.util.ConstantUtils;

public class FlywayVersionService extends VersionLookupGithubApi {

  private final DockerVersionService dockerVersionService;

  public FlywayVersionService() {
    this.dockerVersionService = new DockerVersionService();
  }

  public LatestVersion getFlywayVersion(final String latestDockerVersionFromMongo) {
    final LatestVersion latestVersionFlyway =
        getGithubApiLatestVersion(ConstantUtils.FLYWAY_NAME, ConstantUtils.FLYWAY_NAME);
    final String latestVersionDocker =
        getVersionDocker(latestVersionFlyway.getVersionFull(), latestDockerVersionFromMongo);
    latestVersionFlyway.setVersionDocker(latestVersionDocker);
    return latestVersionFlyway;
  }

  /**
   * @param versionFull eg: 11.8.1
   * @return eg: flyway:11.8.1-alpine
   */
  private String getVersionDocker(
      final String versionFull, final String latestDockerVersionFromMongo) {
    final String library = ConstantUtils.FLYWAY_NAME;
    final String tag = versionFull + "-" + ConstantUtils.DOCKER_ALPINE;
    final boolean isNewDockerImageExists =
        dockerVersionService.checkDockerVersionExists(library, tag);
    if (isNewDockerImageExists) {
      return library + ":" + tag;
    }
    return latestDockerVersionFromMongo;
  }
}
