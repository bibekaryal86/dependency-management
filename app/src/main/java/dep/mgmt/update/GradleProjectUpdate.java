package dep.mgmt.update;

import dep.mgmt.model.AppDataLatestVersions;
import dep.mgmt.model.AppDataRepository;
import dep.mgmt.model.BuildGradleConfigs;
import dep.mgmt.model.entity.DependencyEntity;
import dep.mgmt.service.GradleDependencyVersionService;
import dep.mgmt.service.GradlePluginVersionService;
import dep.mgmt.util.ConstantUtils;
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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GradleProjectUpdate {

  private static final Logger log = LoggerFactory.getLogger(GradleProjectUpdate.class);

  private final AppDataLatestVersions latestVersions;
  private final AppDataRepository repository;
  private final GradleDependencyVersionService gradleDependencyVersionService;
  private final GradlePluginVersionService gradlePluginVersionService;
  private final Map<String, DependencyEntity> pluginsMap;
  private final Map<String, DependencyEntity> dependenciesMap;

  public GradleProjectUpdate(
      final AppDataLatestVersions latestVersions, final AppDataRepository repository) {
    this.latestVersions = latestVersions;
    this.repository = repository;

    this.gradleDependencyVersionService = new GradleDependencyVersionService();
    this.gradlePluginVersionService = new GradlePluginVersionService();

    this.dependenciesMap = this.gradleDependencyVersionService.getGradleDependenciesMap();
    this.pluginsMap = this.gradlePluginVersionService.getGradlePluginsMap();
  }

  public boolean execute() {
    final boolean isBuildGradleUpdated = executeBuildGradleUpdate();
    final boolean isGradleWrapperUpdated = executeGradleWrapperUpdate();
    final boolean isGcpConfigUpdated =
        UpdateGcpConfigs.execute(
            this.repository, this.latestVersions.getLatestVersionLanguages().getJava());
    final boolean isDockerfileUpdated =
        UpdateDockerFile.execute(this.repository, this.latestVersions);
    final boolean isGithubWorkflowsUpdated =
        UpdateGithubWorkflows.execute(this.repository, this.latestVersions);

    return isBuildGradleUpdated
        || isGradleWrapperUpdated
        || isGcpConfigUpdated
        || isDockerfileUpdated
        || isGithubWorkflowsUpdated;
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

  /*
   * BUILD.GRADLE UPDATE
   */

  private boolean executeBuildGradleUpdate() {
    boolean isBuildGradleUpdated = false;

    try {
      List<String> gradleModules = this.repository.getGradleModules();
      for (String gradleModule : gradleModules) {
        log.debug(
            "Update Gradle Build File for Module: [ {} ] [ {} ]",
            this.repository.getRepoName(),
            gradleModule);
        BuildGradleConfigs buildGradleConfigs = readBuildGradle(gradleModule);
        if (buildGradleConfigs == null) {
          log.error("Build Gradle Configs is null: [ {} ]", this.repository.getRepoPath());
        } else {
          List<String> buildGradleContent = modifyBuildGradle(buildGradleConfigs);

          if (CommonUtilities.isEmpty(buildGradleContent)) {
            log.debug("Build Gradle Configs not updated: [ {} ]", this.repository.getRepoPath());
          } else {
            boolean isWriteToFile =
                writeBuildGradleToFile(buildGradleConfigs.getBuildGradlePath(), buildGradleContent);

            if (isWriteToFile) {
              isBuildGradleUpdated = true;
            } else {
              log.debug(
                  "Build Gradle Changes Not Written to File: [ {} ]",
                  this.repository.getRepoPath());
            }
          }
        }
      }
    } catch (Exception ex) {
      log.error("Error in Execute Build Gradle Update: ", ex);
    }

    return isBuildGradleUpdated;
  }

  private boolean writeBuildGradleToFile(
      final Path buildGradlePath, final List<String> buildGradleContent) {
    log.debug("Writing to build.gradle file: [ {} ]", buildGradlePath);
    return writeToFile(buildGradlePath, buildGradleContent);
  }

  private BuildGradleConfigs readBuildGradle(final String gradleModule) {
    Path buildGradlePath =
        Path.of(
            this.repository
                .getRepoPath()
                .toString()
                .concat(ConstantUtils.PATH_DELIMITER)
                .concat(gradleModule)
                .concat(ConstantUtils.PATH_DELIMITER)
                .concat(ConstantUtils.BUILD_GRADLE));

    try {
      List<String> allLines = Files.readAllLines(buildGradlePath);
      BuildGradleConfigs.GradleConfigBlock plugins = getPluginsBlock(allLines);
      BuildGradleConfigs.GradleConfigBlock dependencies = getDependenciesBlock(allLines, -1);

      // there might be dependencies block inside buildscript block
      BuildGradleConfigs.GradleConfigBlock dependenciesBuildScript;
      int dependenciesInBuildscriptBlock = getDependenciesBlockBuildscriptBeginPosition(allLines);
      if (dependenciesInBuildscriptBlock > 0) {
        dependenciesBuildScript = getDependenciesBlock(allLines, dependenciesInBuildscriptBlock);
      } else {
        dependenciesBuildScript =
            new BuildGradleConfigs.GradleConfigBlock(
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
      }

      return new BuildGradleConfigs(
          buildGradlePath, allLines, plugins, List.of(dependencies, dependenciesBuildScript));
    } catch (IOException e) {
      log.error(
          "Error reading build.gradle: [ {} ] [ {} ]", this.repository.getRepoName(), gradleModule);
    }

    return null;
  }

  private BuildGradleConfigs.GradleConfigBlock getPluginsBlock(final List<String> allLines) {
    List<BuildGradleConfigs.GradleConfigBlock.GradleDependencyPlugin> plugins = new ArrayList<>();
    int pluginsBeginPosition = allLines.indexOf("plugins {");

    if (pluginsBeginPosition >= 0) {
      for (int i = pluginsBeginPosition + 1; i < allLines.size(); i++) {
        final String plugin = allLines.get(i);
        // check if this is the end of the block
        if (plugin.equals("}") && isEndOfABlock(allLines, i + 1)) {
          break;
        }
        // ignore comments, new lines and plugins that don't have version
        if (leftTrim(plugin).startsWith("//") || !plugin.contains("version")) {
          continue;
        }
        // Example:    id 'io.freefair.lombok' version '6.6.3'
        String[] pluginArray = plugin.trim().split(" ");
        if (pluginArray.length != 4) {
          continue;
        }
        String group = "";
        String version = "";

        if (pluginArray[1].contains("'")) {
          group = pluginArray[1].replace("'", "");
        } else {
          group = pluginArray[1].replace("\"", "");
        }
        if (pluginArray[3].contains("'")) {
          version = pluginArray[3].replace("'", "");
        } else {
          version = pluginArray[3].replace("\"", "");
        }
        plugins.add(
            new BuildGradleConfigs.GradleConfigBlock.GradleDependencyPlugin(
                plugin, group, null, version, false));
      }
    } else {
      log.debug("No plugins in the project...");
    }

    return new BuildGradleConfigs.GradleConfigBlock(
        Collections.emptyList(), Collections.emptyList(), plugins);
  }

  private int getDependenciesBlockBuildscriptBeginPosition(final List<String> allLines) {
    int buildscriptBeginPosition = allLines.indexOf("buildscript {");
    int dependenciesBeginPosition = -1;

    if (buildscriptBeginPosition >= 0) {
      for (int i = buildscriptBeginPosition + 1; i < allLines.size(); i++) {
        String buildscript = allLines.get(i);
        // check if this is the end of the block
        if (buildscript.equals("}") && isEndOfABlock(allLines, i + 1)) {
          break;
        }
        if (buildscript.contains("dependencies {")) {
          dependenciesBeginPosition = i;
          break;
        }
      }
    }
    return dependenciesBeginPosition;
  }

  private BuildGradleConfigs.GradleConfigBlock getDependenciesBlock(
      final List<String> allLines, final int buildscriptDependenciesBeginPosition) {
    List<BuildGradleConfigs.GradleConfigBlock.GradleDefinition> gradleDefinitions =
        new ArrayList<>();
    List<BuildGradleConfigs.GradleConfigBlock.GradleDependencyPlugin> gradleDependencies =
        new ArrayList<>();

    int dependenciesBeginPosition;
    if (buildscriptDependenciesBeginPosition > 0) {
      dependenciesBeginPosition = buildscriptDependenciesBeginPosition;
    } else {
      dependenciesBeginPosition = allLines.indexOf("dependencies {");
    }

    if (dependenciesBeginPosition >= 0) {
      for (int i = dependenciesBeginPosition + 1; i < allLines.size(); i++) {
        final String original = allLines.get(i);
        // There is a chance `dependency` could be modified, so keep `original` untouched
        String dependency = allLines.get(i);
        // check if this is the end of the block
        if (dependency.equals("}") && isEndOfABlock(allLines, i + 1)) {
          break;
        }
        // Examples from mvnrepository - Gradle: #1, Gradle (Short): #2, Gradle (Kotlin): #3
        // 1: implementation group: 'ch.qos.logback', name: 'logback-classic', version: '1.4.5'
        // 2: implementation 'com.google.code.gson:gson:2.10.1'
        // 3: implementation ('com.google.code.gson:gson:2.10.1')
        // 4: testImplementation('org.springframework.boot:spring-boot-starter-test:2.3.0.RELEASE')
        // 5: implementation('org.slf4j:slf4j-api') version set as strict or require or other
        // 6: classpath 'org.postgresql:postgresql:42.1.3' (this is in buildscript block)
        if (isDependencyDeclaration(leftTrim(dependency))) {
          if (dependency.contains("(") && dependency.contains(")")) {
            dependency = dependency.replace("(", " ").replace(")", " ");
          }

          BuildGradleConfigs.GradleConfigBlock.GradleDependencyPlugin gradleDependency =
              getGradleDependency(dependency, original);
          if (gradleDependency != null) {
            gradleDependencies.add(gradleDependency);
          }
        } else if (isDefinitionDeclaration(leftTrim(dependency))) {
          BuildGradleConfigs.GradleConfigBlock.GradleDefinition gradleDefinition =
              getGradleDefinition(dependency);
          if (gradleDefinition != null) {
            gradleDefinitions.add(gradleDefinition);
          }
        }
      }
    } else {
      log.debug(
          "No [buildscriptDependenciesBeginPosition={}] dependencies in the project...",
          buildscriptDependenciesBeginPosition);
    }

    return new BuildGradleConfigs.GradleConfigBlock(
        gradleDefinitions, gradleDependencies, Collections.emptyList());
  }

  private boolean isDependencyDeclaration(final String dependency) {
    List<String> dependencyConfigurations =
        Arrays.asList(
            "api",
            "compileOnlyApi",
            "implementation",
            "testImplementation",
            "compileOnly",
            "testCompileOnly",
            "runtimeOnly",
            "testRuntimeOnly",
            "classpath");
    return dependencyConfigurations.stream().anyMatch(dependency::startsWith);
  }

  private boolean isDefinitionDeclaration(final String dependency) {
    return dependency.startsWith("def");
  }

  private BuildGradleConfigs.GradleConfigBlock.GradleDependencyPlugin getGradleDependency(
      final String dependency, final String original) {
    // Get between `'` or `"`
    Pattern pattern;
    if (dependency.contains("'") && !dependency.contains("\"")) {
      pattern = Pattern.compile(String.format("(?<=\\%s)(.*?)(?=\\%s)", "'", "'"));
    } else if (!dependency.contains("'") && dependency.contains("\"")) {
      pattern = Pattern.compile(String.format("(?<=\\%s)(.*?)(?=\\%s)", "\"", "\""));
    } else {
      return null;
    }

    Matcher matcher = pattern.matcher(dependency);
    if (matcher.find()) {
      String group = matcher.group();
      if (group.contains(":")) {
        // From above examples this matches - #2, #3 and #4
        return getGradleDependency(group, null, original);
      } else {
        // From examples this matches - #1
        Stream<MatchResult> matcherResultStream = matcher.results();
        List<String> artifactVersion =
            matcherResultStream
                .map(
                    matchResult -> {
                      if (!matchResult.group().contains(",")) {
                        return matchResult.group().trim();
                      }
                      return null;
                    })
                .filter(Objects::nonNull)
                .toList();
        return getGradleDependency(group, artifactVersion, original);
      }
    }

    return null;
  }

  private BuildGradleConfigs.GradleConfigBlock.GradleDependencyPlugin getGradleDependency(
      final String group, final List<String> artifactVersion, final String original) {
    String[] dependencyArray;
    if (artifactVersion == null) {
      dependencyArray = group.split(":");
    } else {
      dependencyArray = (group + ":" + String.join(":", artifactVersion)).split(":");
    }

    if (dependencyArray.length == 3) {
      return new BuildGradleConfigs.GradleConfigBlock.GradleDependencyPlugin(
          original, dependencyArray[0], dependencyArray[1], dependencyArray[2], Boolean.FALSE);
    }

    return null;
  }

  private BuildGradleConfigs.GradleConfigBlock.GradleDefinition getGradleDefinition(
      final String dependency) {
    Pattern pattern = Pattern.compile(String.format("(?<=\\%s)(.*?)(?=\\%s)", "\"", "\""));
    Matcher matcher = pattern.matcher(dependency);

    if (matcher.find()) {
      String value = matcher.group();
      pattern = Pattern.compile("\\w+\\s+\\w+");
      matcher = pattern.matcher(dependency);

      if (matcher.find()) {
        String defName = matcher.group();
        String[] defNameArray = defName.split(" ");
        if (defNameArray.length == 2) {
          return new BuildGradleConfigs.GradleConfigBlock.GradleDefinition(
              dependency, defNameArray[1], value);
        }
      }
    }

    return null;
  }

  private List<String> getJavaBlock(final List<String> allLines) {
    List<String> javaLines = new ArrayList<>();
    int javaBeginPosition = allLines.indexOf("java {");

    if (javaBeginPosition >= 0) {
      for (int i = javaBeginPosition + 1; i < allLines.size(); i++) {
        String javaLine = allLines.get(i);
        // check if this is the end of the block
        if (javaLine.equals("}") && isEndOfABlock(allLines, i + 1)) {
          break;
        }
        javaLines.add(javaLine);
      }
    }
    return javaLines;
  }

  private boolean isEndOfABlock(final List<String> allLines, final int positionPlusOne) {
    // assumption: only one empty line between blocks and at the end of file

    // check 1: if this is the end of file and nothing exists after
    if (isDoesNotExist(allLines, positionPlusOne)) {
      return true;
    }
    // check 2: if this is the end of file and only next line is empty line
    if (allLines.get(positionPlusOne).trim().isEmpty()
        && isDoesNotExist(allLines, positionPlusOne + 1)) {
      return true;
    }
    // check 3: check against end of super block (Eg: buildscript { dependencies {} })
    if (allLines.get(positionPlusOne).trim().equals("}")) {
      return true;
    }
    // check 4: check against beginning of another block (Eg: repositories {)
    Pattern pattern = Pattern.compile("([a-z]+\\s\\{)");
    Matcher matcher;
    if (allLines.get(positionPlusOne).trim().isEmpty()) {
      matcher = pattern.matcher(allLines.get(positionPlusOne + 1));
    } else {
      // though assumed, still check if there is no empty lines between blocks
      matcher = pattern.matcher(allLines.get(positionPlusOne));
    }
    return matcher.find();
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  private boolean isDoesNotExist(final List<String> allLines, final int positionPlusOne) {
    try {
      allLines.get(positionPlusOne);
      return false;
    } catch (IndexOutOfBoundsException ignored) {
      return true;
    }
  }

  private List<String> modifyBuildGradle(final BuildGradleConfigs buildGradleConfigs) {
    final List<String> originals = new ArrayList<>(buildGradleConfigs.getOriginals());

    final BuildGradleConfigs.GradleConfigBlock pluginsBlock = buildGradleConfigs.getPlugins();
    modifyPluginsBlock(pluginsBlock, originals);

    final List<BuildGradleConfigs.GradleConfigBlock> dependenciesBlock =
        buildGradleConfigs.getDependencies();
    for (BuildGradleConfigs.GradleConfigBlock dependencyBlock : dependenciesBlock) {
      modifyDependenciesBlock(dependencyBlock, originals);
    }

    modifyJavaBlock(originals);

    if (originals.equals(buildGradleConfigs.getOriginals())) {
      return Collections.emptyList();
    } else {
      return originals;
    }
  }

  private void modifyPluginsBlock(
      final BuildGradleConfigs.GradleConfigBlock pluginsBlock, final List<String> originals) {
    List<String> updatedPlugins = new ArrayList<>();
    if (pluginsBlock != null && !pluginsBlock.getPlugins().isEmpty()) {
      for (final BuildGradleConfigs.GradleConfigBlock.GradleDependencyPlugin gradlePlugin :
          pluginsBlock.getPlugins()) {
        if (gradlePlugin.getVersion().startsWith("$")) {
          // this updates definition
          String definitionName = gradlePlugin.getVersion().replace("$", "");

          if (updatedPlugins.contains(definitionName)) {
            continue;
          }

          Optional<BuildGradleConfigs.GradleConfigBlock.GradleDefinition> gradleDefinitionOptional =
              pluginsBlock.getDefinitions().stream()
                  .filter(gradleDefinition -> gradleDefinition.getName().equals(definitionName))
                  .findFirst();
          if (gradleDefinitionOptional.isPresent()) {
            BuildGradleConfigs.GradleConfigBlock.GradleDefinition gradleDefinition =
                gradleDefinitionOptional.get();
            String version = gradleDefinition.getValue();
            BuildGradleConfigs.GradleConfigBlock.GradleDependencyPlugin modifiedGradlePlugin =
                new BuildGradleConfigs.GradleConfigBlock.GradleDependencyPlugin(
                    null, gradlePlugin.getGroup(), null, version, Boolean.FALSE);

            String updatedOriginal = modifyPlugin(modifiedGradlePlugin, gradleDefinition);

            if (updatedOriginal != null) {
              int index = originals.indexOf(gradleDefinition.getOriginal());
              if (index >= 0) {
                originals.set(index, updatedOriginal);
                updatedPlugins.add(definitionName);
              }
            }
          }
        } else {
          String updatedOriginal = modifyPlugin(gradlePlugin, null);
          if (updatedOriginal != null) {
            int index = originals.indexOf(gradlePlugin.getOriginal());
            if (index >= 0) {
              originals.set(index, updatedOriginal);
            }
          }
        }
      }
    }
  }

  private void modifyDependenciesBlock(
      final BuildGradleConfigs.GradleConfigBlock dependenciesBlock, final List<String> originals) {
    List<String> updatedDefinitions = new ArrayList<>();
    if (dependenciesBlock != null && !dependenciesBlock.getDependencies().isEmpty()) {
      for (final BuildGradleConfigs.GradleConfigBlock.GradleDependencyPlugin gradleDependency :
          dependenciesBlock.getDependencies()) {
        if (gradleDependency.getVersion().startsWith("$")) {
          // this updates definition
          String definitionName = gradleDependency.getVersion().replace("$", "");

          if (updatedDefinitions.contains(definitionName)) {
            continue;
          }

          Optional<BuildGradleConfigs.GradleConfigBlock.GradleDefinition> gradleDefinitionOptional =
              dependenciesBlock.getDefinitions().stream()
                  .filter(gradleDefinition -> gradleDefinition.getName().equals(definitionName))
                  .findFirst();
          if (gradleDefinitionOptional.isPresent()) {
            BuildGradleConfigs.GradleConfigBlock.GradleDefinition gradleDefinition =
                gradleDefinitionOptional.get();
            String version = gradleDefinition.getValue();
            BuildGradleConfigs.GradleConfigBlock.GradleDependencyPlugin modifiedGradleDependency =
                new BuildGradleConfigs.GradleConfigBlock.GradleDependencyPlugin(
                    null,
                    gradleDependency.getGroup(),
                    gradleDependency.getArtifact(),
                    version,
                    Boolean.FALSE);

            String updatedOriginal = modifyDependency(modifiedGradleDependency, gradleDefinition);

            if (updatedOriginal != null) {
              int index = originals.indexOf(gradleDefinition.getOriginal());
              if (index >= 0) {
                originals.set(index, updatedOriginal);
                updatedDefinitions.add(definitionName);
              }
            }
          }
        } else {
          String updatedOriginal = modifyDependency(gradleDependency, null);
          if (updatedOriginal != null) {
            int index = originals.indexOf(gradleDependency.getOriginal());
            if (index >= 0) {
              originals.set(index, updatedOriginal);
            }
          }
        }
      }
    }
  }

  private String modifyPlugin(
      final BuildGradleConfigs.GradleConfigBlock.GradleDependencyPlugin gradlePlugin,
      final BuildGradleConfigs.GradleConfigBlock.GradleDefinition gradleDefinition) {
    String group = gradlePlugin.getGroup();
    DependencyEntity plugin = this.pluginsMap.get(group);

    if (plugin == null) {
      // It is likely plugin information is not available in the local repository
      log.info("Plugin information missing in local repo: [ {} ]", group);
      // Save to mongo repository
      this.gradlePluginVersionService.insertGradlePlugin(group, gradlePlugin.getVersion());
    }

    String latestVersion = "";
    if (plugin != null && !plugin.getSkipVersion()) {
      latestVersion = plugin.getVersion();
    }
    if (VersionUtils.isRequiresUpdate(gradlePlugin.getVersion(), latestVersion)) {
      return gradleDefinition == null
          ? gradlePlugin.getOriginal().replace(gradlePlugin.getVersion(), latestVersion)
          : gradleDefinition.getOriginal().replace(gradleDefinition.getValue(), latestVersion);
    }

    return null;
  }

  private String modifyDependency(
      final BuildGradleConfigs.GradleConfigBlock.GradleDependencyPlugin gradleDependency,
      final BuildGradleConfigs.GradleConfigBlock.GradleDefinition gradleDefinition) {
    String mavenId = gradleDependency.getGroup() + ":" + gradleDependency.getArtifact();
    DependencyEntity dependency = this.dependenciesMap.get(mavenId);

    if (dependency == null) {
      // It is likely dependency information is not available in the local repository
      log.info("Dependency information missing in local repo: [ {} ]", mavenId);
      // Save to mongo repository
      this.gradleDependencyVersionService.insertGradleDependency(
          mavenId, gradleDependency.getVersion());
    }

    String latestVersion = "";
    if (dependency != null && !dependency.getSkipVersion()) {
      latestVersion = dependency.getVersion();
    }
    if (VersionUtils.isRequiresUpdate(gradleDependency.getVersion(), latestVersion)) {
      return gradleDefinition == null
          ? gradleDependency.getOriginal().replace(gradleDependency.getVersion(), latestVersion)
          : gradleDefinition.getOriginal().replace(gradleDefinition.getValue(), latestVersion);
    }

    return null;
  }

  private void modifyJavaBlock(final List<String> originals) {
    final String latestJavaVersionMajor =
        this.latestVersions.getLatestVersionLanguages().getJava().getVersionMajor();
    final String currentJavaVersionMajor = extractOldVersion(originals, latestJavaVersionMajor);

    if (currentJavaVersionMajor.equals(latestJavaVersionMajor)) {
      return;
    } else if (CommonUtilities.parseIntNoEx(currentJavaVersionMajor)
        >= CommonUtilities.parseIntNoEx(latestJavaVersionMajor)) {
      return;
    }

    String oldVersionString = "JavaVersion.VERSION_" + currentJavaVersionMajor;
    String newVersionString = "JavaVersion.VERSION_" + latestJavaVersionMajor;
    String oldOfString = "JavaLanguageVersion.of(" + currentJavaVersionMajor + ")";
    String newOfString = "JavaLanguageVersion.of(" + latestJavaVersionMajor + ")";

    for (int i = 0; i < originals.size(); i++) {
      String line = originals.get(i);
      if (line.contains(oldVersionString)) {
        line = line.replace(oldVersionString, newVersionString);
        originals.set(i, line);
      } else if (line.contains(oldOfString)) {
        line = line.replace(oldOfString, newOfString);
        originals.set(i, line);
      }
    }
  }

  private String extractOldVersion(
      final List<String> javaLines, final String latestJavaVersionMajor) {
    for (String line : javaLines) {
      // Match "VERSION_X"
      Matcher versionMatcher = Pattern.compile("JavaVersion.VERSION_(\\d+)").matcher(line);
      if (versionMatcher.find()) {
        return versionMatcher.group(1);
      }

      // Match "of(X)"
      Matcher ofMatcher = Pattern.compile("JavaLanguageVersion.of\\((\\d+)\\)").matcher(line);
      if (ofMatcher.find()) {
        return ofMatcher.group(1);
      }
    }

    return latestJavaVersionMajor;
  }

  /*
   * GRADLE WRAPPER UPDATE
   */

  private boolean executeGradleWrapperUpdate() {
    // this check is done when repository object is created
    // adding here as backup
    boolean isGradleWrapperUpdated = false;
    if (!VersionUtils.isRequiresUpdate(
        this.repository.getCurrentGradleVersion(),
        this.latestVersions.getLatestVersionTools().getGradle().getVersionFull())) {
      return false;
    }

    Path wrapperPath =
        Path.of(
            repository.getRepoPath().toString().concat(ConstantUtils.GRADLE_WRAPPER_PROPERTIES));
    List<String> gradleWrapperContent = updateGradleWrapperProperties(wrapperPath);

    boolean isWriteToFile = writeGradleWrapperPropertiesToFile(wrapperPath, gradleWrapperContent);
    if (isWriteToFile) {
      isGradleWrapperUpdated = true;
    } else {
      log.debug(
          "Gradle Wrapper Properties Changes Not Written to File: [ {} ]",
          this.repository.getRepoPath());
    }

    return isGradleWrapperUpdated;
  }

  private List<String> updateGradleWrapperProperties(final Path wrapperPath) {
    try {
      List<String> updatedWrapperProperties = new ArrayList<>();
      List<String> wrapperProperties = Files.readAllLines(wrapperPath);

      for (String wrapperProperty : wrapperProperties) {
        if (wrapperProperty.startsWith("distributionUrl")) {
          String updatedDistributionUrl =
              updateDistributionUrl(
                  wrapperProperty,
                  this.repository.getCurrentGradleVersion(),
                  this.latestVersions.getLatestVersionTools().getGradle().getVersionFull());
          updatedWrapperProperties.add(updatedDistributionUrl);
        } else {
          updatedWrapperProperties.add(wrapperProperty);
        }
      }

      return updatedWrapperProperties;
    } catch (IOException e) {
      log.error("Error reading gradle-wrapper.properties: [ {} ]", repository);
    }
    return Collections.emptyList();
  }

  private String updateDistributionUrl(
      final String distributionUrl,
      final String currentGradleVersion,
      final String latestGradleVersion) {
    return distributionUrl.replace(currentGradleVersion, latestGradleVersion);
  }

  private boolean writeGradleWrapperPropertiesToFile(
      final Path gradleWrapperPropertiesPath, final List<String> gradleWrapperPropertiesContent) {
    log.debug("Writing to gradle-wrapper.properties file: [ {} ]", gradleWrapperPropertiesPath);
    return writeToFile(gradleWrapperPropertiesPath, gradleWrapperPropertiesContent);
  }

  private String leftTrim(final String line) {
    return line.replaceAll("^\\s+", "");
  }
}
