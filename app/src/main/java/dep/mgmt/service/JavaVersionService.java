package dep.mgmt.service;

import com.fasterxml.jackson.core.type.TypeReference;
import dep.mgmt.model.LatestVersion;
import dep.mgmt.model.web.JavaReleaseResponse;
import dep.mgmt.util.ConstantUtils;
import io.github.bibekaryal86.shdsvc.Connector;
import io.github.bibekaryal86.shdsvc.dtos.Enums;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaVersionService {

  private static final Logger log = LoggerFactory.getLogger(JavaVersionService.class);

  private final DockerVersionService dockerVersionService;

  public JavaVersionService() {
    this.dockerVersionService = new DockerVersionService();
  }

  public LatestVersion getJavaVersion(
      final String latestGcpRuntimeVersion, final String latestDockerVersionFromMongo) {
    List<JavaReleaseResponse.JavaVersion> javaReleaseVersions = getJavaReleaseVersions();

    // get rid of non lts and sort by version descending
    JavaReleaseResponse.JavaVersion latestJavaRelease =
        javaReleaseVersions.stream()
            .filter(javaVersion -> ConstantUtils.VERSION_LTS.equals(javaVersion.getOptional()))
            .findFirst()
            .orElse(null);

    if (latestJavaRelease == null) {
      log.error("Latest Java Release is null...");
      return null;
    }

    final String versionActual = latestJavaRelease.getSemver();
    final String versionFull = getVersionFull(versionActual);
    final String versionMajor = getVersionMajor(versionFull);
    final String versionDocker = getVersionDocker(versionMajor, latestDockerVersionFromMongo);
    final String versionGcp = getVersionGcp(versionMajor, latestGcpRuntimeVersion);
    return new LatestVersion(versionActual, versionFull, versionMajor, versionDocker, versionGcp);
  }

  public List<JavaReleaseResponse.JavaVersion> getJavaReleaseVersions() {
    try {
      final String url = ConstantUtils.JAVA_RELEASES_ENDPOINT;
      return Connector.sendRequestNoEx(
              ConstantUtils.JAVA_RELEASES_ENDPOINT,
              Enums.HttpMethod.GET,
              new TypeReference<JavaReleaseResponse>() {},
              null,
              null,
              null)
          .responseBody()
          .getVersions();
    } catch (Exception ex) {
      log.error("ERROR Get Java Releases", ex);
    }
    return Collections.emptyList();
  }

  /**
   * @param versionActual eg: 21.0.4+7.0.LTS
   * @return eg: 21.0,4
   */
  private String getVersionFull(final String versionActual) {
    return versionActual.trim().split("\\+")[0];
  }

  /**
   * @param versionFull eg: 21.0.4
   * @return eg: 21
   */
  private String getVersionMajor(final String versionFull) {
    return versionFull.trim().split("\\.")[0];
  }

  /**
   * @param versionMajor eg: 21
   * @return eg: eclipse-temurin:21-jre-alpine
   */
  private String getVersionDocker(
      final String versionMajor, final String latestDockerVersionFromMongo) {
    final String library = ConstantUtils.DOCKER_JRE;
    final String tag =
        versionMajor + "-" + ConstantUtils.JAVA_JRE + "-" + ConstantUtils.DOCKER_ALPINE;
    final boolean isNewDockerImageExists =
        dockerVersionService.checkDockerVersionExists(library, tag);
    if (isNewDockerImageExists) {
      return library + ":" + tag;
    }
    return latestDockerVersionFromMongo;
  }

  /**
   * @param versionMajor eg: 21
   * @return eg: java21
   */
  private String getVersionGcp(final String versionMajor, final String latestGcpRuntimeVersion) {
    if (CommonUtilities.parseIntNoEx(versionMajor)
        > CommonUtilities.parseIntNoEx(latestGcpRuntimeVersion)) {
      return ConstantUtils.JAVA_NAME + latestGcpRuntimeVersion;
    }
    return ConstantUtils.JAVA_NAME + versionMajor;
  }
}
