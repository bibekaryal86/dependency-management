package dep.mgmt.update;

import dep.mgmt.model.AppDataRepository;
import dep.mgmt.model.AppDataScriptFile;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateGradleSpotless {
  private static final Logger log = LoggerFactory.getLogger(UpdateGradleSpotless.class);

  public static void execute(
      final List<AppDataRepository> repositories,
      final AppDataScriptFile scriptFile,
      final String branchName) {
    // updating Gradle Spotless is fairly straightforward because everything is done by the gradlew
    // script, we just need to execute it for each repository
    repositories.forEach(repository -> executeUpdate(repository, scriptFile, branchName));
  }

  private static void executeUpdate(
      final AppDataRepository repository,
      final AppDataScriptFile scriptFile,
      final String branchName) {
    log.info("Execute Update Gradle Spotless: [ {} ]", repository.getRepoName());
    List<String> arguments = new LinkedList<>();
    arguments.add(repository.getRepoPath().toString());
    arguments.add(branchName);
    ExecuteScriptFile.executeScript(scriptFile, arguments, Boolean.TRUE, repository);
  }
}
