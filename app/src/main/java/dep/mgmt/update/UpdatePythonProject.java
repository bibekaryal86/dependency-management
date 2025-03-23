package dep.mgmt.update;

import dep.mgmt.model.AppDataLatestVersions;
import dep.mgmt.model.AppDataRepository;
import dep.mgmt.model.entity.DependencyEntity;
import dep.mgmt.service.PythonPackageVersionService;
import dep.mgmt.util.ConstantUtils;
import dep.mgmt.util.VersionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdatePythonProject {
  private static final Logger log = LoggerFactory.getLogger(UpdatePythonProject.class);

  private final AppDataLatestVersions latestVersions;
  private final AppDataRepository repository;
  private final PythonPackageVersionService pythonPackageVersionService;
  private final UpdateDockerFile updateDockerFile;
  private final UpdateGcpConfigs updateGcpConfigs;
  private final UpdateGithubWorkflows updateGithubWorkflows;
  private final Map<String, DependencyEntity> packagesMap;

  public UpdatePythonProject(
      final AppDataLatestVersions latestVersions,
      final AppDataRepository repository) {
    this.latestVersions = latestVersions;
    this.repository = repository;

    this.pythonPackageVersionService = new PythonPackageVersionService();
    this.updateDockerFile = new UpdateDockerFile(repository, latestVersions);
    this.updateGcpConfigs = new UpdateGcpConfigs(repository, latestVersions.getLatestVersionLanguages().getJava());
    this.updateGithubWorkflows = new UpdateGithubWorkflows(repository, latestVersions);

    this.packagesMap = this.pythonPackageVersionService.getPythonPackagesMap();
  }

  private boolean executePythonUpdate() {
    final boolean isProjectTomlUpdated = executePyProjectTomlUpdate();
    final boolean isRequirementsTxtUpdated = executeRequirementsTxtUpdate();
    final boolean isGcpConfigUpdated = this.updateGcpConfigs.executeGcpConfigsUpdate();
    final boolean isDockerfileUpdated = this.updateDockerFile.executeDockerfileUpdate();
    final boolean isGithubWorkflowsUpdated = this.updateGithubWorkflows.executeGithubWorkflowsUpdate();

    return isProjectTomlUpdated || isRequirementsTxtUpdated || isGcpConfigUpdated || isDockerfileUpdated || isGithubWorkflowsUpdated;
  }

  private List<String> readFromFile(final Path path) {
    try {
      return Files.readAllLines(path);
    } catch (IOException ex) {
      log.error("Error reading file: [ {} ]", path);
    }
    return Collections.emptyList();
  }

  private boolean writeToFile(final Path path, final List<String> content) {
    try {
      Files.write(path, content, StandardCharsets.UTF_8);
      return true;
    } catch (IOException ex) {
      log.error("Error Saving Updated File: [ {} ]", path, ex);
      return false;
    }
  }

  private boolean executeRequirementsTxtUpdate() {
    boolean isUpdated = false;
    for (String requirementsTxt : this.repository.getRequirementsTxts()) {
      isUpdated = updateRequirementsTxt(requirementsTxt);
    }
    return isUpdated;
  }

  private boolean updateRequirementsTxt(final String requirementsTxt) {
    boolean isUpdated = false;
    Path requirementsTxtPath =
        Path.of(
            this.repository
                .getRepoPath()
                .toString()
                .concat(ConstantUtils.PATH_DELIMITER)
                .concat(requirementsTxt));
    List<String> requirementsTxtContent = readFromFile(requirementsTxtPath);

    if (requirementsTxtContent.isEmpty()) {
      log.error("[ {} ] content is empty: in [ {} ]", requirementsTxt, this.repository.getRepoName());
    } else {
      return modifyRequirementsTxt(requirementsTxtPath, requirementsTxtContent);
    }
    return isUpdated;
  }

  private boolean modifyRequirementsTxt(final Path requirementsTxtPath, final List<String> requirementsTxtContent) {
    boolean isUpdated = false;
    List<String> updatedRequirementsTxtContent = new ArrayList<>();

    for (String s : requirementsTxtContent) {
      String updatedS = updateRequirement(s);
      if (!s.equals(updatedS)) {
        isUpdated = true;
      }
      updatedRequirementsTxtContent.add(updatedS);
    }

    if (isUpdated) {
      return writeToFile(requirementsTxtPath, updatedRequirementsTxtContent);
    }
    return false;
  }

  private String updateRequirement(final String requirement) {
    // ignore commented out lines
    if (requirement.startsWith("#")) {
      return requirement;
    }
    String updatedLine = requirement;
    String[] requirementArray;
    if (requirement.contains(">=")) {
      requirementArray = requirement.split(">=");
    } else {
      requirementArray = requirement.split("==");
    }

    if (requirementArray.length == 2) {
      String name = requirementArray[0].trim();
      String version = requirementArray[1].trim();
      DependencyEntity onePackage = this.packagesMap.get(name);

      if (onePackage == null) {
        savePackage(name, version);
      }

      String latestVersion = "";
      if (onePackage != null && !onePackage.getSkipVersion()) {
        latestVersion = onePackage.getVersion();
      }

      if (VersionUtils.isRequiresUpdate(version, latestVersion)) {
        updatedLine = updatedLine.replace(version, latestVersion);
      }
    } else {
      log.error("Python Requirement Array Size Incorrect: [ {} ]", requirement);
    }
    return updatedLine;
  }

  /*
   * PYPROJECT.TOML UPDATE
   */

  private boolean executePyProjectTomlUpdate() {
    Path pyProjectTomlPath = Path.of(this.repository.getRepoPath().toString().concat(ConstantUtils.PATH_DELIMITER).concat(ConstantUtils.PYPROJECT_TOML));
    List<String> pyProjectContent = readFromFile(pyProjectTomlPath);

    if (pyProjectContent.isEmpty()) {
      log.error("PyProject Toml Content is empty: [ {} ]", this.repository.getRepoName());
    } else {
      return modifyPyProjectToml(pyProjectTomlPath, pyProjectContent);
    }

    return false;
  }

  private boolean modifyPyProjectToml(final Path pyProjectTomlPath, final List<String> pyProjectContent) {
    boolean isUpdated = false;

    List<String> updatedPyProjectContent = new ArrayList<>();
    for (String s : pyProjectContent) {
      if (isRequiresBuildTools(s)) {
        String updatedS = updateBuildTools(s);
        if (!updatedS.equals(s)) {
          isUpdated = true;
        }
        updatedPyProjectContent.add(updatedS);
      } else if (isRequiresPython(s)) {
        String updatedS = updateRequiresPython(s);
        if (!updatedS.equals(s)) {
          isUpdated = true;
        }
        updatedPyProjectContent.add(updatedS);
      } else if (isBlackTargetVersion(s)) {
        String updatedS = updateBlackTargetVersion(s);
        if (!updatedS.equals(s)) {
          isUpdated = true;
        }
        updatedPyProjectContent.add(updatedS);
      } else {
        updatedPyProjectContent.add(s);
      }
    }

    if (isUpdated) {
      return writeToFile(pyProjectTomlPath, updatedPyProjectContent);
    }

    return false;
  }

  private boolean isRequiresBuildTools(final String line) {
    return line.contains("requires") && line.contains("setuptools") && line.contains("wheel");
  }

  private boolean isRequiresPython(final String line) {
    return line.contains("requires-python");
  }

  private boolean isBlackTargetVersion(final String line) {
    return line.contains("target-version");
  }

  private String updateBuildTools(final String line) {
    List<String> buildTools = new ArrayList<>();
    Pattern pattern = Pattern.compile("'(.*?)'");
    Matcher matcher = pattern.matcher(line);

    while (matcher.find()) {
      buildTools.add(matcher.group(1));
    }

    return updateBuildTools(line, buildTools);
  }

  private String updateRequiresPython(final String line) {
    final String currentVersion = line.replaceAll("[^\\d.]", "").trim();
    final String latestVersion =
        VersionUtils.getVersionMajorMinor(
            this.latestVersions.getLatestVersionLanguages().getPython().getVersionFull(),
            true);

    if (currentVersion.equals(latestVersion)) {
      return line;
    }

    return line.replace(currentVersion, latestVersion);
  }

  private String updateBlackTargetVersion(final String line) {
    final String currentVersion = getCurrentBlackTargetVersion(line);
    final String latestVersion =
        "py"
            + VersionUtils.getVersionMajorMinor(
                this.latestVersions.getLatestVersionLanguages().getPython().getVersionFull(),
                false);

    if (currentVersion.equals(latestVersion)) {
      return line;
    }

    return line.replace(currentVersion, latestVersion);
  }

  public String getCurrentBlackTargetVersion(final String line) {
    String[] parts = line.split("=");
    String versionPart = parts[1].trim();
    versionPart = versionPart.replaceAll("[\\[\\]' ]", "");
    return versionPart;
  }

  private String updateBuildTools(final String line, final List<String> buildTools) {
    String updatedLine = line;
    for (String buildTool : buildTools) {
      String[] buildToolArray;
      if (buildTool.contains(">=")) {
        buildToolArray = buildTool.split(">=");
      } else {
        buildToolArray = buildTool.split("==");
      }

      if (buildToolArray.length == 2) {
        String name = buildToolArray[0].trim();
        String version = buildToolArray[1].trim();
        DependencyEntity onePackage = this.packagesMap.get(name);

        if (onePackage == null) {
          savePackage(name, version);
        }

        String latestVersion = "";
        if (onePackage != null && !onePackage.getSkipVersion()) {
          latestVersion = onePackage.getVersion();
        }

        if (VersionUtils.isRequiresUpdate(version, latestVersion)) {
          updatedLine = updatedLine.replace(version, latestVersion);
        }
      } else {
        log.error("Build Tool Array Size Incorrect: [ {} ]", buildTool);
      }
    }
    return updatedLine;
  }

  private void savePackage(final String name, final String version) {
    log.info("Python Packages information missing in local repo: [ {} ]", name);
    this.pythonPackageVersionService.insertPythonPackage(name, version);
  }
}
