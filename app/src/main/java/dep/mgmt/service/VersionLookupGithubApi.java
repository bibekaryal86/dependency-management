package dep.mgmt.service;

import com.fasterxml.jackson.core.type.TypeReference;
import dep.mgmt.model.LatestVersion;
import dep.mgmt.model.web.ApiReleaseResponse;
import dep.mgmt.util.ConstantUtils;
import dep.mgmt.util.VersionUtils;
import io.github.bibekaryal86.shdsvc.Connector;
import io.github.bibekaryal86.shdsvc.dtos.Enums;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class VersionLookupGithubApi {
  private static final Logger log = LoggerFactory.getLogger(VersionLookupGithubApi.class);

  protected LatestVersion getGithubApiLatestVersion(final String owner, final String repo) {
    ApiReleaseResponse apiReleaseResponse =
        getGithubApiReleaseResponse(owner, repo, Boolean.FALSE, Boolean.TRUE);
    if (apiReleaseResponse == null) {
      return null;
    }
    final String versionActual = apiReleaseResponse.getTagName();
    final String versionFull = getVersionFull(versionActual);
    final String versionMajor = getVersionMajor(versionFull);
    return new LatestVersion(versionActual, versionFull, versionMajor, null, null);
  }

  protected ApiReleaseResponse getGithubApiReleaseResponse(
      final String owner, final String repo, final boolean isTags, final boolean isCheckMain) {
    ApiReleaseResponse apiReleaseResponse = null;
    try {
      final String url =
          String.format(
              ConstantUtils.GITHUB_RELEASES_ENDPOINT,
              owner,
              repo,
              isTags ? ConstantUtils.GITHUB_ENDPOINT_TAGS : ConstantUtils.GITHUB_ENDPOINT_RELEASES);
      final List<ApiReleaseResponse> apiReleaseResponses =
          Connector.sendRequest(
                  url,
                  Enums.HttpMethod.GET,
                  new TypeReference<List<ApiReleaseResponse>>() {},
                  null,
                  null,
                  null)
              .responseBody();
      apiReleaseResponse =
          isTags
              ? apiReleaseResponses.stream()
                  .filter(arr -> VersionUtils.isCheckPreReleaseVersion(arr.getName()))
                  .findFirst()
                  .orElse(null)
              : apiReleaseResponses.stream()
                  .filter(
                      arr ->
                          isCheckMain
                              ? ("main".equals(arr.getTargetCommitish())
                                  && !(arr.getPrerelease() || arr.getDraft()))
                              : !(arr.getPrerelease() || arr.getDraft()))
                  .findFirst()
                  .orElse(null);
      if (apiReleaseResponse == null) {
        log.error("GitHub Api Response NULL Error: [{}/{}/isTags={}]", owner, repo, isTags);
      }
    } catch (Exception ex) {
      log.error("ERROR Get Flyway Releases", ex);
    }
    return apiReleaseResponse;
  }

  /**
   * @param versionActual eg: v4.2.0 or codeql-bundle-v2.19.1
   * @return eg: 4.2.0 or 2.19.1
   */
  private String getVersionFull(final String versionActual) {
    return versionActual.replaceAll("[^0-9.]", "");
  }

  /**
   * @param versionFull eg: 4.2.0 or 2.19.1
   * @return eg: 4 or 2
   */
  private String getVersionMajor(final String versionFull) {
    return versionFull.trim().split("\\.")[0];
  }
}
