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

  private final AppDataRepository repository;
  private final AppDataScriptFile scriptFile;
  private final boolean isInit;
  private final String branchName;

  public UpdateDependencies(
      final AppDataRepository repository,
      final AppDataScriptFile scriptFile,
      final boolean isInit) {
    this.repository = repository;
    this.scriptFile = scriptFile;
    this.isInit = isInit;
    this.branchName = null;
  }

  public UpdateDependencies(
      final AppDataRepository repository,
      final AppDataScriptFile scriptFile,
      final String branchName) {
    this.repository = repository;
    this.scriptFile = scriptFile;
    this.branchName = branchName;
    this.isInit = Boolean.FALSE;
  }

  public void execute() {
    log.debug(
        "Update Dependencies Init/Exit: [{}] | [{}] | [{}] | [{}]",
        this.repository,
        this.scriptFile,
        this.isInit,
        this.branchName);
    List<String> arguments = new LinkedList<>();
    arguments.add(this.repository.getRepoPath().toString());

    if (CommonUtilities.isEmpty(branchName)) {
      arguments.add(String.valueOf(this.isInit));
    } else {
      arguments.add(this.branchName);
    }

    new ExecuteScriptFile(this.scriptFile, arguments, Boolean.FALSE, this.repository)
        .executeScript();
  }
}
