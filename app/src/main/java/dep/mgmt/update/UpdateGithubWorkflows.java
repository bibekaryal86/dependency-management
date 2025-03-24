package dep.mgmt.update;

import dep.mgmt.model.AppDataLatestVersions;
import dep.mgmt.model.AppDataRepository;
import dep.mgmt.util.VersionUtils;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateGithubWorkflows {
  private static final Logger log = LoggerFactory.getLogger(UpdateGithubWorkflows.class);
  private final AppDataRepository repository;
  private final AppDataLatestVersions latestVersions;
  private final Path githubWorkflowsFolderPath;

  public UpdateGithubWorkflows(
      final AppDataRepository repository, final AppDataLatestVersions latestVersions) {
    this.repository = repository;
    this.latestVersions = latestVersions;
    githubWorkflowsFolderPath = this.repository.getRepoPath().resolve(".github");
  }

  public boolean execute() {
    boolean isUpdated = false;
    if (Files.exists(this.githubWorkflowsFolderPath)) {
      List<Path> githubWorkflowPaths = findGithubWorkflows();
      for (Path githubWorkflowPath : githubWorkflowPaths) {
        List<String> githubWorkflowContent = readGithubWorkflowFile(githubWorkflowPath);
        githubWorkflowContent = updateGithubWorkflowFile(githubWorkflowContent);
        boolean isWrittenToFile =
            writeGithubWorkflowFile(githubWorkflowPath, githubWorkflowContent);

        if (isWrittenToFile && !isUpdated) {
          isUpdated = true;
        }
      }
    }
    return isUpdated;
  }

  private List<Path> findGithubWorkflows() {
    try (Stream<Path> stream = Files.walk(this.githubWorkflowsFolderPath, 2)) {
      return stream
          .filter(Files::isRegularFile)
          .filter(path -> path.toString().endsWith(".yml"))
          .collect(Collectors.toList());
    } catch (IOException ex) {
      log.error("Find Github Actions Files: [{}]", this.githubWorkflowsFolderPath, ex);
      return Collections.emptyList();
    }
  }

  private List<String> readGithubWorkflowFile(final Path githubWorkflowPath) {
    try {
      return Files.readAllLines(githubWorkflowPath);
    } catch (IOException ex) {
      log.error(
          "Error Reading Github Workflow [{}] of Repository [{}]",
          githubWorkflowPath,
          this.repository.getRepoName());
      return Collections.emptyList();
    }
  }

  private List<String> updateGithubWorkflowFile(final List<String> githubWorkflowContent) {
    if (CommonUtilities.isEmpty(githubWorkflowContent)) {
      return githubWorkflowContent;
    }

    boolean isUpdated = false;
    List<String> updatedGithubWorkflowContent = new ArrayList<>();

    for (int i = 0; i < githubWorkflowContent.size(); i++) {
      String line = githubWorkflowContent.get(i);
      if (line.trim().startsWith("#")) {
        updatedGithubWorkflowContent.add(line);
        continue;
      }

      String updatedLine = updateGithubActions(line);
      if (line.equals(updatedLine)) {
        updatedLine = updateLanguageVersion(updatedLine);
      }
      if (line.equals(updatedLine) && i > 0) {
        final String lineMinusOne = githubWorkflowContent.get(i - 1);
        List<String> updatedLines = updateToolVersionFlyway(updatedLine, lineMinusOne);
        updatedLine = updatedLines.getFirst();

        final String updatedLineMinusOne = updatedLines.get(1);
        if (!lineMinusOne.equals(updatedLineMinusOne)) {
          updatedGithubWorkflowContent.set(i - 1, updatedLineMinusOne);
        }
      }

      updatedGithubWorkflowContent.add(updatedLine);

      if (!line.equals(updatedLine)) {
        isUpdated = true;
      }
    }

    return isUpdated ? updatedGithubWorkflowContent : Collections.emptyList();
  }

  private String updateGithubActions(final String githubActionLine) {
    String currentVersion = "";
    String latestVersion = "";

    if (githubActionLine.contains("actions/checkout")) {
      latestVersion =
          this.latestVersions.getLatestVersionGithubActions().getCheckout().getVersionMajor();
    } else if (githubActionLine.contains("actions/setup-java")) {
      latestVersion =
          this.latestVersions.getLatestVersionGithubActions().getSetupJava().getVersionMajor();
    } else if (githubActionLine.contains("gradle/actions/setup-gradle")) {
      latestVersion =
          this.latestVersions.getLatestVersionGithubActions().getSetupGradle().getVersionMajor();
    } else if (githubActionLine.contains("actions/setup-node")) {
      latestVersion =
          this.latestVersions.getLatestVersionGithubActions().getSetupNode().getVersionMajor();
    } else if (githubActionLine.contains("actions/setup-python")) {
      latestVersion =
          this.latestVersions.getLatestVersionGithubActions().getSetupPython().getVersionMajor();
    } else if (githubActionLine.contains("github/codeql-action")) {
      latestVersion =
          this.latestVersions.getLatestVersionGithubActions().getCodeql().getVersionMajor();
    }

    if (!CommonUtilities.isEmpty(latestVersion)) {
      String[] lineArray = githubActionLine.split("@v");
      if (lineArray.length == 2) {
        currentVersion = lineArray[1];
      } else {
        // set it as latest version so that the line doesn't get updated
        currentVersion = latestVersion;
      }
    }

    if (currentVersion.equals(latestVersion)) {
      return githubActionLine;
    }

    return githubActionLine.replace(currentVersion, latestVersion);
  }

  private String updateLanguageVersion(final String versionLine) {
    String currentVersion = "";
    String latestVersion = getLatestLanguageVersion(versionLine);

    if (!CommonUtilities.isEmpty(latestVersion)) {
      currentVersion = getCurrentLanguageVersion(versionLine);

      if (CommonUtilities.isEmpty(currentVersion)) {
        // set it as latest version so that the line doesn't get updated
        currentVersion = latestVersion;
      }
    }

    if (currentVersion.equals(latestVersion)) {
      return versionLine;
    }

    return versionLine.replace(currentVersion, latestVersion);
  }

  private List<String> updateToolVersionFlyway(
      final String versionLine, final String versionLineMinusOne) {
    String currentVersion = "";
    String latestVersion = getLatestToolVersionFlyway(versionLine);

    if (!CommonUtilities.isEmpty(latestVersion)) {
      currentVersion = getCurrentToolVersionFlyway(versionLine);

      if (CommonUtilities.isEmpty(currentVersion)) {
        // set it as latest version so that the line doesn't get updated
        currentVersion = latestVersion;
      }
    }

    if (currentVersion.equals(latestVersion)) {
      return List.of(versionLine, versionLineMinusOne);
    }

    final String updatedVersionLine = versionLine.replace(currentVersion, latestVersion);
    final String updatedVersionLineMinusOne =
        versionLineMinusOne.replace(currentVersion, latestVersion);
    return List.of(updatedVersionLine, updatedVersionLineMinusOne);
  }

  private String getLatestLanguageVersion(final String versionLine) {
    String latestVersion = "";

    if (versionLine.contains("node-version") && !versionLine.contains("matrix.node-version")) {
      latestVersion = this.latestVersions.getLatestVersionLanguages().getNode().getVersionMajor();
    } else if (versionLine.contains("python-version")
        && !versionLine.contains("matrix.python-version")) {
      latestVersion =
          VersionUtils.getVersionMajorMinor(
              this.latestVersions.getLatestVersionLanguages().getPython().getVersionFull(), true);
    } else if (versionLine.contains("java-version")
        && !versionLine.contains("matrix.java-version")) {
      latestVersion = this.latestVersions.getLatestVersionLanguages().getJava().getVersionMajor();
    }

    return latestVersion;
  }

  private String getLatestToolVersionFlyway(final String versionLine) {
    String latestVersion = "";
    if (versionLine.contains("flyway/flyway")) {
      latestVersion = this.latestVersions.getLatestVersionTools().getFlyway().getVersionFull();
    }
    return latestVersion;
  }

  private String getCurrentLanguageVersion(final String versionLine) {
    String currentVersion = "";

    if (versionLine.contains("[") && versionLine.contains("]")) {
      // this is a version matrix with multiple values, find out the lowest number among them
      return getLowestCurrentVersionFromMatrix(versionLine);
    } else {
      String[] versionLineArray = versionLine.split(":");

      if (versionLineArray.length == 2) {
        return versionLineArray[1].trim();
      }
    }

    return currentVersion;
  }

  private String getCurrentToolVersionFlyway(final String versionLine) {
    return versionLine.replaceAll("[^0-9.]", "");
  }

  private String getLowestCurrentVersionFromMatrix(final String versionLine) {
    Pattern pattern = Pattern.compile("\\[(.*?)]");
    Matcher matcher = pattern.matcher(versionLine);

    if (matcher.find()) {
      final String versions = matcher.group(1);
      if (!CommonUtilities.isEmpty(versions)) {
        String lowestVersion =
            Arrays.stream(versions.split(","))
                .map(String::trim)
                .min(VersionUtils::compareVersions)
                .orElse(null);
        if (!CommonUtilities.isEmpty(lowestVersion)) {
          return lowestVersion;
        }
      }
    }

    return "";
  }

  private boolean writeGithubWorkflowFile(
      final Path githubWorkflowPath, final List<String> githubWorkflowContent) {
    if (CommonUtilities.isEmpty(githubWorkflowContent)) {
      return false;
    }

    try {
      Files.write(githubWorkflowPath, githubWorkflowContent, StandardCharsets.UTF_8);
      return true;
    } catch (IOException ex) {
      log.error(
          "Error Writing Updated Github Workflow [{}] of repository: [{}]",
          githubWorkflowPath,
          this.repository.getRepoName());
      return false;
    }
  }
}
