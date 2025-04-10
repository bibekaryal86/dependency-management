package dep.mgmt.service;

import dep.mgmt.connector.GithubConnector;
import dep.mgmt.model.web.GithubApiModel;
import dep.mgmt.util.ConstantUtils;
import dep.mgmt.util.ProcessUtils;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Objects;

public class GithubService {
    private static final Logger log = LoggerFactory.getLogger(GithubService.class);

    private final GithubConnector githubConnector;

    public GithubService() {
        this.githubConnector = new GithubConnector();
    }

    public void createGithubPullRequest(final String repoName, final LocalDate branchDate) {
        log.info("Create Github Pull Request: [{}] | [{}]", repoName, branchDate);
        final String branchName = String.format(ConstantUtils.BRANCH_UPDATE_DEPENDENCIES, branchDate);
        final GithubApiModel.CreatePullRequestResponse createPullRequestResponse = githubConnector.createPullRequest(repoName, branchName);

        if (createPullRequestResponse != null && createPullRequestResponse.getNumber() != null && createPullRequestResponse.getNumber() > 0) {
            ProcessUtils.updateProcessedRepositoriesPrCreated(repoName, createPullRequestResponse.getNumber());
        }
    }

    public void mergeGithubPullRequest(final String repoName, final LocalDate branchDate, final Integer prNumber) {
        log.info("Merge Github Pull Request: [{}] | [{}] | [{}]", repoName, branchDate, prNumber);
        final Integer prNumberFromWorkflowRun = checkWorkflowRun(repoName, branchDate);

        if (prNumberFromWorkflowRun == null) {
            log.info("PR Not Found in Workflow Run: [{}] | [{}]", repoName, branchDate);

        }

        if (prNumber != null && !Objects.equals(prNumber, prNumberFromWorkflowRun)) {
            log.error("PR Number Not Matched in Workflow Run: [{}] | [{}] | [{}] | [{}]",
                    repoName, branchDate, prNumber, prNumberFromWorkflowRun);
            ProcessUtils.addRepositoriesToRetryMerge(repoName);
        }

        if ((prNumber != null && Objects.equals(prNumber, prNumberFromWorkflowRun)) || prNumberFromWorkflowRun != null) {
            final GithubApiModel.MergePullRequestResponse mergePullRequestResponse = githubConnector.mergePullRequest(repoName, prNumber);

            if (mergePullRequestResponse != null && mergePullRequestResponse.getMerged() != null && mergePullRequestResponse.getMerged()) {
                ProcessUtils.updateProcessedRepositoriesPrMerged(repoName);
                ProcessUtils.removeRepositoriesToRetryMerge(repoName);
            } else {
                ProcessUtils.addRepositoriesToRetryMerge(repoName);
            }
        }
    }

    public Integer checkWorkflowRun(final String repoName, final LocalDate branchDate) {
        log.info("Check Workflow Run: [{}] | [{}]", repoName, branchDate);

        final String branchName = String.format(ConstantUtils.BRANCH_UPDATE_DEPENDENCIES, branchDate);
        final GithubApiModel.ListWorkflowRunsResponse workflowRunsResponse = githubConnector.listWorkflowRuns(repoName);

        if (workflowRunsResponse == null || CommonUtilities.isEmpty(workflowRunsResponse.getWorkflowRuns())) {
            log.error("Workflow Runs Response IS null OR Workflow Runs IS empty: [{}] | [{}] | [{}]", repoName, branchDate, workflowRunsResponse);
            return null;
        }

        GithubApiModel.ListWorkflowRunsResponse.WorkflowRun workflowRun = workflowRunsResponse.getWorkflowRuns().stream()
                .filter(wr -> wr.getHeadBranch().equals(branchName)
                        && !CommonUtilities.isEmpty(wr.getPullRequests()))
                .findFirst()
                .orElse(null);

        if (workflowRun == null) {
            log.error("Workflow Runs IS null: [{}] | [{}] | [{}]", repoName, branchDate, workflowRunsResponse);
            return null;
        }

        final boolean isWorkflowRunSuccessful = Objects.equals(branchName, workflowRun.getHeadBranch()) &&
                Objects.equals("pull_request", workflowRun.getEvent()) &&
                Objects.equals("completed", workflowRun.getStatus()) &&
                Objects.equals("success", workflowRun.getConclusion());

        if (!isWorkflowRunSuccessful) {
            log.info("Workflow Run Not Successful: [{}] | [{}] | [{}]", repoName, branchName, workflowRun);
        }

        return workflowRun.getPullRequests().getFirst().getNumber();
    }
}
