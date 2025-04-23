package dep.mgmt.update;

import dep.mgmt.model.AppDataRepository;
import dep.mgmt.model.LatestVersion;
import dep.mgmt.model.enums.RequestParams;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateGcpConfigs {
  private static final Logger log = LoggerFactory.getLogger(UpdateGcpConfigs.class);

  public static boolean execute(
      final AppDataRepository repository, final LatestVersion latestVersion) {
    final Path yamlFilePath =
        repository.getType().equals(RequestParams.UpdateType.PYTHON)
            ? repository.getRepoPath().resolve("app.yaml")
            : repository.getRepoPath().resolve("gcp/app.yaml");

    if (Files.exists(yamlFilePath)) {
      List<String> yamlData = readGcpAppYaml(yamlFilePath, repository);
      yamlData = updateGcpAppYaml(yamlData, latestVersion);
      return writeGcpAppYaml(yamlData, yamlFilePath, repository);
    }
    return false;
  }

  private static List<String> readGcpAppYaml(
      final Path yamlFilePath, final AppDataRepository repository) {
    try {
      return Files.readAllLines(yamlFilePath);
    } catch (IOException ex) {
      log.error("Error Reading GCP App Yaml of Repository [{}]", repository.getRepoName());
      return Collections.emptyList();
    }
  }

  private static List<String> updateGcpAppYaml(
      final List<String> yamlData, final LatestVersion latestVersion) {
    if (CommonUtilities.isEmpty(yamlData)) {
      return yamlData;
    }

    final String latestGcpRuntime = latestVersion.getVersionGcp();
    // assumption: gcp app yaml's first line is always runtime
    final String currentGcpRuntime = getCurrentGcpRuntime(yamlData.getFirst());

    if (currentGcpRuntime == null || latestGcpRuntime.equals(currentGcpRuntime)) {
      return Collections.emptyList();
    }

    final String updatedRuntime = yamlData.getFirst().replace(currentGcpRuntime, latestGcpRuntime);
    yamlData.set(0, updatedRuntime);
    return yamlData;
  }

  private static String getCurrentGcpRuntime(final String runtimeLine) {
    final String[] runtimeArray = runtimeLine.split(":");

    if (runtimeArray.length != 2) {
      log.error("Malformed GCP App Yaml Runtime: [{}]", runtimeLine);
      return null;
    }

    final String runtimeValue = runtimeArray[1].trim();

    if (!isSupportedRuntime(runtimeValue)) {
      log.error("Incorrect GCP App Yaml Runtime: [{}]", runtimeLine);
      return null;
    }

    return runtimeArray[1].trim();
  }

  private static boolean isSupportedRuntime(String runtime) {
    return runtime.contains("java") || runtime.contains("node") || runtime.contains("python");
  }

  private static boolean writeGcpAppYaml(
      final List<String> yamlData, final Path yamlFilePath, final AppDataRepository repository) {
    if (CommonUtilities.isEmpty(yamlData)) {
      return false;
    }

    try {
      Files.write(yamlFilePath, yamlData, StandardCharsets.UTF_8);
      return true;
    } catch (IOException ex) {
      log.error("Error Writing Updated GCP App Yaml of repository: [{}]", repository.getRepoName());
      return false;
    }
  }
}
