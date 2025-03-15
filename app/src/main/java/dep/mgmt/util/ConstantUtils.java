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
          ENV_MAILJET_EMAIL_ADDRESS);

  // NGINX
  public static final String ENV_PORT_DEFAULT = "8080";
  public static final int BOSS_GROUP_THREADS = 1;
  public static final int WORKER_GROUP_THREADS = 8;
  public static final int CONNECT_TIMEOUT_MILLIS = 5000; // 5 seconds
  public static final int MAX_CONTENT_LENGTH = 1048576; // 1MB
  public static final String CONTENT_LENGTH_DEFAULT = "0";

  // SECURITY
  public static final List<String> NO_AUTH_URIS = List.of("/tests/ping");

  // RESPONSES
  public static final String NOT_AUTHENTICATED = "Not Authenticated...";
  public static final String NOT_AUTHORIZED = "Not Authorized...";

  // REPOSITORIES
  public static final String MONGODB_DATABASE_NAME = "repository";
  public static final String MONGODB_COLLECTION_GRADLE_DEPENDENCY = "gradle_dependency";
  public static final String MONGODB_COLLECTION_GRADLE_PLUGIN = "gradle_plugin";
  public static final String MONGODB_COLLECTION_LATEST_VERSION = "latest_version";
  public static final String MONGODB_COLLECTION_NPM_DEPENDENCY = "npm_dependency";
  public static final String MONGODB_COLLECTION_PROCESS_SUMMARY = "process_summary";
  public static final String MONGODB_COLLECTION_PYTHON_PACKAGE = "python_package";
  public static final String MONGODB_COLUMN_ID = "_id";
  public static final String MONGODB_COLUMN_UPDATE_DATETIME = "updateDateTime";
  public static final String MONGODB_COLUMN_UPDATE_TYPE = "updateType";

  // ENDPOINTS
  public static final String JAVA_RELEASES_ENDPOINT =
      "https://api.adoptium.net/v3/info/release_versions?heap_size=normal&image_type=jdk&lts=true&page=0&page_size=50&project=jdk&release_type=ga&semver=false&sort_method=DEFAULT&sort_order=DESC&vendor=eclipse";
  public static final String NODE_RELEASES_ENDPOINT = "https://nodejs.org/dist/index.json";
  public static final String MAVEN_SEARCH_ENDPOINT =
      "https://search.maven.org/solrsearch/select?core=gav&rows=5&wt=json&q=g:%s+AND+a:%s";
  public static final String PYPI_SEARCH_ENDPOINT = "https://pypi.org/pypi/%s/json";
  public static final String GCP_RUNTIME_SUPPORT_ENDPOINT =
      "https://cloud.google.com/appengine/docs/standard/lifecycle/support-schedule";
  public static final String DOCKER_TAG_LOOKUP_ENDPOINT =
      "https://hub.docker.com/v2/repositories/library/%s/tags/%s/";
  public static final String GITHUB_RELEASES_ENDPOINT = "https://api.github.com/repos/%s/%s/%s";
  public static final String GRADLE_PLUGINS_ENDPOINT = "https://plugins.gradle.org/plugin/%s";

  // RUNNABLES
  public static final String APP_MAIN_MODULE = "app";
  public static final String PATH_DELIMITER = "/";
  public static final String COMMAND_PATH = PATH_DELIMITER + "bin" + PATH_DELIMITER + "bash";
  public static final String SCRIPTS_DIRECTORY = "scripts";
  public static final String CHMOD_COMMAND = "chmod +x ";
  public static final String JAVA_SYSTEM_TMPDIR = System.getProperty("java.io.tmpdir");
  public static final String GRADLE_WRAPPER_PROPERTIES =
      "/gradle/wrapper/gradle-wrapper.properties";
  public static final String BUILD_GRADLE = "build.gradle";
  public static final String PYPROJECT_TOML = "pyproject.toml";
  public static final String PACKAGE_JSON = "package.json";
  public static final String BRANCH_UPDATE_DEPENDENCIES = "update_dependencies_%s";

  // http requests
  public static final AttributeKey<String> REQUEST_ID = AttributeKey.valueOf("REQUEST_ID");

  // OTHERS
  public static final int CLEANUP_BEFORE_DAYS = 45;
  public static final int SCHEDULER_TIMEOUT = 5;
  public static final int SCHEDULER_START_HOUR = 20;
  public static final int SCHEDULER_START_MINUTE = 0;
  public static final int SCHEDULER_START_SECOND = 0;
  public static final String DOCKER_JRE = "eclipse-temurin";
  public static final String DOCKER_ALPINE = "alpine";
  public static final String GITHUB_ENDPOINT_TAGS = "tags";
  public static final String GITHUB_ENDPOINT_RELEASES = "releases";
}
