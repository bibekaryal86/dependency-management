package dep.mgmt.update;

import dep.mgmt.model.AppDataRepository;
import dep.mgmt.model.AppDataScriptFile;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateDependencies {
  private static final Logger log = LoggerFactory.getLogger(UpdateDependencies.class);

  public static void execute(
      final AppDataRepository repository,
      final AppDataScriptFile scriptFile,
      final String branchName,
      final boolean isInit) {
    final String actionType;
    if (isInit) {
      actionType = "INIT";
    } else if (CommonUtilities.isEmpty(branchName)) {
      actionType = "EXIT";
    } else {
      actionType = "EXEC";
    }

    log.info(
        "Execute Update Dependencies ActionType=[{}] | RepoName=[{}] | ScriptName=[{}] | IsInit=[{}] | BranchName=[{}]",
        actionType,
        repository.getRepoName(),
        scriptFile.getScriptName(),
        isInit,
        branchName);
    List<String> arguments = new LinkedList<>();
    arguments.add(repository.getRepoPath().toString());

    if (CommonUtilities.isEmpty(branchName)) {
      arguments.add(String.valueOf(isInit));
    } else {
      arguments.add(branchName);
    }

    arguments.add(repository.getType().name());
    ExecuteScriptFile.executeScript(scriptFile, arguments, Boolean.FALSE, repository);
  }
}
