package dep.mgmt.update;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dep.mgmt.model.AppDataLatestVersions;
import dep.mgmt.model.AppDataRepository;
import dep.mgmt.model.entity.DependencyEntity;
import dep.mgmt.service.NodeDependencyVersionService;
import dep.mgmt.util.ConstantUtils;
import dep.mgmt.util.CustomPrettyPrinter;
import dep.mgmt.util.VersionUtils;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeProjectUpdate {
  private static final Logger log = LoggerFactory.getLogger(NodeProjectUpdate.class);

  private final AppDataLatestVersions latestVersions;
  private final AppDataRepository repository;
  private final NodeDependencyVersionService nodeDependencyVersionService;
  private final Map<String, DependencyEntity> dependenciesMap;

  public NodeProjectUpdate(
      final AppDataLatestVersions latestVersions, final AppDataRepository repository) {
    this.latestVersions = latestVersions;
    this.repository = repository;
    this.nodeDependencyVersionService = new NodeDependencyVersionService();
    this.dependenciesMap = this.nodeDependencyVersionService.getNodeDependenciesMap();
  }

  public boolean execute() {
    log.debug("Updating [{}]", this.repository.getRepoName());
    final boolean isPackageJsonUpdated = executePackageJsonUpdate();
    final boolean isGcpConfigUpdated =
        UpdateGcpConfigs.execute(
            this.repository, this.latestVersions.getLatestVersionLanguages().getNode());
    final boolean isDockerfileUpdated =
        UpdateDockerFile.execute(this.repository, this.latestVersions);
    final boolean isGithubWorkflowsUpdated =
        UpdateGithubWorkflows.execute(this.repository, this.latestVersions);
    log.info(
        "Update Finished: [{}]--[{}-{}-{}-{}]",
        this.repository.getRepoName(),
        isPackageJsonUpdated,
        isGcpConfigUpdated,
        isDockerfileUpdated,
        isGithubWorkflowsUpdated);

    return isPackageJsonUpdated
        || isGcpConfigUpdated
        || isDockerfileUpdated
        || isGithubWorkflowsUpdated;
  }

  public boolean executePackageJsonUpdate() {
    final Path packageJsonPath =
        Path.of(
            this.repository
                .getRepoPath()
                .toString()
                .concat(ConstantUtils.PATH_DELIMITER)
                .concat(ConstantUtils.PACKAGE_JSON));
    final File packageJsonFile = new File(packageJsonPath.toFile().getPath());
    final JsonNode rootNode = readPackageJsonContents(packageJsonFile);

    if (rootNode == null) {
      log.error("Root Node is NULL: [{}]", packageJsonPath);
      return false;
    }

    ObjectNode rootObject = (ObjectNode) rootNode;

    final boolean isDependenciesUpdated =
        updateDependencies(rootObject, ConstantUtils.DEPENDENCIES);
    final boolean isDevDependenciesUpdated =
        updateDependencies(rootObject, ConstantUtils.DEPENDENCIES_DEV);
    final boolean isOptionalDependenciesUpdated =
        updateDependencies(rootObject, ConstantUtils.DEPENDENCIES_OPTIONAL);
    final boolean isEnginesUpdated = updateEngines(rootObject);

    if (isDependenciesUpdated
        || isDevDependenciesUpdated
        || isOptionalDependenciesUpdated
        || isEnginesUpdated) {
      return writePackageJsonContents(packageJsonFile, rootNode);
    }

    return false;
  }

  private JsonNode readPackageJsonContents(final File file) {
    try {
      return CommonUtilities.objectMapperProvider().readTree(file);
    } catch (Exception ex) {
      log.error("Get Package Json Contents: [{}]", file.getPath(), ex);
      return null;
    }
  }

  private boolean writePackageJsonContents(final File file, final JsonNode jsonNode) {
    try {
      final String jsonContent =
          CommonUtilities.objectMapperProvider()
              .writer(new CustomPrettyPrinter())
              .writeValueAsString(jsonNode)
              .concat(System.lineSeparator());
      Files.write(Paths.get(file.toURI()), jsonContent.getBytes());
      return true;
    } catch (Exception ex) {
      log.error("Write Package Json Contents: [{}]", file.getPath(), ex);
      return false;
    }
  }

  private boolean updateDependencies(final ObjectNode rootObject, final String section) {
    final JsonNode sectionNode = rootObject.get(section);
    boolean isUpdated = false;

    if (sectionNode != null) {
      Iterator<String> fieldNames = sectionNode.fieldNames();
      while (fieldNames.hasNext()) {
        final String name = fieldNames.next();
        final String version = sectionNode.get(name).textValue();
        final String currentVersion = getCurrentVersion(version);

        final DependencyEntity dependency = this.dependenciesMap.get(name);
        if (dependency == null) {
          this.nodeDependencyVersionService.insertNodeDependency(name, version);
        }

        String latestVersion = "";
        if (dependency != null && !dependency.getSkipVersion()) {
          latestVersion = dependency.getVersion();
        }

        if (VersionUtils.isRequiresUpdate(currentVersion, latestVersion)) {
          final String latestVersionToUse = version.replace(currentVersion, latestVersion);
          ((ObjectNode) sectionNode).put(name, latestVersionToUse);
          isUpdated = true;
        }
      }
    }

    return isUpdated;
  }

  private boolean updateEngines(ObjectNode rootObject) {
    final JsonNode enginesNode = rootObject.get(ConstantUtils.ENGINES);

    if (enginesNode != null && enginesNode.has(ConstantUtils.NODE_NAME)) {
      final String currentVersion = enginesNode.get(ConstantUtils.NODE_NAME).asText();
      final String latestVersion =
          latestVersions.getLatestVersionLanguages().getNode().getVersionMajor();

      if (VersionUtils.isRequiresUpdate(currentVersion, latestVersion)) {
        ((ObjectNode) enginesNode).put(ConstantUtils.NODE_NAME, latestVersion);
        return true;
      }
    }

    return false;
  }

  private String getCurrentVersion(final String version) {
    if (!CommonUtilities.isEmpty(version) && (version.startsWith("^") || version.startsWith("~"))) {
      return version.substring(1);
    }
    return version;
  }
}
