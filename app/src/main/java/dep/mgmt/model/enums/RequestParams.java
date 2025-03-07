package dep.mgmt.model.enums;

public class RequestParams {
  public enum UpdateType {
    ALL,
    GITHUB_BRANCH_DELETE,
    GITHUB_PR_CREATE,
    GITHUB_PULL,
    GITHUB_MERGE,
    GITHUB_RESET,
    GRADLE_DEPENDENCIES,
    GRADLE_SPOTLESS,
    NPM_DEPENDENCIES,
    NPM_SNAPSHOT,
    PYTHON_DEPENDENCIES
  }

  public enum CacheType {
    ALL,
    APP_INIT_DATA,
    PLUGINS_MAP,
    DEPENDENCIES_MAP,
    PACKAGES_MAP,
    NPMSKIPS_MAP
  }

  public enum LogLevelChange {
    INFO,
    DEBUG
  }
}
