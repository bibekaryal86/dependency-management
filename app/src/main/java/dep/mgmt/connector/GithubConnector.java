package dep.mgmt.connector;

import com.fasterxml.jackson.core.type.TypeReference;
import dep.mgmt.model.web.GithubPullRequestApi;
import dep.mgmt.util.ConstantUtils;
import io.github.bibekaryal86.shdsvc.Connector;
import io.github.bibekaryal86.shdsvc.dtos.Enums;
import io.github.bibekaryal86.shdsvc.dtos.HttpResponse;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GithubConnector {

  private static final Logger log = LoggerFactory.getLogger(GithubConnector.class);

  public GithubPullRequestApi.CreateResponse createPullRequest(
      final String repoName, final String branchName) {
    log.info("Create Pull Request: [{}] | [{}]", repoName, branchName);

    final String repoOwner = CommonUtilities.getSystemEnvProperty(ConstantUtils.ENV_GITHUB_OWNER);
    final String url = String.format(ConstantUtils.GITHUB_CREATE_PR_ENDPOINT, repoOwner, repoName);
    final Map<String, String> headers =
        Map.of(
            "Accept",
            "application/vnd.github+json",
            "Authorization",
            String.format(
                "Bearer %s", CommonUtilities.getSystemEnvProperty(ConstantUtils.ENV_GITHUB_TOKEN)));
    final GithubPullRequestApi.CreateRequest requestBody =
        new GithubPullRequestApi.CreateRequest(
            ConstantUtils.GITHUB_PR_TITLE_BODY,
            ConstantUtils.GITHUB_PR_TITLE_BODY,
            branchName,
            ConstantUtils.GITHUB_PR_BASE_BRANCH);

    HttpResponse<GithubPullRequestApi.CreateResponse> response =
        Connector.sendRequest(
            url,
            Enums.HttpMethod.POST,
            new TypeReference<GithubPullRequestApi.CreateResponse>() {},
            null,
            headers,
            requestBody);
    if (response.statusCode() == 201) {
      return response.responseBody();
    }

    return null;
  }

  public GithubPullRequestApi.MergeResponse mergePullRequest(
      final String repoName, final Integer pullNumber) {
    log.info("Merge Pull Request: [{}] | [{}]", repoName, pullNumber);

    final String repoOwner = CommonUtilities.getSystemEnvProperty(ConstantUtils.ENV_GITHUB_OWNER);
    final String url =
        String.format(ConstantUtils.GITHUB_MERGE_PR_ENDPOINT, repoOwner, repoName, pullNumber);
    final Map<String, String> headers =
        Map.of(
            "Accept",
            "application/vnd.github+json",
            "Authorization",
            String.format(
                "Bearer %s", CommonUtilities.getSystemEnvProperty(ConstantUtils.ENV_GITHUB_TOKEN)));
    final GithubPullRequestApi.MergeRequest requestBody =
        new GithubPullRequestApi.MergeRequest(ConstantUtils.GITHUB_PR_MERGE_METHOD);

    HttpResponse<GithubPullRequestApi.MergeResponse> response =
        Connector.sendRequest(
            url,
            Enums.HttpMethod.PUT,
            new TypeReference<GithubPullRequestApi.MergeResponse>() {},
            null,
            headers,
            requestBody);
    if (response.statusCode() == 200) {
      return response.responseBody();
    }

    return null;
  }
}
