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

  private final String repoHome;
  private final AppDataRepository repository;
  private final AppDataScriptFile scriptFile;
  private final boolean isReset;
  private final boolean isPull;
  private final boolean isRunAsync;

  public UpdateRepoResetPull(
      final String repoHome,
      final AppDataScriptFile scriptFile,
      final boolean isReset,
      final boolean isPull,
      final boolean isRunAsync) {
    this.repoHome = repoHome;
    this.repository = null;
    this.scriptFile = scriptFile;
    this.isReset = isReset;
    this.isPull = isPull;
    this.isRunAsync = isRunAsync;
  }

  public UpdateRepoResetPull(
      final AppDataRepository repository,
      final AppDataScriptFile scriptFile,
      final boolean isReset,
      final boolean isPull,
      final boolean isRunAsync) {
    this.repoHome = null;
    this.repository = repository;
    this.scriptFile = scriptFile;
    this.isReset = isReset;
    this.isPull = isPull;
    this.isRunAsync = isRunAsync;
  }

  public void execute() {
    log.info(
        "Execute Update Repo Reset Pull on: [{}] | [{}] | [{}] | [{}] | [{}]",
        this.repoHome,
        this.repository,
        this.isReset,
        this.isPull,
        this.isRunAsync);
    List<String> arguments = new LinkedList<>();
    if (CommonUtilities.isEmpty(this.repoHome) && this.repository != null) {
      arguments.add(this.repository.getRepoPath().toString());
    } else if (!CommonUtilities.isEmpty(this.repoHome) && this.repository == null) {
      arguments.add(this.repoHome);
    } else {
      arguments.add("N/A");
    }
    arguments.add(String.valueOf(this.isReset));
    arguments.add(String.valueOf(this.isPull));
    new ExecuteScriptFile(this.scriptFile, arguments, this.isRunAsync, this.repository)
        .executeScript();
  }
}
