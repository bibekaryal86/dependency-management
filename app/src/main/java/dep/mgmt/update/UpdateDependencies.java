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
    log.info(
        "Execute Update Dependencies: [{}] | [{}] | [{}] | [{}]",
        repository.getRepoName(),
        scriptFile.getScriptFileName(),
        isInit,
        branchName);
    List<String> arguments = new LinkedList<>();
    arguments.add(repository.getRepoPath().toString());

    if (CommonUtilities.isEmpty(branchName)) {
      arguments.add(String.valueOf(isInit));
    } else {
      arguments.add(branchName);
    }

    ExecuteScriptFile.executeScript(scriptFile, arguments, Boolean.FALSE, repository);
  }
}
