package dep.mgmt.service;

import com.fasterxml.jackson.core.type.TypeReference;
import dep.mgmt.config.CacheConfig;
import dep.mgmt.config.MongoDbConfig;
import dep.mgmt.model.entity.DependencyEntity;
import dep.mgmt.model.web.PythonPackageSearchResponse;
import dep.mgmt.repository.NpmDependencyRepository;
import dep.mgmt.util.ConstantUtils;
import io.github.bibekaryal86.shdsvc.Connector;
import io.github.bibekaryal86.shdsvc.dtos.Enums;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NpmDependencyVersionService {

  private static final Logger log = LoggerFactory.getLogger(NpmDependencyVersionService.class);
  private final NpmDependencyRepository npmDependencyRepository;

  public NpmDependencyVersionService() {
    this.npmDependencyRepository = new NpmDependencyRepository(MongoDbConfig.getDatabase());
  }

  // TODO
  public String getPythonPackageVersion(final String name) {
    return null;
  }

  // TODO
  private PythonPackageSearchResponse getPythonPackageSearchResponse(final String name) {
    try {
      final String url = String.format(ConstantUtils.PYPI_SEARCH_ENDPOINT, name);
      return Connector.sendRequest(
              url,
              Enums.HttpMethod.GET,
              new TypeReference<PythonPackageSearchResponse>() {},
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
      npmDependenciesMap = npmDependencies.stream().collect(Collectors.toMap(DependencyEntity::getName, npmDependency -> npmDependency));
      CacheConfig.setNpmDependenciesMap(npmDependenciesMap);
    }
    return npmDependenciesMap;
  }

  public void saveNpmDependency(final DependencyEntity dependencyEntity) {
    log.info("Save NPM Dependency: [ {} ]", dependencyEntity);
    CacheConfig.resetPythonPackagesMap();
    npmDependencyRepository.insert(dependencyEntity);
  }

  public void saveNpmDependency(final String name, final String version) {
    log.info("Save NPM Dependency: [ {} ] | [ {} ]", name, version);
    CacheConfig.resetGradleDependenciesMap();
    final DependencyEntity dependencyEntity = new DependencyEntity(name, version);
    npmDependencyRepository.insert(dependencyEntity);
  }
}
