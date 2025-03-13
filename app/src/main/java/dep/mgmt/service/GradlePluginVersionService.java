package dep.mgmt.service;

import dep.mgmt.config.CacheConfig;
import dep.mgmt.config.MongoDbConfig;
import dep.mgmt.model.entity.DependencyEntity;
import dep.mgmt.repository.GradlePluginRepository;
import dep.mgmt.util.ConstantUtils;
import dep.mgmt.util.ProcessUtils;
import dep.mgmt.util.VersionUtils;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GradlePluginVersionService {

  private static final Logger log = LoggerFactory.getLogger(GradlePluginVersionService.class);
  private final GradlePluginRepository gradlePluginRepository;

  public GradlePluginVersionService() {
    this.gradlePluginRepository = new GradlePluginRepository(MongoDbConfig.getDatabase());
  }

  public String getGradlePluginVersion(final String group) {
    log.debug("Get Latest Gradle Plugin: [ {} ]", group);
    Document document = getGradlePlugins(group);
    log.debug("Gradle Plugin Document: [ {} ] | [ {} ]", group, document);
    if (document != null) {
      Element versionElement = document.getElementsByClass("version-info").first();

      if (versionElement != null) {
        Element latestVersionElement = versionElement.selectFirst("h3");

        if (latestVersionElement != null) {
          String latestVersionText = latestVersionElement.text();
          return getGradlePluginVersionLatest(latestVersionText);
        } else {
          log.error("ERROR Latest Version Element is NULL: [ {} ]", group);
        }
      } else {
        log.error("ERROR Version Element is NULL: [ {} ]", group);
      }
    }
    return null;
  }

  private Document getGradlePlugins(final String group) {
    try {
      String url = String.format(ConstantUtils.GRADLE_PLUGINS_ENDPOINT, group);
      return Jsoup.connect(url).get();
    } catch (IOException ex) {
      log.error("ERROR Get Gradle Plugins: [ {} ]", group, ex);
    }
    return null;
  }

  private String getGradlePluginVersionLatest(final String latestVersionText) {
    String[] latestVersionTextArray = latestVersionText.split(" ");
    if (latestVersionTextArray.length == 3) {
      String version = latestVersionTextArray[1];
      if (VersionUtils.isCheckPreReleaseVersion(version)) {
        return version;
      }
    } else {
      log.error("ERROR Get Latest Gradle Plugin Version Wrong Length: [ {} ]", latestVersionText);
    }
    return null;
  }

  public Map<String, DependencyEntity> getGradlePluginsMap() {
    Map<String, DependencyEntity> gradlePluginsMap = CacheConfig.getGradlePluginsMap();
    if (CommonUtilities.isEmpty(gradlePluginsMap)) {
      final List<DependencyEntity> gradlePlugins = gradlePluginRepository.findAll();
      log.info("Gradle Plugins List: [ {} ]", gradlePlugins.size());
      gradlePluginsMap =
          gradlePlugins.stream()
              .collect(Collectors.toMap(DependencyEntity::getName, gradlePlugin -> gradlePlugin));
      CacheConfig.setGradlePluginsMap(gradlePluginsMap);
    }
    return gradlePluginsMap;
  }

  public void saveGradlePlugin(final DependencyEntity dependencyEntity) {
    log.info("Save Gradle Plugin: [ {} ]", dependencyEntity);
    CacheConfig.resetGradlePluginsMap();
    gradlePluginRepository.insert(dependencyEntity);
  }

  public void saveGradlePlugin(final String name, final String version) {
    log.info("Save Gradle Plugin: [ {} ] | [ {} ]", name, version);
    CacheConfig.resetGradleDependenciesMap();
    final DependencyEntity dependencyEntity = new DependencyEntity(name, version);
    gradlePluginRepository.insert(dependencyEntity);
  }

  public void updateGradlePlugins(final Map<String, DependencyEntity> gradlePluginsLocal) {
    final List<DependencyEntity> gradlePlugins = gradlePluginRepository.findAll();
    List<DependencyEntity> gradlePluginsToUpdate = new ArrayList<>();

    gradlePlugins.forEach(
        gradlePlugin -> {
          String group = gradlePlugin.getName();
          String currentVersion = gradlePlugin.getVersion();
          String latestVersion = getGradlePluginVersion(group);

          if (VersionUtils.isRequiresUpdate(currentVersion, latestVersion)) {
            gradlePluginsToUpdate.add(
                new DependencyEntity(
                    gradlePluginsLocal.get(gradlePlugin.getName()).getId(),
                    gradlePlugin.getName(),
                    latestVersion,
                    Boolean.FALSE));
          }
        });

    log.info(
        "Gradle Plugins to Update: [{}]\n[{}]",
        gradlePluginsToUpdate.size(),
        gradlePluginsToUpdate);

    if (!gradlePluginsToUpdate.isEmpty()) {
      for (DependencyEntity gradlePluginToUpdate : gradlePluginsToUpdate) {
        gradlePluginRepository.update(gradlePluginToUpdate.getId(), gradlePluginToUpdate);
      }
      log.info("Gradle Plugins Updated...");
      ProcessUtils.setMongoPluginsToUpdate(gradlePluginsToUpdate.size());
    }
  }
}
