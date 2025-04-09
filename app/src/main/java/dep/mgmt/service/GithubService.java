package dep.mgmt.service;

import dep.mgmt.connector.GithubConnector;
import dep.mgmt.model.ProcessSummaries;
import dep.mgmt.model.web.GithubApiModel;
import dep.mgmt.util.ConstantUtils;
import dep.mgmt.util.ProcessUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class GithubService {
    private static final Logger log = LoggerFactory.getLogger(GithubService.class);

    private final GithubConnector githubConnector;

    public GithubService() {
        this.githubConnector = new GithubConnector();
    }

    public void createGithubPullRequest(final String repoName, final LocalDate branchDate) {
        log.info("Create Github Pull Request: [{}] | [{}]", repoName, branchDate);
        final String branchName = String.format(ConstantUtils.BRANCH_UPDATE_DEPENDENCIES, branchDate);
        final GithubApiModel.CreatePullRequestResponse createPullRequestResponse = this.githubConnector.createPullRequest(repoName, branchName);

        if (createPullRequestResponse != null && createPullRequestResponse.getNumber() != null && createPullRequestResponse.getNumber() > 0) {
            ProcessUtils.updateProcessedRepositoriesPrCreated(repoName, createPullRequestResponse.getNumber());
        }
    }

    public void mergeGithubPullRequest(final String repoName, final Integer prNumber) {
        log.info("Merge Github Pull Request: [{}] | [{}]", repoName, prNumber);
        final GithubApiModel.MergePullRequestResponse mergePullRequestResponse = this.githubConnector.mergePullRequest(repoName, prNumber);

        if (mergePullRequestResponse != null && mergePullRequestResponse.getMerged() != null && mergePullRequestResponse.getMerged()) {
            ProcessUtils.updateProcessedRepositoriesPrMerged(repoName);
        }
    }
}
