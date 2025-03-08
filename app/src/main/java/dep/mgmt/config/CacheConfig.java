package dep.mgmt.config;

import dep.mgmt.model.AppData;
import dep.mgmt.model.AppDataLatestVersions;
import dep.mgmt.model.AppDataRepository;
import dep.mgmt.model.AppDataScriptFile;
import java.util.List;
import java.util.Map;

public class CacheConfig {
  // TODO dependencies map, packages map, plugins map, etc

  private static AppData APP_DATA = null;

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
}
