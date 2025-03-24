package dep.mgmt.service;

import dep.mgmt.model.LatestVersion;
import dep.mgmt.model.web.ApiReleaseResponse;
import dep.mgmt.util.ConstantUtils;

public class NginxVersionService extends VersionLookupGithubApi {

  private final DockerVersionService dockerVersionService;

  public NginxVersionService() {
    this.dockerVersionService = new DockerVersionService();
  }

  public LatestVersion getNginxVersion(final String latestDockerVersionFromMongo) {
    final ApiReleaseResponse apiReleaseResponse =
        getGithubApiReleaseResponse(
            ConstantUtils.NGINX_NAME, ConstantUtils.NGINX_NAME, Boolean.TRUE);

    if (apiReleaseResponse == null) {
      return null;
    }

    final String versionActual = apiReleaseResponse.getName();
    final String versionFull = getVersionFull(versionActual);
    final String versionDocker = getVersionDocker(versionFull, latestDockerVersionFromMongo);

    return new LatestVersion(versionActual, versionFull, null, versionDocker, null);
  }

  /**
   * @param versionActual eg: release-1.27.2
   * @return eg: 1.27.2
   */
  private String getVersionFull(final String versionActual) {
    return versionActual.trim().split("-")[1];
  }

  /**
   * @param versionFull eg: 1.27.2
   * @return eg: nginx:1.27.2-alpine
   */
  private String getVersionDocker(
      final String versionFull, final String latestDockerVersionFromMongo) {
    final String library = ConstantUtils.NGINX_NAME;
    final String tag = versionFull + "-" + ConstantUtils.DOCKER_ALPINE;
    final boolean isNewDockerImageExists =
        dockerVersionService.checkDockerVersionExists(library, tag);
    if (isNewDockerImageExists) {
      return library + ":" + tag;
    }
    return latestDockerVersionFromMongo;
  }
}
