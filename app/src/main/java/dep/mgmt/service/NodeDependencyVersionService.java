package dep.mgmt.service;

import com.fasterxml.jackson.core.type.TypeReference;
import dep.mgmt.config.CacheConfig;
import dep.mgmt.config.MongoDbConfig;
import dep.mgmt.model.entity.DependencyEntity;
import dep.mgmt.model.web.NpmRegistryResponse;
import dep.mgmt.repository.NodeDependencyRepository;
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

public class NodeDependencyVersionService {

  private static final Logger log = LoggerFactory.getLogger(NodeDependencyVersionService.class);
  private final NodeDependencyRepository nodeDependencyRepository;

  public NodeDependencyVersionService() {
    this.nodeDependencyRepository = new NodeDependencyRepository(MongoDbConfig.getDatabase());
  }

  public String getNodeDependencyVersion(final String name) {
    NpmRegistryResponse npmRegistryResponse = getNpmRegistrySearchResponse(name);

    if (npmRegistryResponse == null
        || npmRegistryResponse.getDistTags() == null
        || CommonUtilities.isEmpty(npmRegistryResponse.getDistTags().getLatest())) {
      return null;
    }
    return npmRegistryResponse.getDistTags().getLatest();
  }

  private NpmRegistryResponse getNpmRegistrySearchResponse(final String name) {
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
      log.error("ERROR in Get NPM Registry Search Response: [ {} ]", name, ex);
    }
    return null;
  }

  public Map<String, DependencyEntity> getNodeDependenciesMap() {
    Map<String, DependencyEntity> nodeDependenciesMap = CacheConfig.getNodeDependenciesMap();
    if (CommonUtilities.isEmpty(nodeDependenciesMap)) {
      final List<DependencyEntity> nodeDependencies = nodeDependencyRepository.findAll();
      log.info("Node Dependencies List: [ {} ]", nodeDependencies.size());
      nodeDependenciesMap =
          nodeDependencies.stream()
              .collect(Collectors.toMap(DependencyEntity::getName, nodeDependency -> nodeDependency));
      CacheConfig.setNodeDependenciesMap(nodeDependenciesMap);
    }
    return nodeDependenciesMap;
  }

  public void insertNodeDependency(final String name, final String version) {
    log.info("Insert Node Dependency: [ {} ] | [ {} ]", name, version);
    CacheConfig.resetNodeDependenciesMap();
    final DependencyEntity dependencyEntity = new DependencyEntity(name, version);
    nodeDependencyRepository.insert(dependencyEntity);
  }

  public void updateNodeDependency(final DependencyEntity dependencyEntity) {
    log.info("Update Node Dependency: [ {} ]", dependencyEntity);
    CacheConfig.resetNodeDependenciesMap();
    nodeDependencyRepository.update(dependencyEntity.getId(), dependencyEntity);
  }

  public void updateNodeDependencies() {
    final Map<String, DependencyEntity> nodeDependenciesLocal = getNodeDependenciesMap();
    final List<DependencyEntity> nodeDependencies = nodeDependencyRepository.findAll();
    List<DependencyEntity> nodeDependenciesToUpdate = new ArrayList<>();

    nodeDependencies.forEach(
        nodeDependency -> {
          String name = nodeDependency.getName();
          String currentVersion = nodeDependency.getVersion();
          String latestVersion = getNodeDependencyVersion(name);

          if (VersionUtils.isRequiresUpdate(currentVersion, latestVersion)) {
            nodeDependenciesToUpdate.add(
                new DependencyEntity(
                    nodeDependenciesLocal.get(nodeDependency.getName()).getId(),
                    nodeDependency.getName(),
                    latestVersion,
                    Boolean.FALSE));
          }
        });

    log.info(
        "Node Dependencies to Update: [{}]\n[{}]",
        nodeDependenciesToUpdate.size(),
        nodeDependenciesToUpdate);

    if (!nodeDependenciesToUpdate.isEmpty()) {
      for (DependencyEntity nodeDependencyToUpdate : nodeDependenciesToUpdate) {
        nodeDependencyRepository.update(nodeDependencyToUpdate.getId(), nodeDependencyToUpdate);
      }
      log.info("Node Dependencies Updated...");
      ProcessUtils.setMongoPackagesToUpdate(nodeDependenciesToUpdate.size());
    }
  }
}
