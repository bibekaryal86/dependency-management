package dep.mgmt.service;

import dep.mgmt.config.CacheConfig;
import dep.mgmt.config.MongoDbConfig;
import dep.mgmt.model.entity.DependencyEntity;
import dep.mgmt.repository.GradlePluginRepository;
import dep.mgmt.util.ConstantUtils;
import dep.mgmt.util.VersionUtils;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
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
    List<DependencyEntity> plugins = gradlePluginRepository.findAll();
    log.info("Gradle Plugins Map: [ {} ]", plugins.size());

    Map<String, DependencyEntity> gradlePluginsMap =
        plugins.stream().collect(Collectors.toMap(DependencyEntity::getName, plugin -> plugin));
    CacheConfig.setGradlePluginsMap(gradlePluginsMap);
    return gradlePluginsMap;
  }

  public void saveGradlePlugin(final DependencyEntity dependencyEntity) {
    log.info("Save Gradle Plugin: [ {} ]", dependencyEntity);
    CacheConfig.resetGradlePluginsMap();
    gradlePluginRepository.insert(dependencyEntity);
    CompletableFuture.runAsync(this::getGradlePluginsMap);
  }

  public void saveGradlePlugin(final String name, final String version) {
    log.info("Save Gradle Plugin: [ {} ] | [ {} ]", name, version);
    CacheConfig.resetGradleDependenciesMap();
    final DependencyEntity dependencyEntity = new DependencyEntity(name, version);
    gradlePluginRepository.insert(dependencyEntity);
    CompletableFuture.runAsync(this::getGradlePluginsMap);
  }
}
