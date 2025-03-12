package dep.mgmt.service;

import dep.mgmt.config.MongoDbConfig;
import dep.mgmt.model.AppDataLatestVersions;
import dep.mgmt.model.entity.LatestVersionEntity;
import dep.mgmt.repository.LatestVersionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

public class LatestVersionService {

  private static final Logger log = LoggerFactory.getLogger(LatestVersionService.class);

  private final LatestVersionRepository latestVersionRepository;

  public LatestVersionService() {
    this.latestVersionRepository = new LatestVersionRepository(MongoDbConfig.getDatabase());
  }

  public Optional<LatestVersionEntity> getMostRecentLatestVersionEntity() {
    log.debug("Get Most Recent Latest Version Entity...");
    return latestVersionRepository.findFirstByOrderByUpdateDateTimeDesc();
  }


  public void saveLatestVersion(final AppDataLatestVersions latestVersions) {
    log.info("Save Latest Version: [{}]", latestVersions);

    final LatestVersionEntity latestVersionEntity = new LatestVersionEntity();
    latestVersionEntity.setNginx(latestVersions.getLatestVersionServers().getNginx());
    latestVersionEntity.setGradle(latestVersions.getLatestVersionTools().getGradle());
    latestVersionEntity.setFlyway(latestVersions.getLatestVersionTools().getFlyway());
    latestVersionEntity.setCheckout(latestVersions.getLatestVersionGithubActions().getCheckout());
    latestVersionEntity.setSetupJava(latestVersions.getLatestVersionGithubActions().getSetupJava());
    latestVersionEntity.setSetupGradle(latestVersions.getLatestVersionGithubActions().getSetupGradle());
    latestVersionEntity.setSetupNode(latestVersions.getLatestVersionGithubActions().getSetupNode());
    latestVersionEntity.setSetupPython(latestVersions.getLatestVersionGithubActions().getSetupPython());
    latestVersionEntity.setCodeql(latestVersions.getLatestVersionGithubActions().getCodeql());
    latestVersionEntity.setJava(latestVersions.getLatestVersionLanguages().getJava());
    latestVersionEntity.setNode(latestVersions.getLatestVersionLanguages().getNode());
    latestVersionEntity.setPython(latestVersions.getLatestVersionLanguages().getPython());
    latestVersionEntity.setUpdateDateTime(LocalDateTime.now());
    latestVersionRepository.insert(latestVersionEntity);
  }
}
