package dep.mgmt.update;

import dep.mgmt.model.AppDataRepository;
import dep.mgmt.model.AppDataScriptFile;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateBranchDelete {
  private static final Logger log = LoggerFactory.getLogger(UpdateBranchDelete.class);

  private final String repoHome;
  private final AppDataRepository repository;
  private final AppDataScriptFile scriptFile;
  private final boolean isDeleteUpdateDependenciesOnly;

  public UpdateBranchDelete(
      final String repoHome,
      AppDataScriptFile scriptFile,
      final boolean isDeleteUpdateDependenciesOnly) {
    this.repoHome = repoHome;
    this.repository = null;
    this.scriptFile = scriptFile;
    this.isDeleteUpdateDependenciesOnly = isDeleteUpdateDependenciesOnly;
  }

  public UpdateBranchDelete(
      final AppDataRepository repository,
      AppDataScriptFile scriptFile,
      final boolean isDeleteUpdateDependenciesOnly) {
    this.repoHome = null;
    this.repository = repository;
    this.scriptFile = scriptFile;
    this.isDeleteUpdateDependenciesOnly = isDeleteUpdateDependenciesOnly;
  }

  public void execute() {
    log.debug("Update Branch Delete: [{}] | [{}]", this.repoHome, this.repository);
    List<String> arguments = new LinkedList<>();
    if (CommonUtilities.isEmpty(this.repoHome) && this.repository != null) {
      arguments.add(this.repository.getRepoPath().toString());
    } else if (!CommonUtilities.isEmpty(this.repoHome) && this.repository == null) {
      arguments.add(this.repoHome);
    } else {
      arguments.add("N/A");
    }
    arguments.add(String.valueOf(this.isDeleteUpdateDependenciesOnly));
    new ExecuteScriptFile(this.scriptFile, arguments, Boolean.TRUE, this.repository)
        .executeScript();
  }
}
