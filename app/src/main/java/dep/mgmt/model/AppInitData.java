package dep.mgmt.model;

import java.util.List;
import java.util.Map;

public class AppInitData {
  private final Map<String, String> argsMap;
  private final List<AppDataScriptFile> scriptFiles;
  private final List<AppDataRepository> repositories;
  private final AppDataLatestVersions latestVersionsModel;

  public AppInitData(
      final Map<String, String> argsMap,
      final List<AppDataScriptFile> scriptFiles,
      final List<AppDataRepository> repositories,
      final AppDataLatestVersions latestVersionsModel) {
    this.argsMap = argsMap;
    this.scriptFiles = scriptFiles;
    this.repositories = repositories;
    this.latestVersionsModel = latestVersionsModel;
  }

  public Map<String, String> getArgsMap() {
    return argsMap;
  }

  public List<AppDataScriptFile> getScriptFiles() {
    return scriptFiles;
  }

  public List<AppDataRepository> getRepositories() {
    return repositories;
  }

  public AppDataLatestVersions getLatestVersionsModel() {
    return latestVersionsModel;
  }

  @Override
  public String toString() {
    return "AppInitData{"
        + "argsMap="
        + argsMap
        + ", scriptFiles="
        + scriptFiles
        + ", repositories="
        + repositories
        + ", latestVersionsModel="
        + latestVersionsModel
        + '}';
  }
}
