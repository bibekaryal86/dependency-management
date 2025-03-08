package dep.mgmt.service;

import dep.mgmt.model.LatestVersion;
import dep.mgmt.model.web.ApiReleaseResponse;
import dep.mgmt.util.ConstantUtils;

public class GradleVersionService extends VersionLookupGithubApi {

  private final DockerVersionService dockerVersionService;

  public GradleVersionService() {
    this.dockerVersionService = new DockerVersionService();
  }

  public LatestVersion getGradleVersion(
      final String latestJavaVersionMajor, final String latestDockerVersionFromMongo) {
    final ApiReleaseResponse apiReleaseResponse =
        getGithubApiReleaseResponse("gradle", "gradle", Boolean.FALSE);
    final String versionFull = apiReleaseResponse.getName();
    final String versionDocker =
        getGradleVersionDocker(versionFull, latestJavaVersionMajor, latestDockerVersionFromMongo);
    return new LatestVersion(versionFull, versionFull, null, versionDocker, null);
  }

  /**
   * @param versionFull eg: 8.10 or 8.10.2
   * @return eg: 8.10-jdk21-alpine or 8.10.2-jdk21-alpine
   */
  private String getGradleVersionDocker(
      final String versionFull,
      final String latestJavaVersionMajor,
      final String latestDockerVersionFromMongo) {
    final String library = "gradle";
    final String tag =
        versionFull + "-jdk" + latestJavaVersionMajor + "-" + ConstantUtils.DOCKER_ALPINE;
    final boolean isNewDockerImageExists =
        this.dockerVersionService.checkDockerVersionExists(library, tag);
    if (isNewDockerImageExists) {
      return library + ":" + tag;
    }
    return latestDockerVersionFromMongo;
  }
}
