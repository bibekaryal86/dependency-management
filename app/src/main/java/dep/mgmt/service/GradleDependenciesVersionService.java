package dep.mgmt.service;

import com.fasterxml.jackson.core.type.TypeReference;
import dep.mgmt.model.web.MavenSearchResponse;
import dep.mgmt.util.ConstantUtils;
import dep.mgmt.util.VersionUtils;
import io.github.bibekaryal86.shdsvc.Connector;
import io.github.bibekaryal86.shdsvc.dtos.Enums;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import java.util.Comparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GradleDependenciesVersionService {

  private static final Logger log = LoggerFactory.getLogger(GradleDependenciesVersionService.class);

  public String getLatestDependencyVersion(
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
}
