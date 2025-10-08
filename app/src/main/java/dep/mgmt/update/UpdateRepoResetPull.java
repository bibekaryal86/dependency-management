package dep.mgmt.update;

import dep.mgmt.model.AppDataRepository;
import dep.mgmt.model.AppDataScriptFile;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateRepoResetPull {
  private static final Logger log = LoggerFactory.getLogger(UpdateRepoResetPull.class);

  public static void execute(
      final String repoHome,
      final AppDataRepository repository,
      final AppDataScriptFile scriptFile,
      final boolean isReset,
      final boolean isPull,
      final boolean isRunAsync) {
    log.info(
        "Execute Update Repo Reset Pull on: RepoName=[{}] | RepoHome=[{}] | IsReset=[{}] | IsPull=[{}] | IsRunAsync=[{}]",
        repository == null ? null : repository.getRepoName(),
        repoHome,
        isReset,
        isPull,
        isRunAsync);
    List<String> arguments = new LinkedList<>();
    if (CommonUtilities.isEmpty(repoHome) && repository != null) {
      arguments.add(repository.getRepoPath().toString());
    } else if (!CommonUtilities.isEmpty(repoHome) && repository == null) {
      arguments.add(repoHome);
    } else {
      arguments.add("N/A");
    }
    arguments.add(String.valueOf(isReset));
    arguments.add(String.valueOf(isPull));
    ExecuteScriptFile.executeScript(scriptFile, arguments, isRunAsync, repository);
  }
}
