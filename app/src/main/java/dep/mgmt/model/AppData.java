package dep.mgmt.model;

import java.util.List;
import java.util.Map;

public class AppData {
  private final Map<String, String> argsMap;
  private final List<AppDataScriptFile> scriptFiles;
  private final List<AppDataRepository> repositories;
  private final AppDataLatestVersions latestVersions;

  public AppData(
      final Map<String, String> argsMap,
      final List<AppDataScriptFile> scriptFiles,
      final List<AppDataRepository> repositories,
      final AppDataLatestVersions latestVersions) {
    this.argsMap = argsMap;
    this.scriptFiles = scriptFiles;
    this.repositories = repositories;
    this.latestVersions = latestVersions;
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

  public AppDataLatestVersions getLatestVersions() {
    return latestVersions;
  }

  @Override
  public String toString() {
    return "AppData{"
        + "argsMap="
        + argsMap
        + ", scriptFiles="
        + scriptFiles
        + ", repositories="
        + repositories
        + ", latestVersions="
        + latestVersions
        + '}';
  }
}
