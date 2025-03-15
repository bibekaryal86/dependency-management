package dep.mgmt.util;

import dep.mgmt.config.CacheConfig;
import dep.mgmt.model.AppData;
import dep.mgmt.model.AppDataLatestVersions;
import dep.mgmt.model.AppDataRepository;
import dep.mgmt.model.AppDataScriptFile;
import dep.mgmt.model.LatestVersion;
import dep.mgmt.model.enums.RequestParams;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppDataUtils {

  private static final Logger log = LoggerFactory.getLogger(AppDataUtils.class);

  public static AppData appData() {
    final AppData appDataCache = CacheConfig.getAppData();
    if (appDataCache == null) {
      return setAppData();
    }
    return appDataCache;
  }

  public static AppData setAppData() {
    log.info("Set App Data...");
    // get the input arguments
    final Map<String, String> argsMap = validateInputAndMakeArgsMap();
    // get the list of repositories and their type
    final List<AppDataRepository> repositories = getRepositoryLocations(argsMap);
    // get the scripts included in resources folder
    final List<AppDataScriptFile> scriptFiles = getScriptFilesInResources();
    // get the latest versions of tools and runtimes
    final AppDataLatestVersions latestVersions = getLatestVersions();
    // set app data cache and return
    return CacheConfig.setAppData(argsMap, scriptFiles, repositories, latestVersions);
  }

  private static Map<String, String> validateInputAndMakeArgsMap() {
    log.debug("Make Args Map...");
    final Map<String, String> properties =
        CommonUtilities.getSystemEnvProperties(ConstantUtils.ENV_KEY_NAMES);
    final List<String> errors =
        ConstantUtils.ENV_KEY_NAMES.stream()
            .filter(
                key -> !ConstantUtils.ENV_SERVER_PORT.equals(key) && properties.get(key) == null)
            .toList();
    log.info("Args Map After Conversion: [ {} ]", properties.size());
    if (errors.isEmpty()) {
      return properties;
    }
    throw new IllegalStateException(
        "One or more environment configurations could not be accessed...");
  }

  private static List<AppDataRepository> getRepositoryLocations(final Map<String, String> argsMap) {
    log.debug("Get Repository Locations...");

    List<Path> repoPaths;
    try (Stream<Path> pathStream =
        Files.walk(Paths.get(argsMap.get(ConstantUtils.ENV_REPO_HOME)), 2)) {
      repoPaths = pathStream.filter(Files::isDirectory).toList();
    } catch (Exception ex) {
      throw new RuntimeException("Repositories not found in the repo path provided!", ex);
    }

    if (repoPaths.isEmpty()) {
      throw new RuntimeException("Repositories not found in the repo path provided...");
    }

    List<AppDataRepository> npmRepositories = new ArrayList<>();
    List<AppDataRepository> gradleRepositories = new ArrayList<>();
    List<AppDataRepository> pythonRepositories = new ArrayList<>();

    for (Path path : repoPaths) {
      try (Stream<Path> pathStream = Files.list(path)) {
        npmRepositories.addAll(
            pathStream
                .filter(stream -> "package.json".equals(stream.getFileName().toString()))
                .map(mapper -> new AppDataRepository(path, RequestParams.UpdateType.NPM))
                .toList());
      } catch (Exception ex) {
        throw new RuntimeException("NPM Files not found in the repo path provided!", ex);
      }

      try (Stream<Path> pathStream = Files.list(path)) {
        gradleRepositories.addAll(
            pathStream
                .filter(stream -> "settings.gradle".equals(stream.getFileName().toString()))
                .map(
                    mapper -> {
                      List<String> gradleModules = readGradleModules(mapper);
                      return new AppDataRepository(
                          path, RequestParams.UpdateType.GRADLE, gradleModules);
                    })
                .toList());
      } catch (Exception ex) {
        throw new RuntimeException("Gradle Repositories not found in the repo path provided!", ex);
      }

      try (Stream<Path> pathStream = Files.list(path)) {
        pythonRepositories.addAll(
            pathStream
                .filter(stream -> "pyproject.toml".equals(stream.getFileName().toString()))
                .map(
                    mapper -> {
                      List<String> requirementsTxts = readRequirementsTxts(path);
                      return new AppDataRepository(
                          path, RequestParams.UpdateType.PYTHON, requirementsTxts);
                    })
                .toList());
      } catch (Exception ex) {
        throw new RuntimeException("Python Files not found in the repo path provided!", ex);
      }
    }

    // add gradle wrapper version data
    List<AppDataRepository> gradleWrapperRepositories =
        gradleRepositories.stream()
            .map(
                repository -> {
                  String currentGradleVersion = getCurrentGradleVersionInRepo(repository);
                  return new AppDataRepository(
                      repository.getRepoPath(),
                      repository.getType(),
                      repository.getGradleModules(),
                      currentGradleVersion);
                })
            .toList();

    List<AppDataRepository> repositories = new ArrayList<>();
    repositories.addAll(npmRepositories);
    repositories.addAll(gradleWrapperRepositories);
    repositories.addAll(pythonRepositories);

    log.info("Repository list: [ {} ]", repositories.size());
    log.debug("Repository list: [ {} ]", repositories);
    return repositories;
  }

  private static List<String> readGradleModules(final Path settingsGradlePath) {
    try {
      List<String> allLines = Files.readAllLines(settingsGradlePath);
      Pattern pattern = Pattern.compile(String.format("(?<=\\%s)(.*?)(?=\\%s)", "'", "'"));

      return allLines.stream()
          .filter(line -> line.contains("include"))
          .map(
              line -> {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                  return matcher.group().replace(":", "");
                }
                return null;
              })
          .filter(Objects::nonNull)
          .toList();
    } catch (IOException ex) {
      log.error("Error in Read Gradle Modules: [ {} ]", settingsGradlePath, ex);
      return Collections.singletonList(ConstantUtils.APP_MAIN_MODULE);
    }
  }

  private static List<AppDataScriptFile> getScriptFilesInResources() {
    log.debug("Get Script files in Resources...");
    List<AppDataScriptFile> scriptFiles = new ArrayList<>();

    try {
      ClassLoader classLoader = AppDataUtils.class.getClassLoader();
      URL resourcesUrl = classLoader.getResource("scripts");

      if (resourcesUrl == null) {
        throw new RuntimeException("scripts directory not found in resources...");
      }

      Path resourcesPath = Paths.get(resourcesUrl.toURI());

      if (!Files.exists(resourcesPath) || !Files.isDirectory(resourcesPath)) {
        throw new RuntimeException("scripts directory is not a valid directory...");
      }

      try (Stream<Path> files = Files.list(resourcesPath)) {
        scriptFiles =
            files
                .filter(path -> path.endsWith(".sh"))
                .map(path -> new AppDataScriptFile(path.getFileName().toString()))
                .toList();
      }

      if (scriptFiles.isEmpty()) {
        throw new RuntimeException("Script files not found in resources...");
      }

      log.info("Script files: [ {} ]", scriptFiles.size());
      log.debug("Script files: [ {} ]", scriptFiles);
      return scriptFiles;
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private static String getCurrentGradleVersionInRepo(final AppDataRepository repository) {
    Path wrapperPath =
        Path.of(
            repository.getRepoPath().toString().concat(ConstantUtils.GRADLE_WRAPPER_PROPERTIES));
    try {
      List<String> allLines = Files.readAllLines(wrapperPath);
      String distributionUrl =
          allLines.stream()
              .filter(line -> line.startsWith("distributionUrl"))
              .findFirst()
              .orElse(null);

      if (distributionUrl != null) {
        return parseDistributionUrlForGradleVersion(distributionUrl);
      }
    } catch (IOException e) {
      log.error("Error reading gradle-wrapper.properties: [ {} ]", repository);
    }
    return null;
  }

  private static String parseDistributionUrlForGradleVersion(final String distributionUrl) {
    // matches text between two hyphens
    // eg: distributionUrl=https\://services.gradle.org/distributions/gradle-8.0-bin.zip
    Pattern pattern = Pattern.compile("(?<=\\-)(.*?)(?=\\-)");
    Matcher matcher = pattern.matcher(distributionUrl);
    if (matcher.find()) {
      return matcher.group();
    } else {
      return null;
    }
  }

  private static List<String> readRequirementsTxts(final Path path) {
    try (Stream<Path> pathStream = Files.list(path)) {
      return pathStream
          .filter(
              stream ->
                  stream.getFileName().toString().startsWith("requirements")
                      && stream.getFileName().toString().contains(".txt"))
          .map(stream -> stream.getFileName().toString())
          .toList();
    } catch (Exception ex) {
      throw new RuntimeException(
          "Requirements Texts Files not found in the repo path provided!", ex);
    }
  }

  // TODO latest versions service
  private static AppDataLatestVersions getLatestVersions() {
    return null;
  }

  private static void validateLatestVersion(final Object latestVersion) {
    Field[] fields = latestVersion.getClass().getDeclaredFields();
    try {
      for (Field field : fields) {
        field.setAccessible(true);
        if (field.getType().equals(LatestVersion.class)) {
          LatestVersion value = (LatestVersion) field.get(latestVersion);
          if (value == null) {
            throw new RuntimeException(
                String.format("Field %s doesn't have value", field.getName()));
          }
        }
      }
    } catch (Exception ex) {
      log.error("Validate Latest Version: [{}]", latestVersion, ex);
      throw new RuntimeException("Latest Version Value Check Exception");
    }
  }
}
