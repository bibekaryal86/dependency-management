package dep.mgmt.service;

import com.fasterxml.jackson.core.type.TypeReference;
import dep.mgmt.model.LatestVersion;
import dep.mgmt.model.web.NodeReleaseResponse;
import dep.mgmt.util.ConstantUtils;
import io.github.bibekaryal86.shdsvc.Connector;
import io.github.bibekaryal86.shdsvc.dtos.Enums;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class NodeVersionService {

  private static final Logger log = LoggerFactory.getLogger(NodeVersionService.class);

  private final DockerVersionService dockerVersionService;

  public NodeVersionService() {
    this.dockerVersionService = new DockerVersionService();
  }

  public LatestVersion getLatestNodeVersion(
      final String latestGcpRuntimeVersion, final String latestDockerVersionFromMongo) {
    List<NodeReleaseResponse> nodeReleaseResponses = getNodeReleases();
    // get rid of non lts and sort by version descending
    Optional<NodeReleaseResponse> optionalNodeReleaseResponse =
        nodeReleaseResponses.stream()
            .filter(nodeReleaseResponse -> !"false".equals(nodeReleaseResponse.getLts()))
            .findFirst();

    NodeReleaseResponse latestNodeRelease = optionalNodeReleaseResponse.orElse(null);
    log.info("Latest Node Release: [ {} ]", latestNodeRelease);

    if (latestNodeRelease == null) {
      log.error("Latest Node Release Null Error...");
      return null;
    }

    final String versionActual = latestNodeRelease.getVersion();
    final String versionFull = getVersionFull(versionActual);
    final String versionMajor = getVersionMajor(versionFull);
    final String versionDocker = getVersionDocker(versionMajor, latestDockerVersionFromMongo);
    final String versionGcp = getVersionGcp(versionMajor, latestGcpRuntimeVersion);
    return new LatestVersion(versionActual, versionFull, versionMajor, versionDocker, versionGcp);
  }

  private List<NodeReleaseResponse> getNodeReleases() {
    try {
      return Connector.sendRequest(ConstantUtils.NODE_RELEASES_ENDPOINT, Enums.HttpMethod.GET, new TypeReference<List<NodeReleaseResponse>>() {}, null, null, null).responseBody();
    } catch (Exception ex) {
      log.error("Get Node Releases Error...", ex);
    }
    return Collections.emptyList();
  }

  /**
   * @param versionActual eg: v20.18.0
   * @return eg: 20.18.0
   */
  private String getVersionFull(final String versionActual) {
    return versionActual.replaceAll("[^0-9.]", "");
  }

  /**
   * @param versionFull eg: 20.18.0
   * @return eg: 20
   */
  private String getVersionMajor(final String versionFull) {
    return versionFull.trim().split("\\.")[0];
  }

  /**
   * @param versionMajor eg: 20
   * @return eg: node:20-alpine
   */
  private String getVersionDocker(
      final String versionMajor, final String latestDockerVersionFromMongo) {
    final String library = "node";
    final String tag = versionMajor + "-" + ConstantUtils.DOCKER_ALPINE;
    final boolean isNewDockerImageExists = dockerVersionService.checkDockerVersionExists(library, tag);
    if (isNewDockerImageExists) {
      return library + ":" + tag;
    }
    return latestDockerVersionFromMongo;
  }

  /**
   * @param versionMajor eg: 20
   * @return eg: nodejs20
   */
  private String getVersionGcp(final String versionMajor, final String latestGcpRuntimeVersion) {
    if (CommonUtilities.parseIntNoEx(versionMajor) > CommonUtilities.parseIntNoEx(latestGcpRuntimeVersion)) {
      return "nodejs" + latestGcpRuntimeVersion;
    }
    return "nodejs" + versionMajor;
  }
}
