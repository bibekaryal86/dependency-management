package dep.mgmt.service;

import com.fasterxml.jackson.core.type.TypeReference;
import dep.mgmt.config.CacheConfig;
import dep.mgmt.config.MongoDbConfig;
import dep.mgmt.model.entity.DependencyEntity;
import dep.mgmt.model.web.MavenSearchResponse;
import dep.mgmt.repository.GradleDependencyRepository;
import dep.mgmt.util.ConstantUtils;
import dep.mgmt.util.ProcessUtils;
import dep.mgmt.util.VersionUtils;
import io.github.bibekaryal86.shdsvc.Connector;
import io.github.bibekaryal86.shdsvc.dtos.Enums;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GradleDependencyVersionService {

  private static final Logger log = LoggerFactory.getLogger(GradleDependencyVersionService.class);
  private final GradleDependencyRepository gradleDependencyRepository;

  public GradleDependencyVersionService() {
    this.gradleDependencyRepository = new GradleDependencyRepository(MongoDbConfig.getDatabase());
  }

  public String getGradleDependencyVersion(
      final String group, final String artifact, final String currentVersion) {
    MavenSearchResponse mavenSearchResponse = getMavenSearchResponse(group, artifact);
    MavenSearchResponse.MavenResponse.MavenDoc mavenDoc =
        getLatestDependencyVersion(mavenSearchResponse);
    log.debug(
        "Maven Search Response: [ {} ], [ {} ], [ {} ], [ {} ]",
        group,
        artifact,
        mavenDoc,
        mavenSearchResponse);

    if (mavenDoc == null) {
      return currentVersion;
    }

    return mavenDoc.getV();
  }

  private MavenSearchResponse getMavenSearchResponse(final String group, final String artifact) {
    try {
      final String url = String.format(ConstantUtils.MAVEN_SEARCH_ENDPOINT, group, artifact);
      return Connector.sendRequest(
              url,
              Enums.HttpMethod.GET,
              new TypeReference<MavenSearchResponse>() {},
              null,
              null,
              null)
          .responseBody();
    } catch (Exception ex) {
      log.error("ERROR in Get Maven Search Response: [ {} ] [ {} ]", group, artifact, ex);
    }
    return null;
  }

  private MavenSearchResponse.MavenResponse.MavenDoc getLatestDependencyVersion(
      final MavenSearchResponse mavenSearchResponse) {
    // the search returns 5 latest, filter to not get RC or alpha/beta or unfinished releases
    // the search returns sorted list already, but need to filter and get max after
    if (mavenSearchResponse != null
        && mavenSearchResponse.getResponse() != null
        && !CommonUtilities.isEmpty(mavenSearchResponse.getResponse().getDocs())) {
      MavenSearchResponse.MavenResponse mavenResponse = mavenSearchResponse.getResponse();
      return mavenResponse.getDocs().stream()
          .filter(mavenDoc -> VersionUtils.isCheckPreReleaseVersion(mavenDoc.getV()))
          .max(
              Comparator.comparing(
                  MavenSearchResponse.MavenResponse.MavenDoc::getV,
                  Comparator.comparing(VersionUtils::getVersionToCompare)))
          .orElse(null);
    }
    return null;
  }

  public Map<String, DependencyEntity> getGradleDependenciesMap() {
    Map<String, DependencyEntity> gradleDependenciesMap = CacheConfig.getGradleDependenciesMap();
    if (CommonUtilities.isEmpty(gradleDependenciesMap)) {
      final List<DependencyEntity> gradleDependencies = gradleDependencyRepository.findAll();
      log.info("Gradle Dependencies List: [ {} ]", gradleDependencies.size());
      gradleDependenciesMap =
          gradleDependencies.stream()
              .collect(
                  Collectors.toMap(
                      DependencyEntity::getName, gradleDependency -> gradleDependency));
      CacheConfig.setGradleDependenciesMap(gradleDependenciesMap);
    }
    return gradleDependenciesMap;
  }

  public void insertGradleDependency(final String name, final String version) {
    log.info("Insert Gradle Dependency: [ {} ] | [ {} ]", name, version);
    CacheConfig.resetGradleDependenciesMap();
    final DependencyEntity dependencyEntity = new DependencyEntity(name, version);
    gradleDependencyRepository.insert(dependencyEntity);
  }

  public void updateGradleDependency(final DependencyEntity dependencyEntity) {
    log.info("Update Gradle Dependency: [ {} ]", dependencyEntity);
    CacheConfig.resetGradleDependenciesMap();
    gradleDependencyRepository.update(dependencyEntity.getId(), dependencyEntity);
  }

  public void updateGradleDependencies() {
    final Map<String, DependencyEntity> gradleDependenciesLocal = getGradleDependenciesMap();
    final List<DependencyEntity> gradleDependencies = gradleDependencyRepository.findAll();
    List<DependencyEntity> gradleDependenciesToUpdate = new ArrayList<>();

    gradleDependencies.forEach(
        gradleDependency -> {
          String[] mavenIdArray = gradleDependency.getName().split(":");
          String currentVersion = gradleDependency.getVersion();
          String latestVersion =
              getGradleDependencyVersion(mavenIdArray[0], mavenIdArray[1], currentVersion);

          if (VersionUtils.isRequiresUpdate(currentVersion, latestVersion)) {
            gradleDependenciesToUpdate.add(
                new DependencyEntity(
                    gradleDependenciesLocal.get(gradleDependency.getName()).getId(),
                    gradleDependency.getName(),
                    latestVersion,
                    Boolean.FALSE));
          }
        });

    log.info(
        "Gradle Dependencies to Update: [{}]\n[{}]",
        gradleDependenciesToUpdate.size(),
        gradleDependenciesToUpdate);

    if (!gradleDependenciesToUpdate.isEmpty()) {
      for (DependencyEntity gradleDependencyToUpdate : gradleDependenciesToUpdate) {
        gradleDependencyRepository.update(
            gradleDependencyToUpdate.getId(), gradleDependencyToUpdate);
      }
      log.info("Gradle Dependencies Updated...");
      ProcessUtils.setMongoDependenciesToUpdate(gradleDependenciesToUpdate.size());
    }
  }
}
