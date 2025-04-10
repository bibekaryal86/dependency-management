package dep.mgmt.server;

public class Endpoints {
  private static final String CONTEXT_PATH = "dep-mgmt";

  public static final String APP_TESTS_CONTROLLER = "/" + CONTEXT_PATH + "/tests";
  public static final String APP_TESTS_PING = APP_TESTS_CONTROLLER + "/ping";
  public static final String APP_TESTS_RESET = APP_TESTS_CONTROLLER + "/reset";
  public static final String APP_TESTS_RATE = APP_TESTS_CONTROLLER + "/github-rate-limit";
  public static final String APP_TESTS_TASKS = APP_TESTS_CONTROLLER + "/task-queue";

  public static final String UPDATE_DEPENDENCIES_CONTROLLER = "/" + CONTEXT_PATH + "/api/v1/update";

  public static final String MONGO_REPO_CONTROLLER = "/" + CONTEXT_PATH + "/api/v1/mongo";
  public static final String MONGO_GRADLE_DEPENDENCY = MONGO_REPO_CONTROLLER + "/gradle-dependency";
  public static final String MONGO_GRADLE_PLUGIN = MONGO_REPO_CONTROLLER + "/gradle-plugin";
  public static final String MONGO_NODE_DEPENDENCY = MONGO_REPO_CONTROLLER + "/node-dependency";
  public static final String MONGO_PYTHON_PACKAGE = MONGO_REPO_CONTROLLER + "/python-package";
  public static final String MONGO_REPO_UPDATE = MONGO_REPO_CONTROLLER + "/update";
  public static final String MONGO_LATEST_VERSION = MONGO_REPO_CONTROLLER + "/latest-version";
  public static final String MONGO_PROCESS_SUMMARY = MONGO_REPO_CONTROLLER + "/process-summary";
  public static final String MONGO_EXCLUDED_REPO = MONGO_REPO_CONTROLLER + "/excluded-repo";
}
