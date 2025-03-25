package dep.mgmt.update;

import dep.mgmt.model.AppDataRepository;
import dep.mgmt.model.AppDataScriptFile;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateNpmSnapshots {
  private static final Logger log = LoggerFactory.getLogger(UpdateNpmSnapshots.class);

  private final List<AppDataRepository> repositories;
  private final AppDataScriptFile scriptFile;
  private final String branchName;

  public UpdateNpmSnapshots(
      final List<AppDataRepository> repositories,
      final AppDataScriptFile scriptFile,
      final String branchName) {
    this.repositories = repositories;
    this.scriptFile = scriptFile;
    this.branchName = branchName;
  }

  public void execute() {
    // updating NPM snapshots is fairly straightforward because everything is done by the npm
    // script, we just need to execute it for each repository
    this.repositories.forEach(this::executeUpdate);
  }

  private void executeUpdate(final AppDataRepository repository) {
    log.debug("Execute NPM Snapshots Update on: [ {} ]", repository);
    List<String> arguments = new LinkedList<>();
    arguments.add(repository.getRepoPath().toString());
    arguments.add(branchName);
    new ExecuteScriptFile(this.scriptFile.getScriptFileName(), arguments, Boolean.TRUE);
  }
}
