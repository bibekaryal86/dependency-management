package dep.mgmt.config;

import dep.mgmt.model.AppData;
import dep.mgmt.model.AppDataLatestVersions;
import dep.mgmt.model.AppDataRepository;
import dep.mgmt.model.AppDataScriptFile;
import dep.mgmt.model.entity.DependencyEntity;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CacheConfig {
  private static AppData APP_DATA = null;
  private static Map<String, DependencyEntity> GRADLE_DEPENDENCIES_MAP = Collections.emptyMap();
  private static Map<String, DependencyEntity> GRADLE_PLUGINS_MAP = Collections.emptyMap();
  private static Map<String, DependencyEntity> NPM_DEPENDENCIES_MAP = Collections.emptyMap();
  private static Map<String, DependencyEntity> PYTHON_PACKAGES_MAP = Collections.emptyMap();

  public static AppData getAppData() {
    return APP_DATA;
  }

  public static AppData setAppData(
      final Map<String, String> argsMap,
      final List<AppDataScriptFile> scriptFiles,
      final List<AppDataRepository> repositories,
      final AppDataLatestVersions latestVersions) {
    APP_DATA = new AppData(argsMap, scriptFiles, repositories, latestVersions);
    return APP_DATA;
  }

  public static void resetAppData() {
    APP_DATA = null;
  }

  public static Map<String, DependencyEntity> getGradleDependenciesMap() {
    return GRADLE_DEPENDENCIES_MAP;
  }

  public static Map<String, DependencyEntity> setGradleDependenciesMap(
      final Map<String, DependencyEntity> gradleDependenciesMap) {
    GRADLE_DEPENDENCIES_MAP = Map.copyOf(gradleDependenciesMap);
    return GRADLE_DEPENDENCIES_MAP;
  }

  public static void resetGradleDependenciesMap() {
    GRADLE_DEPENDENCIES_MAP = Collections.emptyMap();
  }

  public static Map<String, DependencyEntity> getGradlePluginsMap() {
    return GRADLE_PLUGINS_MAP;
  }

  public static Map<String, DependencyEntity> setGradlePluginsMap(
          final Map<String, DependencyEntity> gradlePluginsMap) {
    GRADLE_PLUGINS_MAP = Map.copyOf(gradlePluginsMap);
    return GRADLE_PLUGINS_MAP;
  }

  public static void resetGradlePluginsMap() {
    GRADLE_PLUGINS_MAP = Collections.emptyMap();
  }

  public static Map<String, DependencyEntity> getNpmDependenciesMap() {
    return NPM_DEPENDENCIES_MAP;
  }

  public static Map<String, DependencyEntity> setNpmDependenciesMap(
      final Map<String, DependencyEntity> npmDependenciesMap) {
    NPM_DEPENDENCIES_MAP = Map.copyOf(npmDependenciesMap);
    return NPM_DEPENDENCIES_MAP;
  }

  public static void resetNpmDependenciesMap() {
    NPM_DEPENDENCIES_MAP = Collections.emptyMap();
  }

  public static Map<String, DependencyEntity> getPythonPackagesMap() {
    return PYTHON_PACKAGES_MAP;
  }

  public static Map<String, DependencyEntity> setPythonPackagesMap(
      final Map<String, DependencyEntity> pythonPackages) {
    PYTHON_PACKAGES_MAP = Map.copyOf(pythonPackages);
    return PYTHON_PACKAGES_MAP;
  }

  public static void resetPythonPackagesMap() {
    PYTHON_PACKAGES_MAP = Collections.emptyMap();
  }
}
