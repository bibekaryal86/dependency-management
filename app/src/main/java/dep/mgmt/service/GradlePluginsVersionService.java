package dep.mgmt.service;

import dep.mgmt.util.ConstantUtils;
import dep.mgmt.util.VersionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GradlePluginsVersionService {

    private static final Logger log = LoggerFactory.getLogger(GradlePluginsVersionService.class);

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
            if (!VersionUtils.isCheckPreReleaseVersion(version)) {
                return version;
            }
        } else {
            log.error("ERROR Get Latest Gradle Plugin Version Wrong Length: [ {} ]", latestVersionText);
        }
        return null;
    }
}
