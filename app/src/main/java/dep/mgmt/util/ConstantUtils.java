package dep.mgmt.util;

import io.netty.util.AttributeKey;
import java.util.List;

public class ConstantUtils {
  // provided at runtime
  public static final String ENV_SERVER_PORT = "PORT";
  public static final String ENV_SELF_USER = "SELF_USERNAME";
  public static final String ENV_SELF_PWD = "SELF_PASSWORD";
  public static final String ENV_DB_HOST = "DB_HOST";
  public static final String ENV_DB_NAME = "DB_NAME";
  public static final String ENV_DB_USER = "DB_USERNAME";
  public static final String ENV_DB_PWD = "DB_PASSWORD";
  public static final String ENV_REPO_HOME = "REPO_HOME";
  public static final String ENV_SEND_EMAIL = "SEND_EMAIL";
  public static final String ENV_MAILJET_PUBLIC_KEY = "MJ_PUBLIC";
  public static final String ENV_MAILJET_PRIVATE_KEY = "MJ_PUBLIC";
  public static final String ENV_MAILJET_EMAIL_ADDRESS = "MJ_EMAIL";
  public static final String ENV_GITHUB_OWNER = "GH_OWNER";
  public static final String ENV_GITHUB_TOKEN = "GH_TOKEN";
  public static final List<String> ENV_KEY_NAMES =
      List.of(
          ENV_SERVER_PORT,
          ENV_SELF_USER,
          ENV_SELF_PWD,
          ENV_DB_HOST,
          ENV_DB_NAME,
          ENV_DB_USER,
          ENV_DB_PWD,
          ENV_REPO_HOME,
          ENV_SEND_EMAIL,
          ENV_MAILJET_PUBLIC_KEY,
          ENV_MAILJET_PRIVATE_KEY,
          ENV_MAILJET_EMAIL_ADDRESS,
          ENV_GITHUB_OWNER,
          ENV_GITHUB_TOKEN);

  // NGINX
  public static final String ENV_PORT_DEFAULT = "8080";
  public static final int BOSS_GROUP_THREADS = 1;
  public static final int WORKER_GROUP_THREADS = 8;
  public static final int CONNECT_TIMEOUT_MILLIS = 5000; // 5 seconds
  public static final int MAX_CONTENT_LENGTH = 1048576; // 1MB
  public static final String CONTENT_LENGTH_DEFAULT = "0";

  // RESPONSES
  public static final String NOT_AUTHENTICATED = "Not Authenticated...";
  public static final String NOT_AUTHORIZED = "Not Authorized...";

  // REPOSITORIES
  public static final String MONGODB_DATABASE_NAME = "repository";
  public static final String MONGODB_COLLECTION_GRADLE_DEPENDENCY = "gradle_dependency";
  public static final String MONGODB_COLLECTION_GRADLE_PLUGIN = "gradle_plugin";
  public static final String MONGODB_COLLECTION_LATEST_VERSION = "latest_version";
  public static final String MONGODB_COLLECTION_NODE_DEPENDENCY = "node_dependency";
  public static final String MONGODB_COLLECTION_PROCESS_SUMMARY = "process_summary";
  public static final String MONGODB_COLLECTION_PYTHON_PACKAGE = "python_package";
  public static final String MONGODB_COLLECTION_EXCLUDED_REPO = "excluded_repo";
  public static final String MONGODB_COLLECTION_LOG_ENTRY = "log_entry";
  public static final String MONGODB_COLUMN_ID = "_id";
  public static final String MONGODB_COLUMN_NAME = "name";
  public static final String MONGODB_COLUMN_UPDATE_DATETIME = "updateDateTime";
  public static final String MONGODB_COLUMN_UPDATE_TYPE = "updateType";

  // ENDPOINTS
  public static final String JAVA_RELEASES_ENDPOINT =
      "https://api.adoptium.net/v3/info/release_versions?heap_size=normal&image_type=jdk&lts=true&page=0&page_size=50&project=jdk&release_type=ga&semver=false&sort_method=DEFAULT&sort_order=DESC&vendor=eclipse";
  public static final String NODE_RELEASES_ENDPOINT = "https://nodejs.org/dist/index.json";
  public static final String MAVEN_SEARCH_ENDPOINT =
      "https://search.maven.org/solrsearch/select?core=gav&rows=5&wt=json&q=g:%s+AND+a:%s";
  public static final String NPM_REGISTRY_ENDPOINT = "https://registry.npmjs.org/%s";
  public static final String PYPI_SEARCH_ENDPOINT = "https://pypi.org/pypi/%s/json";
  public static final String GCP_RUNTIME_SUPPORT_ENDPOINT =
      "https://cloud.google.com/appengine/docs/standard/lifecycle/support-schedule";
  public static final String DOCKER_TAG_LOOKUP_ENDPOINT =
      "https://hub.docker.com/v2/repositories/library/%s/tags/%s/";
  public static final String GITHUB_RELEASES_ENDPOINT = "https://api.github.com/repos/%s/%s/%s";
  public static final String GRADLE_PLUGINS_ENDPOINT = "https://plugins.gradle.org/plugin/%s";

  public static final String GITHUB_RATE_LIMIT_ENDPOINT = "https://api.github.com/rate_limit";
  public static final String GITHUB_LIST_BRANCHES_ENDPOINT =
      "https://api.github.com/repos/%s/%s/branches";
  public static final String GITHUB_CREATE_PR_ENDPOINT = "https://api.github.com/repos/%s/%s/pulls";
  public static final String GITHUB_LIST_PRS_ENDPOINT = "https://api.github.com/repos/%s/%s/pulls";
  public static final String GITHUB_MERGE_PR_ENDPOINT =
      "https://api.github.com/repos/%s/%s/pulls/%s/merge";
  public static final String GITHUB_LIST_CHECKS_ENDPOINT =
      "https://api.github.com/repos/%s/%s/actions/runs?per_page=5";
  public static final String GITHUB_PR_TITLE_BODY =
      "Dependencies Updated (https://bit.ly/dep-mgmt)";
  public static final String GITHUB_PR_BASE_BRANCH = "main";
  public static final String GITHUB_PR_MERGE_METHOD = "squash";

  // RUNNABLES
  public static final String APP_MAIN_MODULE = "app";
  public static final String PATH_DELIMITER = "/";
  public static final String COMMAND_PATH = PATH_DELIMITER + "bin" + PATH_DELIMITER + "bash";
  public static final String SCRIPTS_DIRECTORY = "scripts";
  public static final String SCRIPTS_FILE = SCRIPTS_DIRECTORY + PATH_DELIMITER + "scripts.txt";
  public static final String CHMOD_COMMAND = "chmod +x ";
  public static final String JAVA_SYSTEM_TMPDIR = System.getProperty("java.io.tmpdir");
  public static final String GRADLE_WRAPPER_PROPERTIES =
      "/gradle/wrapper/gradle-wrapper.properties";
  public static final String BUILD_GRADLE = "build.gradle";
  public static final String PYPROJECT_TOML = "pyproject.toml";
  public static final String PACKAGE_JSON = "package.json";
  public static final String DEPENDENCIES = "dependencies";
  public static final String DEPENDENCIES_DEV = "devDependencies";
  public static final String DEPENDENCIES_OPTIONAL = "optionalDependencies";
  public static final String ENGINES = "engines";
  public static final String BRANCH_UPDATE_DEPENDENCIES = "update_dependencies_%s";

  // http requests
  public static final AttributeKey<String> REQUEST_ID = AttributeKey.valueOf("REQUEST_ID");

  // SCRIPT FILE NAMES
  public static final String SCRIPT_DELETE = "DELETE_UPDATE_DEPS";
  public static final String SCRIPT_DELETE_ONE = "DELETE_UPDATE_DEPS_ONE";
  public static final String SCRIPT_SPOTLESS = "GRADLE_SPOTLESS";
  public static final String SCRIPT_SNAPSHOT = "NPM_SNAPSHOT";
  public static final String SCRIPT_RESET_PULL = "REPO_RESET_PULL";
  public static final String SCRIPT_RESET_PULL_ONE = "REPO_RESET_PULL_ONE";
  public static final String SCRIPT_UPDATE_INIT = "UPDATE_DEPS_INIT_EXIT";
  public static final String SCRIPT_UPDATE_EXEC = "UPDATE_DEPS_EXEC";

  // UPDATES
  public static final int CLEANUP_BEFORE_DAYS = 30;
  public static final int SCHEDULER_TIMEOUT = 5;
  public static final int SCHEDULER_START_HOUR_UPDATE_REPO = 20;
  public static final int SCHEDULER_START_MINUTE = 0;
  public static final int SCHEDULER_START_SECOND = 0;
  public static final long TASK_DELAY_ZERO = 0L;
  public static final long TASK_DELAY_DEFAULT = 1000; // 1 second
  public static final long TASK_DELAY_PULL_REQUEST = 1000 * 60; // 1 minute
  public static final long TASK_DELAY_PULL_REQUEST_TRY = 1000 * 60 * 5; // 5 minutes
  public static final long TASK_DELAY_PULL_REQUEST_RETRY = 1000 * 60 * 30; // 30 minutes
  public static final String DOCKER_JRE = "eclipse-temurin";
  public static final String DOCKER_ALPINE = "alpine";
  public static final String GITHUB_ENDPOINT_TAGS = "tags";
  public static final String GITHUB_ENDPOINT_RELEASES = "releases";
  public static final String JAVA_JDK = "jdk";
  public static final String JAVA_JRE = "jre";
  public static final String VERSION_LTS = "LTS";
  public static final String GCP_RUNTIME_ID = "Runtime ID";
  public static final String FLYWAY_NAME = "flyway";
  public static final String JAVA_NAME = "java";
  public static final String NODE_NAME = "node";
  public static final String NODEJS_NAME = "nodejs";
  public static final String PYTHON_NAME = "python";
  public static final String CPYTHON_NAME = "cpython";
  public static final String GRADLE_NAME = "gradle";
  public static final String GITHUB_NAME = "github";
  public static final String NGINX_NAME = "nginx";
  public static final String GITHUB_ACTIONS_NAME = "actions";
  public static final String GITHUB_ACTIONS_CHECKOUT = "checkout";
  public static final String GITHUB_ACTIONS_SETUP_JAVA = "setup-java";
  public static final String GITHUB_ACTIONS_SETUP_NODE = "setup-node";
  public static final String GITHUB_ACTIONS_SETUP_PYTHON = "setup-python";
  public static final String GITHUB_ACTIONS_CODEQL = "codeql-action";

  // UPDATES
  public static final String QUEUE_RESET = "RESET_DATA";
  public static final String QUEUE_SET = "SET_DATA";
  public static final String QUEUE_UPDATE = "UPDATE_DATA";
  public static final String QUEUE_FILES = "RECREATE_FILES";
  public static final String QUEUE_UPDATE_DEPENDENCIES = "UPDATE_DEPENDENCIES_%s";
  public static final String QUEUE_CREATE_PULL_REQUESTS = "PULL_REQUESTS_CREATE";
  public static final String QUEUE_MERGE_PULL_REQUESTS = "PULL_REQUESTS_MERGE";
  public static final String QUEUE_MERGE_PULL_REQUESTS_RETRY = "PULL_REQUESTS_MERGE_RETRY";
  public static final String QUEUE_PROCESS_SUMMARY = "PROCESS_SUMMARY";
  public static final String QUEUE_DATA_CLEANUP = "DATA_CLEANUP";
  public static final String TASK_RESET_APP_DATA = "RESET_APP_DATA";
  public static final String TASK_SET_APP_DATA = "SET_APP_DATA";
  public static final String TASK_RESET_GRADLE_DEPENDENCIES = "RESET_GRADLE_DEPENDENCIES";
  public static final String TASK_SET_GRADLE_DEPENDENCIES = "SET_GRADLE_DEPENDENCIES";
  public static final String TASK_UPDATE_GRADLE_DEPENDENCIES = "UPDATE_GRADLE_DEPENDENCIES";
  public static final String TASK_RESET_GRADLE_PLUGINS = "RESET_GRADLE_PLUGINS";
  public static final String TASK_SET_GRADLE_PLUGINS = "SET_GRADLE_PLUGINS";
  public static final String TASK_UPDATE_GRADLE_PLUGINS = "UPDATE_GRADLE_PLUGINS";
  public static final String TASK_RESET_NODE_DEPENDENCIES = "RESET_NODE_DEPENDENCIES";
  public static final String TASK_SET_NODE_DEPENDENCIES = "SET_NODE_DEPENDENCIES";
  public static final String TASK_UPDATE_NODE_DEPENDENCIES = "UPDATE_NODE_DEPENDENCIES";
  public static final String TASK_RESET_PYTHON_PACKAGES = "RESET_PYTHON_PACKAGES";
  public static final String TASK_SET_PYTHON_PACKAGES = "SET_PYTHON_PACKAGES";
  public static final String TASK_UPDATE_PYTHON_PACKAGES = "UPDATE_PYTHON_PACKAGES";
  public static final String TASK_RESET_EXCLUDED_REPOS = "RESET_EXCLUDED_REPOS";
  public static final String TASK_SET_EXCLUDED_REPOS = "SET_EXCLUDED_REPOS";
  public static final String TASK_DELETE_SCRIPT_FILES = "DELETE_SCRIPT_FILES";
  public static final String TASK_CREATE_SCRIPT_FILES = "CREATE_SCRIPT_FILES";
  public static final String TASK_GITHUB_RESET_PULL = "GITHUB_RESET_PULL";
  public static final String TASK_GITHUB_BRANCH_DELETE = "GITHUB_BRANCH_DELETE";
  public static final String TASK_GRADLE_SPOTLESS = "GRADLE_SPOTLESS";
  public static final String TASK_NPM_SNAPSHOTS = "NPM_SNAPSHOTS";
  public static final String TASK_UPDATE_DEPENDENCIES = "UPDATE_DEPENDENCIES_%s_%s";
  public static final String TASK_PULL_REQUESTS = "PULL_REQUESTS_%s_%s";
  public static final String TASK_RESET_PROCESS_SUMMARIES = "RESET_PROCESS_SUMMARIES";
  public static final String TASK_GITHUB_RATE_LIMIT = "GITHUB_RATE_LIMIT";
  public static final String TASK_LOG_CAPTURE_STOP = "STOP_LOG_CAPTURE";
  public static final String TASK_LOG_CAPTURE_SAVE = "SAVE_LOG_CAPTURE";
  public static final String TASK_DATA_CLEANUP_PROCESS_SUMMARY = "CLEANUP_PROCESS_SUMMARY";
  public static final String TASK_DATA_CLEANUP_LOG_ENTRY = "CLEANUP_LOG_ENTRY";
  public static final String TASK_DATA_CLEANUP_LATEST_VERSION = "CLEANUP_LATEST_VERSION";

  public static final String APPENDER_INIT = "INIT";
  public static final String APPENDER_EXEC = "EXEC";
  public static final String APPENDER_EXIT = "EXIT";
  public static final String APPENDER_QUEUE_NAME = "_QUEUE";
  public static final String APPENDER_TASK_NAME = "_TASK";
  public static final String APPENDER_RETRY = "_RETRY";
  public static final String APPENDER_LOCAL = "_LOCAL";
  public static final String APPENDER_REMOTE = "_REMOTE";

  public static final String RESPONSE_TESTS_PING = "{\"ping\": \"successful\"}";
  public static final String RESPONSE_REQUEST_SUBMITTED = "{\"request\": \"submitted\"}";
}
