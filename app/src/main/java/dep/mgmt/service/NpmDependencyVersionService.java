package dep.mgmt.service;

import com.fasterxml.jackson.core.type.TypeReference;
import dep.mgmt.config.CacheConfig;
import dep.mgmt.config.MongoDbConfig;
import dep.mgmt.model.entity.DependencyEntity;
import dep.mgmt.model.web.NpmRegistryResponse;
import dep.mgmt.repository.NpmDependencyRepository;
import dep.mgmt.util.ConstantUtils;
import dep.mgmt.util.ProcessUtils;
import dep.mgmt.util.VersionUtils;
import io.github.bibekaryal86.shdsvc.Connector;
import io.github.bibekaryal86.shdsvc.dtos.Enums;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NpmDependencyVersionService {

  private static final Logger log = LoggerFactory.getLogger(NpmDependencyVersionService.class);
  private final NpmDependencyRepository npmDependencyRepository;

  public NpmDependencyVersionService() {
    this.npmDependencyRepository = new NpmDependencyRepository(MongoDbConfig.getDatabase());
  }

  public String getNpmDependencyVersion(final String name) {
    NpmRegistryResponse npmRegistryResponse = getNpmDependencySearchResponse(name);

    if (npmRegistryResponse == null
        || npmRegistryResponse.getDistTags() == null
        || CommonUtilities.isEmpty(npmRegistryResponse.getDistTags().getLatest())) {
      return null;
    }
    return npmRegistryResponse.getDistTags().getLatest();
  }

  private NpmRegistryResponse getNpmDependencySearchResponse(final String name) {
    try {
      final String url = String.format(ConstantUtils.NPM_REGISTRY_ENDPOINT, name);
      return Connector.sendRequest(
              url,
              Enums.HttpMethod.GET,
              new TypeReference<NpmRegistryResponse>() {},
              null,
              null,
              null)
          .responseBody();
    } catch (Exception ex) {
      log.error("ERROR in Get NPM Dependency Search Response: [ {} ]", name, ex);
    }
    return null;
  }

  public Map<String, DependencyEntity> getNpmDependenciesMap() {
    Map<String, DependencyEntity> npmDependenciesMap = CacheConfig.getNpmDependenciesMap();
    if (CommonUtilities.isEmpty(npmDependenciesMap)) {
      final List<DependencyEntity> npmDependencies = npmDependencyRepository.findAll();
      log.info("NPM Dependencies List: [ {} ]", npmDependencies.size());
      npmDependenciesMap =
          npmDependencies.stream()
              .collect(Collectors.toMap(DependencyEntity::getName, npmDependency -> npmDependency));
      CacheConfig.setNpmDependenciesMap(npmDependenciesMap);
    }
    return npmDependenciesMap;
  }

  public void insertNpmDependency(final String name, final String version) {
    log.info("Insert NPM Dependency: [ {} ] | [ {} ]", name, version);
    CacheConfig.resetNpmDependenciesMap();
    final DependencyEntity dependencyEntity = new DependencyEntity(name, version);
    npmDependencyRepository.insert(dependencyEntity);
  }

  public void updateNpmDependency(final DependencyEntity dependencyEntity) {
    log.info("Update NPM Dependency: [ {} ]", dependencyEntity);
    CacheConfig.resetNpmDependenciesMap();
    npmDependencyRepository.update(dependencyEntity.getId(), dependencyEntity);
  }

  public void updateNpmDependencies() {
    final Map<String, DependencyEntity> npmDependenciesLocal = getNpmDependenciesMap();
    final List<DependencyEntity> npmDependencies = npmDependencyRepository.findAll();
    List<DependencyEntity> npmDependenciesToUpdate = new ArrayList<>();

    npmDependencies.forEach(
        npmDependency -> {
          String name = npmDependency.getName();
          String currentVersion = npmDependency.getVersion();
          String latestVersion = getNpmDependencyVersion(name);

          if (VersionUtils.isRequiresUpdate(currentVersion, latestVersion)) {
            npmDependenciesToUpdate.add(
                new DependencyEntity(
                    npmDependenciesLocal.get(npmDependency.getName()).getId(),
                    npmDependency.getName(),
                    latestVersion,
                    Boolean.FALSE));
          }
        });

    log.info(
        "NPM Dependencies to Update: [{}]\n[{}]",
        npmDependenciesToUpdate.size(),
        npmDependenciesToUpdate);

    if (!npmDependenciesToUpdate.isEmpty()) {
      for (DependencyEntity npmDependencyToUpdate : npmDependenciesToUpdate) {
        npmDependencyRepository.update(npmDependencyToUpdate.getId(), npmDependencyToUpdate);
      }
      log.info("NPM Dependencies Updated...");
      ProcessUtils.setMongoPackagesToUpdate(npmDependenciesToUpdate.size());
    }
  }
}
