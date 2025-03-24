package dep.mgmt.service;

import dep.mgmt.config.MongoDbConfig;
import dep.mgmt.model.AppDataLatestVersions;
import dep.mgmt.model.LatestVersion;
import dep.mgmt.model.entity.LatestVersionEntity;
import dep.mgmt.repository.LatestVersionRepository;
import dep.mgmt.util.ConstantUtils;
import java.time.LocalDateTime;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LatestVersionService {

  private static final Logger log = LoggerFactory.getLogger(LatestVersionService.class);

  private final LatestVersionRepository latestVersionRepository;
  private final FlywayVersionService flywayVersionService;
  private final GcpRuntimesVersionService gcpRuntimesVersionService;
  private final GithubActionsVersionService githubActionsVersionService;
  private final GradleVersionService gradleVersionService;
  private final JavaVersionService javaVersionService;
  private final NginxVersionService nginxVersionService;
  private final NodeVersionService nodeVersionService;
  private final PythonVersionService pythonVersionService;

  public LatestVersionService() {
    this.latestVersionRepository = new LatestVersionRepository(MongoDbConfig.getDatabase());
    this.flywayVersionService = new FlywayVersionService();
    this.gcpRuntimesVersionService = new GcpRuntimesVersionService();
    this.githubActionsVersionService = new GithubActionsVersionService();
    this.gradleVersionService = new GradleVersionService();
    this.javaVersionService = new JavaVersionService();
    this.nginxVersionService = new NginxVersionService();
    this.nodeVersionService = new NodeVersionService();
    this.pythonVersionService = new PythonVersionService();
  }

  public AppDataLatestVersions getLatestVersion() {
    log.debug("Get Latest Version...");

    LatestVersionEntity latestVersionEntity =
        latestVersionRepository.findFirstByOrderByUpdateDateTimeDesc();

    if (latestVersionEntity == null) {
      return null;
    }

    Map<String, String> latestGcpRuntimesVersions =
        gcpRuntimesVersionService.getGcpRuntimesVersions();
    final String latestGcpVersionPython = latestGcpRuntimesVersions.get(ConstantUtils.PYTHON_NAME);
    final String latestGcpVersionJava = latestGcpRuntimesVersions.get(ConstantUtils.JAVA_NAME);
    final String latestGcpVersionNode = latestGcpRuntimesVersions.get(ConstantUtils.NODE_NAME);
    final String currentDockerVersionPython = latestVersionEntity.getPython().getVersionDocker();
    final String currentDockerVersionJava = latestVersionEntity.getJava().getVersionDocker();
    final String currentDockerVersionNode = latestVersionEntity.getNode().getVersionDocker();
    final String currentDockerVersionGradle = latestVersionEntity.getGradle().getVersionDocker();
    final String currentDockerVersionFlyway = latestVersionEntity.getFlyway().getVersionDocker();

    // languages
    LatestVersion python =
        pythonVersionService.getPythonVersion(latestGcpVersionPython, currentDockerVersionPython);
    LatestVersion java =
        javaVersionService.getJavaVersion(latestGcpVersionJava, currentDockerVersionJava);
    LatestVersion node =
        nodeVersionService.getNodeVersion(latestGcpVersionNode, currentDockerVersionNode);

    // actions
    LatestVersion codeql = githubActionsVersionService.getGithubActionsCodeqlActionVersion();
    LatestVersion setupPython = githubActionsVersionService.getGithubActionsSetupPythonVersion();
    LatestVersion setupNode = githubActionsVersionService.getGithubActionsSetupNodeVersion();
    LatestVersion setupGradle = githubActionsVersionService.getGithubActionsGradleVersion();
    LatestVersion setupJava = githubActionsVersionService.getGithubActionsSetupJavaVersion();
    LatestVersion checkout = githubActionsVersionService.getGithubActionsCheckoutVersion();

    // tools
    LatestVersion gradle =
        gradleVersionService.getGradleVersion(java.getVersionMajor(), currentDockerVersionGradle);
    LatestVersion flyway = flywayVersionService.getFlywayVersion();
    // servers
    LatestVersion nginx = nginxVersionService.getNginxVersion(currentDockerVersionFlyway);

    AppDataLatestVersions latestVersions =
        new AppDataLatestVersions(
            new AppDataLatestVersions.LatestVersionServers(
                nginx == null ? latestVersionEntity.getNginx() : nginx),
            new AppDataLatestVersions.LatestVersionTools(
                gradle == null ? latestVersionEntity.getGradle() : gradle,
                flyway == null ? latestVersionEntity.getFlyway() : flyway),
            new AppDataLatestVersions.LatestVersionGithubActions(
                checkout == null ? latestVersionEntity.getCheckout() : checkout,
                setupJava == null ? latestVersionEntity.getSetupJava() : setupJava,
                setupGradle == null ? latestVersionEntity.getSetupGradle() : setupGradle,
                setupNode == null ? latestVersionEntity.getSetupNode() : setupNode,
                setupPython == null ? latestVersionEntity.getSetupPython() : setupPython,
                codeql == null ? latestVersionEntity.getCodeql() : codeql),
            new AppDataLatestVersions.LatestVersionLanguages(java, node, python));

    if (checkAndSaveLatestVersionEntity(latestVersionEntity, latestVersions)) {
      saveLatestVersion(latestVersions);
    }

    return latestVersions;
  }

  private boolean checkAndSaveLatestVersionEntity(
      final LatestVersionEntity latestVersionEntity, AppDataLatestVersions latestVersionModel) {
    if (!latestVersionEntity
        .getNginx()
        .getVersionActual()
        .equals(latestVersionModel.getLatestVersionServers().getNginx().getVersionActual())) {
      return true;
    }
    if (!latestVersionEntity
        .getGradle()
        .getVersionActual()
        .equals(latestVersionModel.getLatestVersionTools().getGradle().getVersionActual())) {
      return true;
    }
    if (!latestVersionEntity
        .getCheckout()
        .getVersionActual()
        .equals(
            latestVersionModel.getLatestVersionGithubActions().getCheckout().getVersionActual())) {
      return true;
    }
    if (!latestVersionEntity
        .getSetupJava()
        .getVersionActual()
        .equals(
            latestVersionModel.getLatestVersionGithubActions().getSetupJava().getVersionActual())) {
      return true;
    }
    if (!latestVersionEntity
        .getSetupGradle()
        .getVersionActual()
        .equals(
            latestVersionModel
                .getLatestVersionGithubActions()
                .getSetupGradle()
                .getVersionActual())) {
      return true;
    }
    if (!latestVersionEntity
        .getSetupNode()
        .getVersionActual()
        .equals(
            latestVersionModel.getLatestVersionGithubActions().getSetupNode().getVersionActual())) {
      return true;
    }
    if (!latestVersionEntity
        .getSetupPython()
        .getVersionActual()
        .equals(
            latestVersionModel
                .getLatestVersionGithubActions()
                .getSetupPython()
                .getVersionActual())) {
      return true;
    }
    if (!latestVersionEntity
        .getCodeql()
        .getVersionActual()
        .equals(
            latestVersionModel.getLatestVersionGithubActions().getCodeql().getVersionActual())) {
      return true;
    }
    if (!latestVersionEntity
        .getJava()
        .getVersionActual()
        .equals(latestVersionModel.getLatestVersionLanguages().getJava().getVersionActual())) {
      return true;
    }
    if (!latestVersionEntity
        .getNode()
        .getVersionActual()
        .equals(latestVersionModel.getLatestVersionLanguages().getNode().getVersionActual())) {
      return true;
    }
    return !latestVersionEntity
        .getPython()
        .getVersionActual()
        .equals(latestVersionModel.getLatestVersionLanguages().getPython().getVersionActual());
  }

  private void saveLatestVersion(final AppDataLatestVersions latestVersions) {
    log.info("Save Latest Version: [{}]", latestVersions);

    final LatestVersionEntity latestVersionEntity = new LatestVersionEntity();
    latestVersionEntity.setNginx(latestVersions.getLatestVersionServers().getNginx());
    latestVersionEntity.setGradle(latestVersions.getLatestVersionTools().getGradle());
    latestVersionEntity.setFlyway(latestVersions.getLatestVersionTools().getFlyway());
    latestVersionEntity.setCheckout(latestVersions.getLatestVersionGithubActions().getCheckout());
    latestVersionEntity.setSetupJava(latestVersions.getLatestVersionGithubActions().getSetupJava());
    latestVersionEntity.setSetupGradle(
        latestVersions.getLatestVersionGithubActions().getSetupGradle());
    latestVersionEntity.setSetupNode(latestVersions.getLatestVersionGithubActions().getSetupNode());
    latestVersionEntity.setSetupPython(
        latestVersions.getLatestVersionGithubActions().getSetupPython());
    latestVersionEntity.setCodeql(latestVersions.getLatestVersionGithubActions().getCodeql());
    latestVersionEntity.setJava(latestVersions.getLatestVersionLanguages().getJava());
    latestVersionEntity.setNode(latestVersions.getLatestVersionLanguages().getNode());
    latestVersionEntity.setPython(latestVersions.getLatestVersionLanguages().getPython());
    latestVersionEntity.setUpdateDateTime(LocalDateTime.now());
    latestVersionRepository.insert(latestVersionEntity);
  }
}
