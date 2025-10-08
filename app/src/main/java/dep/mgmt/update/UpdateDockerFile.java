package dep.mgmt.update;

import dep.mgmt.model.AppDataLatestVersions;
import dep.mgmt.model.AppDataRepository;
import dep.mgmt.util.ConstantUtils;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateDockerFile {
  private static final Logger log = LoggerFactory.getLogger(UpdateDockerFile.class);

  public static boolean execute(
      final AppDataRepository repository, final AppDataLatestVersions latestVersions) {
    final Path dockerfilePath = repository.getRepoPath().resolve("Dockerfile");
    if (Files.exists(dockerfilePath)) {
      List<String> dockerfileData = readDockerfile(dockerfilePath, repository);
      dockerfileData = updateDockerfile(dockerfileData, latestVersions);
      return writeDockerfile(dockerfileData, dockerfilePath, repository);
    }
    return false;
  }

  private static List<String> readDockerfile(
      final Path dockerfilePath, final AppDataRepository repository) {
    try {
      return Files.readAllLines(dockerfilePath);
    } catch (IOException ex) {
      log.error("Error Reading Dockerfile of RepoName=[{}]", repository.getRepoName());
      return Collections.emptyList();
    }
  }

  private static List<String> updateDockerfile(
      final List<String> dockerfileData, final AppDataLatestVersions latestVersions) {
    if (CommonUtilities.isEmpty(dockerfileData)) {
      return dockerfileData;
    }

    boolean isUpdated = false;
    List<String> updatedDockerfileData = new ArrayList<>();

    for (String line : dockerfileData) {
      if (line.startsWith("FROM")) {
        final String currentVersion = getCurrentVersionDocker(line);
        final String latestVersion = getLatestVersionDocker(line, latestVersions);

        if (isSupportedFrom(currentVersion)
            && isSupportedFrom(latestVersion)
            && !currentVersion.equals(latestVersion)) {
          final String updatedLine = line.replace(currentVersion, latestVersion);
          updatedDockerfileData.add(updatedLine);
          isUpdated = true;
        } else {
          updatedDockerfileData.add(line);
        }
      } else {
        updatedDockerfileData.add(line);
      }
    }

    return isUpdated ? updatedDockerfileData : Collections.emptyList();
  }

  private static String getCurrentVersionDocker(final String fromLine) {
    final String[] fromArray = fromLine.split(" ");
    if (fromArray.length > 1) {
      return fromArray[1];
    }
    return fromLine;
  }

  private static String getLatestVersionDocker(
      final String fromLine, final AppDataLatestVersions latestVersions) {
    if (fromLine.contains("gradle")) {
      return latestVersions.getLatestVersionTools().getGradle().getVersionDocker();
    }
    if (fromLine.contains(ConstantUtils.DOCKER_JRE)) {
      return latestVersions.getLatestVersionLanguages().getJava().getVersionDocker();
    }
    if (fromLine.contains("node")) {
      return latestVersions.getLatestVersionLanguages().getNode().getVersionDocker();
    }
    if (fromLine.contains("nginx")) {
      return latestVersions.getLatestVersionServers().getNginx().getVersionDocker();
    }
    if (fromLine.contains("python")) {
      return latestVersions.getLatestVersionLanguages().getPython().getVersionDocker();
    }
    return fromLine;
  }

  private static boolean isSupportedFrom(String from) {
    return from.startsWith("gradle")
        || from.contains(ConstantUtils.DOCKER_JRE)
        || from.contains("node")
        || from.contains("nginx")
        || from.contains("python");
  }

  private static boolean writeDockerfile(
      final List<String> dockerfileData,
      final Path dockerfilePath,
      final AppDataRepository repository) {
    if (CommonUtilities.isEmpty(dockerfileData)) {
      return false;
    }

    try {
      Files.write(dockerfilePath, dockerfileData, StandardCharsets.UTF_8);
      return true;
    } catch (IOException ex) {
      log.error("Error Writing Updated Dockerfile of RepoName=[{}]", repository.getRepoName());
      return false;
    }
  }
}
