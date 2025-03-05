package dep.mgmt.util;

import io.netty.util.AttributeKey;
import java.util.List;

public class ConstantUtils {
  // provided at runtime
  public static final String ENV_SERVER_PORT = "PORT";
  public static final String ENV_SELF_USERNAME = "SELF_USERNAME";
  public static final String ENV_SELF_PASSWORD = "SELF_PASSWORD";
  public static final String ENV_DB_HOST = "DB_HOST";
  public static final String ENV_DB_NAME = "DB_NAME";
  public static final String ENV_DB_USERNAME = "DB_USERNAME";
  public static final String ENV_DB_PASSWORD = "DB_PASSWORD";
  public static final String ENV_SEND_EMAIL = "SEND_EMAIL";
  public static final String ENV_MAILJET_PUBLIC_KEY = "MJ_PUBLIC";
  public static final String ENV_MAILJET_PRIVATE_KEY = "MJ_PRIVATE";
  public static final String ENV_MAILJET_EMAIL_ADDRESS = "MJ_EMAIL";
  public static final List<String> ENV_KEY_NAMES =
      List.of(
          ENV_SERVER_PORT,
          ENV_SELF_USERNAME,
          ENV_SELF_PASSWORD,
          ENV_DB_HOST,
          ENV_DB_NAME,
          ENV_DB_USERNAME,
          ENV_DB_PASSWORD,
          ENV_SEND_EMAIL,
          ENV_MAILJET_PUBLIC_KEY,
          ENV_MAILJET_PRIVATE_KEY,
          ENV_MAILJET_EMAIL_ADDRESS);

  public static final String ENV_PORT_DEFAULT = "8080";

  // REQUEST_ID
  public static final AttributeKey<String> REQUEST_ID = AttributeKey.valueOf("REQUEST_ID");

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

  // others
  public static final String PATH_DELIMITER = "/";
  public static final String GRADLE_WRAPPER_REGEX = "(?<=\\-)(.*?)(?=\\-)";
  public static final String GRADLE_BUILD_BLOCK_END_REGEX = "([a-z]+\\s\\{)";
  public static final String GRADLE_BUILD_DEPENDENCIES_REGEX = "(?<=\\%s)(.*?)(?=\\%s)";
  public static final String GRADLE_BUILD_DEFINITION_REGEX = "\\w+\\s+\\w+";
  public static final String GRADLE_JAVA_VERSION_REGEX_1 = "JavaVersion.VERSION_(\\d+)";
  public static final String GRADLE_JAVA_VERSION_REGEX_2 = "JavaLanguageVersion.of\\((\\d+)\\)";
  public static final String PYTHON_PYPROJECT_TOML_BUILDTOOLS_REGEX = "'(.*?)'";
  public static final String COMMAND_PATH = PATH_DELIMITER + "bin" + PATH_DELIMITER + "bash";
  public static final String COMMAND_WINDOWS = "cmd.exe";
  public static final String SCRIPTS_DIRECTORY = "scripts";
  public static final String CHMOD_COMMAND = "chmod +x ";
  public static final String JAVA_SYSTEM_TMPDIR = System.getProperty("java.io.tmpdir");
  public static final String APP_MAIN_MODULE = "app";
  public static final String GRADLE_WRAPPER_PROPERTIES =
      "/gradle/wrapper/gradle-wrapper.properties";
  public static final String BUILD_GRADLE = "build.gradle";
  public static final String PYPROJECT_TOML = "pyproject.toml";
  public static final String PACKAGE_JSON = "package.json";
  public static final String DOCKER_JRE = "eclipse-temurin";
  public static final String MONGODB_DATABASE_NAME = "repository";
  public static final String MONGODB_COLLECTION_DEPENDENCIES = "dependencies";
  public static final String MONGODB_COLLECTION_PLUGINS = "plugins";
  public static final String MONGODB_COLLECTION_PACKAGES = "packages";
  public static final String MONGODB_COLLECTION_NPMSKIPS = "npm_skips";
  public static final String MONGODB_COLLECTION_PROCESS_SUMMARIES = "process_summaries";
  public static final String MONGODB_COLLECTION_LATEST_VERSIONS = "latest_versions";
  public static final String BRANCH_UPDATE_DEPENDENCIES = "update_dependencies_%s";

  public static final String DOCKER_ALPINE = "alpine";

  // endpoints
  public static final String MONGODB_CONNECTION_STRING =
      "mongodb+srv://%s:%s@cluster0.anwaeio.mongodb.net/?retryWrites=true&w=majority";
  public static final String JAVA_RELEASES_ENDPOINT =
      "https://api.adoptium.net/v3/info/release_versions?heap_size=normal&image_type=jdk&lts=true&page=0&page_size=50&project=jdk&release_type=ga&semver=false&sort_method=DEFAULT&sort_order=DESC&vendor=eclipse";
  public static final String PYTHON_RELEASES_ENDPOINT =
      "https://api.github.com/repos/python/cpython/tags";
  public static final String NODE_RELEASES_ENDPOINT = "https://nodejs.org/dist/index.json";
  public static final String GRADLE_RELEASES_ENDPOINT =
      "https://api.github.com/repos/gradle/gradle/releases";
  public static final String NGINX_TAGS_ENDPOINT = "https://api.github.com/repos/nginx/nginx/tags";
  public static final String GITHUB_ACTIONS_RELEASES_ENDPOINT =
      "https://api.github.com/repos/%s/%s/releases";
  public static final String FLYWAY_RELEASES_ENDPOINT =
      "https://api.github.com/repos/flyway/flyway/releases";
  public static final String GRADLE_PLUGINS_ENDPOINT = "https://plugins.gradle.org/plugin/%s";
  public static final String MAVEN_SEARCH_ENDPOINT =
      "https://search.maven.org/solrsearch/select?core=gav&rows=5&wt=json&q=g:%s+AND+a:%s";
  public static final String PYPI_SEARCH_ENDPOINT = "https://pypi.org/pypi/%s/json";
  public static final String GCP_RUNTIME_SUPPORT_ENDPOINT =
      "https://cloud.google.com/appengine/docs/standard/lifecycle/support-schedule";
  public static final String DOCKER_TAG_LOOKUP_ENDPOINT =
      "https://hub.docker.com/v2/repositories/library/%s/tags/%s/";
}
