package dep.mgmt.service;

import com.fasterxml.jackson.core.type.TypeReference;
import dep.mgmt.config.CacheConfig;
import dep.mgmt.config.MongoDbConfig;
import dep.mgmt.model.entity.DependencyEntity;
import dep.mgmt.model.web.PythonPackageSearchResponse;
import dep.mgmt.repository.PythonPackageRepository;
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

public class PythonPackageVersionService {

  private static final Logger log = LoggerFactory.getLogger(PythonPackageVersionService.class);
  private final PythonPackageRepository pythonPackageRepository;

  public PythonPackageVersionService() {
    this.pythonPackageRepository = new PythonPackageRepository(MongoDbConfig.getDatabase());
  }

  public String getPythonPackageVersion(final String name) {
    PythonPackageSearchResponse pythonPackageSearchResponse = getPythonPackageSearchResponse(name);

    if (pythonPackageSearchResponse == null
        || pythonPackageSearchResponse.getInfo() == null
        || pythonPackageSearchResponse.getInfo().getYanked()) {
      return null;
    }

    return pythonPackageSearchResponse.getInfo().getVersion();
  }

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
      log.error("ERROR in Get Python Package Search Response: [ {} ]", name, ex);
    }
    return null;
  }

  public Map<String, DependencyEntity> getPythonPackagesMap() {
    log.debug("Get Python Packages Map...");
    Map<String, DependencyEntity> pythonPackagesMap = CacheConfig.getPythonPackagesMap();
    if (CommonUtilities.isEmpty(pythonPackagesMap)) {
      final List<DependencyEntity> pythonPackages = pythonPackageRepository.findAll();
      log.debug("Python Packages List: [ {} ]", pythonPackages.size());
      pythonPackagesMap =
          pythonPackages.stream()
              .collect(Collectors.toMap(DependencyEntity::getName, pythonPackage -> pythonPackage));
      CacheConfig.setPythonPackagesMap(pythonPackagesMap);
    }
    return pythonPackagesMap;
  }

  public void insertPythonPackage(final String name, final String version) {
    log.info("Insert Python Package: [ {} ] | [ {} ]", name, version);
    CacheConfig.resetPythonPackagesMap();
    final DependencyEntity dependencyEntity = new DependencyEntity(name, version);
    pythonPackageRepository.insert(dependencyEntity);
  }

  public void updatePythonPackage(final DependencyEntity dependencyEntity) {
    log.info("Update Python Package: [ {} ]", dependencyEntity);
    CacheConfig.resetPythonPackagesMap();
    pythonPackageRepository.update(dependencyEntity.getId(), dependencyEntity);
  }

  public void updatePythonPackages() {
    log.info("Update Python Packages...");
    final List<DependencyEntity> pythonPackages = pythonPackageRepository.findAll();
    List<DependencyEntity> pythonPackagesChecked = new ArrayList<>();
    List<DependencyEntity> pythonPackagesToUpdate = new ArrayList<>();

    pythonPackages.forEach(
        pythonPackage -> {
          String name = pythonPackage.getName();
          String currentVersion = pythonPackage.getVersion();
          String latestVersion = getPythonPackageVersion(name);

          if (VersionUtils.isRequiresUpdate(currentVersion, latestVersion)) {
            pythonPackagesToUpdate.add(
                new DependencyEntity(
                    pythonPackage.getId(), pythonPackage.getName(), latestVersion, Boolean.FALSE));
          } else {
            pythonPackagesChecked.add(
                new DependencyEntity(
                    pythonPackage.getId(),
                    pythonPackage.getName(),
                    latestVersion,
                    Boolean.FALSE,
                    pythonPackage.getLastUpdatedDate()));
          }
        });

    log.info("Python Packages to Update: [{}]", pythonPackagesToUpdate.size());
    log.info("Python Packages Checked: [{}]", pythonPackagesChecked.size());
    log.debug("pythonPackagesToUpdate\n{}", pythonPackagesToUpdate);
    log.debug("pythonPackagesChecked\n{}", pythonPackagesChecked);

    if (!pythonPackagesToUpdate.isEmpty()) {
      for (DependencyEntity pythonPackageToUpdate : pythonPackagesToUpdate) {
        pythonPackageRepository.update(pythonPackageToUpdate.getId(), pythonPackageToUpdate);
      }
      ProcessUtils.setMongoPythonPackagesToUpdate(pythonPackagesToUpdate.size());
    }

    if (!pythonPackagesChecked.isEmpty()) {
      for (DependencyEntity pythonPackageChecked : pythonPackagesChecked) {
        pythonPackageRepository.update(pythonPackageChecked.getId(), pythonPackageChecked);
      }
      ProcessUtils.setMongoPythonPackagesChecked(pythonPackagesChecked.size());
    }
  }

  public void updatePythonPackage(final String library) {
    log.info("Update Python Package: [{}]", library);
    final DependencyEntity pythonPackage = pythonPackageRepository.findByAttribute("name", library);

    final String currentVersion = pythonPackage.getVersion();
    final String latestVersion = getPythonPackageVersion(library);

    if (VersionUtils.isRequiresUpdate(currentVersion, latestVersion)) {
      final DependencyEntity pythonPackageToUpdate =
          new DependencyEntity(pythonPackage.getId(), library, latestVersion, Boolean.FALSE);
      pythonPackageRepository.update(pythonPackageToUpdate.getId(), pythonPackageToUpdate);
    } else {
      final DependencyEntity pythonPackageToUpdate =
          new DependencyEntity(
              pythonPackage.getId(),
              library,
              latestVersion,
              Boolean.FALSE,
              pythonPackage.getLastUpdatedDate());
      pythonPackageRepository.update(pythonPackageToUpdate.getId(), pythonPackageToUpdate);
    }
  }
}
