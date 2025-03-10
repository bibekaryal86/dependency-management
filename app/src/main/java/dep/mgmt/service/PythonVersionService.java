package dep.mgmt.service;

import dep.mgmt.model.LatestVersion;
import dep.mgmt.model.web.ApiReleaseResponse;
import dep.mgmt.util.ConstantUtils;
import dep.mgmt.util.VersionUtils;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PythonVersionService extends VersionLookupGithubApi {

  private static final Logger log = LoggerFactory.getLogger(PythonVersionService.class);

  private final DockerVersionService dockerVersionService;

  public PythonVersionService() {
    this.dockerVersionService = new DockerVersionService();
  }

  public LatestVersion getPythonVersion(
      final String latestGcpRuntimeVersion, final String latestDockerVersionFromMongo) {
    ApiReleaseResponse apiReleaseResponse =
        getGithubApiReleaseResponse("python", "cpython", Boolean.TRUE);

    if (apiReleaseResponse == null) {
      return null;
    }

    final String versionActual = apiReleaseResponse.getName();
    final String versionFull = getVersionFull(versionActual);
    final String versionDocker = getVersionDocker(versionFull, latestDockerVersionFromMongo);
    final String versionGcp = getVersionGcp(versionFull, latestGcpRuntimeVersion);
    return new LatestVersion(versionActual, versionFull, null, versionDocker, versionGcp);
  }

  /**
   * @param versionActual eg: v3.12.7
   * @return eg: 3.12.7
   */
  private String getVersionFull(final String versionActual) {
    return versionActual.replaceAll("[^0-9.]", "");
  }

  /**
   * @param versionFull eg: 3.12 or 3.12.7
   * @return eg: python:3.12-alpine or python:3.12.7-alpine
   */
  private String getVersionDocker(
      final String versionFull, final String latestDockerVersionFromMongo) {
    final String library = "python";
    final String tag = versionFull + "-" + ConstantUtils.DOCKER_ALPINE;
    final boolean isNewDockerImageExists =
        dockerVersionService.checkDockerVersionExists(library, tag);
    if (isNewDockerImageExists) {
      return library + ":" + tag;
    }
    return latestDockerVersionFromMongo;
  }

  /**
   * @param versionFull eg: 3.12.7
   * @return eg: python312
   */
  private String getVersionGcp(final String versionFull, final String latestGcpRuntimeVersion) {
    final String versionMajorMinor = VersionUtils.getVersionMajorMinor(versionFull, false);
    if (CommonUtilities.parseIntNoEx(versionMajorMinor)
        > CommonUtilities.parseIntNoEx(latestGcpRuntimeVersion)) {
      return "python" + latestGcpRuntimeVersion;
    }
    return "python" + versionMajorMinor;
  }
}
