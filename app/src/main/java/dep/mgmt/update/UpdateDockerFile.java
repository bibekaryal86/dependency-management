package dep.mgmt.update;

import dep.mgmt.model.AppDataLatestVersions;
import dep.mgmt.model.AppDataRepository;
import dep.mgmt.util.ConstantUtils;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UpdateDockerFile {
  private static final Logger log = LoggerFactory.getLogger(UpdateDockerFile.class);
  private final AppDataRepository repository;
  private final AppDataLatestVersions latestVersions;
  private final Path dockerfilePath;

  public UpdateDockerFile(final AppDataRepository repository, final AppDataLatestVersions latestVersions) {
    this.repository = repository;
    this.latestVersions = latestVersions;
    dockerfilePath = this.repository.getRepoPath().resolve("Dockerfile");
  }

  public boolean executeDockerfileUpdate() {
    if (Files.exists(this.dockerfilePath)) {
      List<String> dockerfileData = readDockerfile();
      dockerfileData = updateDockerfile(dockerfileData);
      return writeDockerfile(dockerfileData);
    }
    return false;
  }

  private List<String> readDockerfile() {
    try {
      return Files.readAllLines(this.dockerfilePath);
    } catch (IOException ex) {
      log.error("Error Reading Dockerfile of Repository [{}]", this.repository.getRepoName());
      return Collections.emptyList();
    }
  }

  private List<String> updateDockerfile(final List<String> dockerfileData) {
    if (CommonUtilities.isEmpty(dockerfileData)) {
      return dockerfileData;
    }

    boolean isUpdated = false;
    List<String> updatedDockerfileData = new ArrayList<>();

    for (String line : dockerfileData) {
      if (line.startsWith("FROM")) {
        final String currentVersion = getCurrentVersionDocker(line);
        final String latestVersion = getLatestVersionDocker(line);

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

  private String getCurrentVersionDocker(final String fromLine) {
    final String[] fromArray = fromLine.split(" ");
    if (fromArray.length > 1) {
      return fromArray[1];
    }
    return fromLine;
  }

  private String getLatestVersionDocker(final String fromLine) {
    if (fromLine.contains("gradle")) {
      return this.latestVersions.getLatestVersionTools().getGradle().getVersionDocker();
    }
    if (fromLine.contains(ConstantUtils.DOCKER_JRE)) {
      return this.latestVersions.getLatestVersionLanguages().getJava().getVersionDocker();
    }
    if (fromLine.contains("node")) {
      return this.latestVersions.getLatestVersionLanguages().getNode().getVersionDocker();
    }
    if (fromLine.contains("nginx")) {
      return this.latestVersions.getLatestVersionServers().getNginx().getVersionDocker();
    }
    if (fromLine.contains("python")) {
      return this.latestVersions.getLatestVersionLanguages().getPython().getVersionDocker();
    }
    return fromLine;
  }

  private boolean isSupportedFrom(String from) {
    return from.startsWith("gradle")
        || from.contains(ConstantUtils.DOCKER_JRE)
        || from.contains("node")
        || from.contains("nginx")
        || from.contains("python");
  }

  private boolean writeDockerfile(final List<String> dockerfileData) {
    if (CommonUtilities.isEmpty(dockerfileData)) {
      return false;
    }

    try {
      Files.write(this.dockerfilePath, dockerfileData, StandardCharsets.UTF_8);
      return true;
    } catch (IOException ex) {
      log.error("Error Writing Updated Dockerfile of repository: [{}]", this.repository.getRepoName());
      return false;
    }
  }
}
