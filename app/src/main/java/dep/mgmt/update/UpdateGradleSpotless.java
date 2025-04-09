package dep.mgmt.update;

import dep.mgmt.model.AppDataRepository;
import dep.mgmt.model.AppDataScriptFile;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateGradleSpotless {
  private static final Logger log = LoggerFactory.getLogger(UpdateGradleSpotless.class);

  private final List<AppDataRepository> repositories;
  private final AppDataScriptFile scriptFile;
  private final String branchName;

  public UpdateGradleSpotless(
      final List<AppDataRepository> repositories,
      final AppDataScriptFile scriptFile,
      final String branchName) {
    this.repositories = repositories;
    this.scriptFile = scriptFile;
    this.branchName = branchName;
  }

  public void execute() {
    // updating Gradle Spotless is fairly straightforward because everything is done by the gradlew
    // script, we just need to execute it for each repository
    this.repositories.forEach(this::executeUpdate);
  }

  private void executeUpdate(final AppDataRepository repository) {
    log.debug("Update Gradle Spotless: [ {} ]", repository);
    List<String> arguments = new LinkedList<>();
    arguments.add(repository.getRepoPath().toString());
    arguments.add(branchName);
    new ExecuteScriptFile(this.scriptFile, arguments, Boolean.TRUE, repository).executeScript();
  }
}
