package dep.mgmt.connector;

import com.fasterxml.jackson.core.type.TypeReference;
import dep.mgmt.model.web.GithubApiModel;
import dep.mgmt.util.ConstantUtils;
import io.github.bibekaryal86.shdsvc.Connector;
import io.github.bibekaryal86.shdsvc.dtos.Enums;
import io.github.bibekaryal86.shdsvc.dtos.HttpResponse;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GithubConnector {

  private static final Logger log = LoggerFactory.getLogger(GithubConnector.class);

  public List<GithubApiModel.ListBranchesResponse> listBranches(final String repoName) {
    final String repoOwner = CommonUtilities.getSystemEnvProperty(ConstantUtils.ENV_GITHUB_OWNER);
    final String url =
        String.format(ConstantUtils.GITHUB_LIST_BRANCHES_ENDPOINT, repoOwner, repoName);
    final Map<String, String> headers = getDefaultHeaders();
    HttpResponse<List<GithubApiModel.ListBranchesResponse>> response =
        Connector.sendRequest(
            url,
            Enums.HttpMethod.GET,
            new TypeReference<List<GithubApiModel.ListBranchesResponse>>() {},
            null,
            headers,
            null);
    if (response.statusCode() == 200) {
      return response.responseBody();
    } else {
      log.error(
          "List Branches Error: [{}] | [{}] | [{}]",
          repoName,
          response.statusCode(),
          response.responseBody());
    }

    return Collections.emptyList();
  }

  public GithubApiModel.CreatePullRequestResponse createPullRequest(
      final String repoName, final String branchName) {
    final String repoOwner = CommonUtilities.getSystemEnvProperty(ConstantUtils.ENV_GITHUB_OWNER);
    final String url = String.format(ConstantUtils.GITHUB_CREATE_PR_ENDPOINT, repoOwner, repoName);
    final Map<String, String> headers = getDefaultHeaders();
    final GithubApiModel.CreatePullRequest requestBody =
        new GithubApiModel.CreatePullRequest(
            ConstantUtils.GITHUB_PR_TITLE_BODY,
            ConstantUtils.GITHUB_PR_TITLE_BODY,
            branchName,
            ConstantUtils.GITHUB_PR_BASE_BRANCH);

    HttpResponse<GithubApiModel.CreatePullRequestResponse> response =
        Connector.sendRequest(
            url,
            Enums.HttpMethod.POST,
            new TypeReference<GithubApiModel.CreatePullRequestResponse>() {},
            null,
            headers,
            requestBody);
    if (response.statusCode() == 201) {
      return response.responseBody();
    } else {
      log.error(
          "Create Pull Request Error: [{}] | [{}] | [{}] | [{}]",
          repoName,
          branchName,
          response.statusCode(),
          response.responseBody());
    }

    return null;
  }

  public List<GithubApiModel.ListPullRequestsResponse> listPullRequests(final String repoName) {
    final String repoOwner = CommonUtilities.getSystemEnvProperty(ConstantUtils.ENV_GITHUB_OWNER);
    final String url = String.format(ConstantUtils.GITHUB_LIST_PRS_ENDPOINT, repoOwner, repoName);
    final Map<String, String> headers = getDefaultHeaders();
    HttpResponse<List<GithubApiModel.ListPullRequestsResponse>> response =
        Connector.sendRequest(
            url,
            Enums.HttpMethod.GET,
            new TypeReference<List<GithubApiModel.ListPullRequestsResponse>>() {},
            null,
            headers,
            null);
    if (response.statusCode() == 200) {
      return response.responseBody();
    } else {
      log.error(
          "List Pull Requests Error: [{}] | [{}] [{}]",
          repoName,
          response.statusCode(),
          response.responseBody());
    }

    return Collections.emptyList();
  }

  public GithubApiModel.MergePullRequestResponse mergePullRequest(
      final String repoName, final Integer pullNumber) {
    final String repoOwner = CommonUtilities.getSystemEnvProperty(ConstantUtils.ENV_GITHUB_OWNER);
    final String url =
        String.format(ConstantUtils.GITHUB_MERGE_PR_ENDPOINT, repoOwner, repoName, pullNumber);
    final Map<String, String> headers = getDefaultHeaders();
    final GithubApiModel.MergePullRequestRequest requestBody =
        new GithubApiModel.MergePullRequestRequest(
            ConstantUtils.GITHUB_PR_TITLE_BODY,
            ConstantUtils.GITHUB_PR_TITLE_BODY,
            ConstantUtils.GITHUB_PR_MERGE_METHOD);

    HttpResponse<GithubApiModel.MergePullRequestResponse> response =
        Connector.sendRequest(
            url,
            Enums.HttpMethod.PUT,
            new TypeReference<GithubApiModel.MergePullRequestResponse>() {},
            null,
            headers,
            requestBody);
    if (response.statusCode() == 200) {
      return response.responseBody();
    } else {
      log.error(
          "Merge Pull Request Error: RepoName=[{}] | PullNumber=[{}] | StatusCode=[{}] | ResponseBody=[{}]",
          repoName,
          pullNumber,
          response.statusCode(),
          response.responseBody());
    }

    return null;
  }

  public GithubApiModel.ListWorkflowRunsResponse listWorkflowRuns(
      final String repoName, final String queryParams) {
    final String repoOwner = CommonUtilities.getSystemEnvProperty(ConstantUtils.ENV_GITHUB_OWNER);
    String url = String.format(ConstantUtils.GITHUB_LIST_CHECKS_ENDPOINT, repoOwner, repoName);
    if (!CommonUtilities.isEmpty(queryParams)) {
      url = url + queryParams;
    }
    final Map<String, String> headers = getDefaultHeaders();
    HttpResponse<GithubApiModel.ListWorkflowRunsResponse> response =
        Connector.sendRequest(
            url,
            Enums.HttpMethod.GET,
            new TypeReference<GithubApiModel.ListWorkflowRunsResponse>() {},
            null,
            headers,
            null);
    if (response.statusCode() == 200) {
      return response.responseBody();
    } else {
      log.error(
          "List Workflow Runs Error: RepoName=[{}] | StatusCode=[{}] ResponseBody=[{}]",
          repoName,
          response.statusCode(),
          response.responseBody());
    }

    return null;
  }

  public GithubApiModel.RateLimitResponse getRateLimits() {
    log.info("Get GitHub API Rate Limits...");
    final String url = ConstantUtils.GITHUB_RATE_LIMIT_ENDPOINT;
    final Map<String, String> headers = getDefaultHeaders();
    HttpResponse<GithubApiModel.RateLimitResponse> response =
        Connector.sendRequest(
            url,
            Enums.HttpMethod.GET,
            new TypeReference<GithubApiModel.RateLimitResponse>() {},
            null,
            headers,
            null);
    if (response.statusCode() == 200) {
      log.info("{}", response.responseBody());
      return response.responseBody();
    } else {
      log.error(
          "Get GitHub API Rate Limits Error: [{}] | [{}]",
          response.statusCode(),
          response.responseBody());
    }
    return null;
  }

  private Map<String, String> getDefaultHeaders() {
    return Map.of(
        "Accept",
        "application/vnd.github+json",
        "Authorization",
        String.format(
            "Bearer %s", CommonUtilities.getSystemEnvProperty(ConstantUtils.ENV_GITHUB_TOKEN)));
  }
}
