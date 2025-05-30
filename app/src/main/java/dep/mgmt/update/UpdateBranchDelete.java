package dep.mgmt.update;

import dep.mgmt.model.AppDataRepository;
import dep.mgmt.model.AppDataScriptFile;
import dep.mgmt.model.ProcessSummaries;
import dep.mgmt.util.ProcessUtils;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateBranchDelete {
  private static final Logger log = LoggerFactory.getLogger(UpdateBranchDelete.class);

  public static void execute(
      final String repoHome,
      final AppDataRepository repository,
      final AppDataScriptFile scriptFile,
      final boolean isDeleteUpdateDependenciesOnly,
      final boolean isCheckMergedPrBeforeDelete) {
    log.info(
        "Update Branch Delete: [{}] | [{}] | [{}] | [{}]",
        repoHome,
        repository == null ? null : repository.getRepoName(),
        isDeleteUpdateDependenciesOnly,
        isCheckMergedPrBeforeDelete);
    List<String> arguments = new LinkedList<>();
    if (CommonUtilities.isEmpty(repoHome) && repository != null) {
      arguments.add(repository.getRepoPath().toString());
    } else if (!CommonUtilities.isEmpty(repoHome) && repository == null) {
      arguments.add(repoHome);
    } else {
      arguments.add("N/A");
    }
    arguments.add(String.valueOf(isDeleteUpdateDependenciesOnly));

    if (isCheckMergedPrBeforeDelete) {
      if (repository == null) {
        log.error("Repository is NULL...");
        return;
      }

      ProcessSummaries.ProcessSummary.ProcessRepository processRepository =
          ProcessUtils.getProcessedRepositoryFromMap(repository.getRepoName());

      if (!processRepository.getUpdateBranchCreated()) {
        log.debug("Update Branch NOT Deleted, No Update Branch: [{}]", repository.getRepoName());
        return;
      }

      if (!processRepository.getPrCreated()) {
        log.debug("Update Branch NOT Deleted, No Pull Request: [{}]", repository.getRepoName());
        return;
      }

      if (!processRepository.getPrMerged()) {
        log.debug(
            "Update Branch NOT Deleted, Pull Request Not Merged: [{}]", repository.getRepoName());
        return;
      }

      ExecuteScriptFile.executeScript(scriptFile, arguments, Boolean.FALSE, repository);
    } else {
      ExecuteScriptFile.executeScript(scriptFile, arguments, Boolean.TRUE, repository);
    }
  }
}
