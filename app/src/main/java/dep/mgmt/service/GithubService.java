package dep.mgmt.service;

import dep.mgmt.connector.GithubConnector;
import dep.mgmt.model.ProcessSummaries;
import dep.mgmt.model.web.GithubApiModel;
import dep.mgmt.util.ConstantUtils;
import dep.mgmt.util.ProcessUtils;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import java.time.LocalDate;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GithubService {
  private static final Logger log = LoggerFactory.getLogger(GithubService.class);

  private final GithubConnector githubConnector;

  public GithubService() {
    this.githubConnector = new GithubConnector();
  }

  public void createGithubPullRequest(
      final String repoName,
      final LocalDate branchDate,
      final boolean isCheckUpdateBranchBeforeCreate) {
    log.info(
        "Github Pull Request Create: [{}] | [{}] | [{}]",
        repoName,
        branchDate,
        isCheckUpdateBranchBeforeCreate);

    if (isCheckUpdateBranchBeforeCreate) {
      ProcessSummaries.ProcessSummary.ProcessRepository processRepository =
          ProcessUtils.getProcessedRepositoryFromMap(repoName);

      if (!processRepository.getUpdateBranchCreated()) {
        log.debug("Github Pull Request NOT Created: [{}] | [{}]", repoName, branchDate);
        return;
      }
    }

    final String branchName = String.format(ConstantUtils.BRANCH_UPDATE_DEPENDENCIES, branchDate);
    final GithubApiModel.CreatePullRequestResponse createPullRequestResponse =
        githubConnector.createPullRequest(repoName, branchName);

    if (createPullRequestResponse != null
        && createPullRequestResponse.getNumber() != null
        && createPullRequestResponse.getNumber() > 0) {
      ProcessUtils.updateProcessedRepositoriesPrCreated(
          repoName, createPullRequestResponse.getNumber());
    }
  }

  public void mergeGithubPullRequest(
      final String repoName,
      final LocalDate branchDate,
      final Integer prNumber,
      final boolean isCheckPrCreatedBeforeMerge) {
    log.info(
        "Github Pull Request Merge: [{}] | [{}] | [{}] | [{}]",
        repoName,
        branchDate,
        prNumber,
        isCheckPrCreatedBeforeMerge);

    if (isCheckPrCreatedBeforeMerge) {
      ProcessSummaries.ProcessSummary.ProcessRepository processRepository =
          ProcessUtils.getProcessedRepositoryFromMap(repoName);

      if (!processRepository.getUpdateBranchCreated()) {
        log.debug(
            "Github Pull Request NOT Merged, No Update Branch: [{}] | [{}] | [{}]",
            repoName,
            branchDate,
            prNumber);
        return;
      }

      if (!processRepository.getPrCreated()) {
        log.info(
            "Github Pull Request NOT Merged, No Pull Request: [{}] | [{}] | [{}]",
            repoName,
            branchDate,
            prNumber);
        return;
      }
    }

    final Integer prNumberFromWorkflowRun = checkWorkflowRun(repoName, branchDate);

    if (prNumberFromWorkflowRun == null) {
      log.info("PR Number from Workflow Run NOT Found: [{}] | [{}]", repoName, branchDate);
    }

    if (prNumber == null && prNumberFromWorkflowRun == null) {
      log.error("No PR Number to Merge: [{}] | [{}]", repoName, branchDate);
      return;
    }

    if (prNumber != null && !Objects.equals(prNumber, prNumberFromWorkflowRun)) {
      log.error(
          "PR Number Not Matched in Workflow Run: [{}] | [{}] | [{}] | [{}]",
          repoName,
          branchDate,
          prNumber,
          prNumberFromWorkflowRun);
      return;
    }

    final GithubApiModel.MergePullRequestResponse mergePullRequestResponse =
        githubConnector.mergePullRequest(repoName, prNumberFromWorkflowRun);

    if (mergePullRequestResponse != null
        && mergePullRequestResponse.getMerged() != null
        && mergePullRequestResponse.getMerged()) {
      ProcessUtils.updateProcessedRepositoriesPrMerged(repoName);
    } else {
      log.error(
          "PR Not Merged: [{}] | [{}] | [{}] | [{}] | [{}]",
          repoName,
          branchDate,
          prNumber,
          prNumberFromWorkflowRun,
          mergePullRequestResponse);
    }
  }

  public Integer checkWorkflowRun(final String repoName, final LocalDate branchDate) {
    log.info("Check Workflow Run: [{}] | [{}]", repoName, branchDate);

    final String branchName = String.format(ConstantUtils.BRANCH_UPDATE_DEPENDENCIES, branchDate);
    final GithubApiModel.ListWorkflowRunsResponse workflowRunsResponse =
        githubConnector.listWorkflowRuns(repoName);

    if (workflowRunsResponse == null
        || CommonUtilities.isEmpty(workflowRunsResponse.getWorkflowRuns())) {
      log.error(
          "Workflow Runs Response IS null OR Workflow Runs IS empty: [{}] | [{}] | [{}]",
          repoName,
          branchDate,
          workflowRunsResponse);
      return null;
    }

    GithubApiModel.ListWorkflowRunsResponse.WorkflowRun workflowRun =
        workflowRunsResponse.getWorkflowRuns().stream()
            .filter(
                wr ->
                    wr.getHeadBranch().equals(branchName)
                        && !CommonUtilities.isEmpty(wr.getPullRequests()))
            .findFirst()
            .orElse(null);

    if (workflowRun == null) {
      log.error(
          "Workflow Runs IS null: [{}] | [{}] | [{}]", repoName, branchDate, workflowRunsResponse);
      return null;
    }

    final boolean isWorkflowRunSuccessful =
        Objects.equals(branchName, workflowRun.getHeadBranch())
            && Objects.equals("pull_request", workflowRun.getEvent())
            && Objects.equals("completed", workflowRun.getStatus())
            && Objects.equals("success", workflowRun.getConclusion());

    if (!isWorkflowRunSuccessful) {
      log.info(
          "Workflow Run Not Successful: [{}] | [{}] | [{}]", repoName, branchName, workflowRun);
      return null;
    }

    return workflowRun.getPullRequests().getFirst().getNumber();
  }

  public GithubApiModel.RateLimitResponse getCurrentGithubRateLimits() {
    return githubConnector.getRateLimits();
  }
}
