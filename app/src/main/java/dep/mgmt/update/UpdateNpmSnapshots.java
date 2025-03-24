package dep.mgmt.update;

import dep.mgmt.model.AppData;
import dep.mgmt.model.AppDataRepository;
import dep.mgmt.model.AppDataScriptFile;
import dep.mgmt.model.enums.RequestParams;
import dep.mgmt.util.ConstantUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public class UpdateNpmSnapshots {
  private static final Logger log = LoggerFactory.getLogger(UpdateNpmSnapshots.class);

  private final List<AppDataRepository> repositories;
  private final AppDataScriptFile scriptFile;
  private final String branchName;

  public UpdateNpmSnapshots(final AppData appData, final String branchName) {
    this.repositories =
        appData.getRepositories().stream()
            .filter(repository -> repository.getType().equals(RequestParams.UpdateType.NPM))
            .toList();
    this.scriptFile =
        appData.getScriptFiles().stream()
            .filter(sf -> sf.getScriptName().equals(ConstantUtils.SCRIPT_SNAPSHOT))
            .findFirst()
            .orElseThrow(
                () -> new IllegalStateException("NPM Snapshot Script Not Found..."));
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
