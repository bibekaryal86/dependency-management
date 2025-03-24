package dep.mgmt.update;

import dep.mgmt.model.AppData;
import dep.mgmt.model.AppDataRepository;
import dep.mgmt.model.AppDataScriptFile;
import dep.mgmt.model.enums.RequestParams;
import dep.mgmt.util.ConstantUtils;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
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
      final AppData appData, final String branchName, final String repoName) {
    this.repositories =
        appData.getRepositories().stream()
            .filter(repository -> repository.getType().equals(RequestParams.UpdateType.GRADLE))
            .filter(
                repository -> {
                  if (CommonUtilities.isEmpty(repoName)) {
                    return true;
                  } else {
                    return repository.getRepoName().equals(repoName);
                  }
                })
            .toList();
    this.scriptFile =
        appData.getScriptFiles().stream()
            .filter(sf -> sf.getScriptName().equals(ConstantUtils.SCRIPT_SPOTLESS))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Gradle Spotless Script Not Found..."));
    this.branchName = branchName;
  }

  public void execute() {
    // updating Gradle Spotless is fairly straightforward because everything is done by the gradlew
    // script, we just need to execute it for each repository
    this.repositories.forEach(this::executeUpdate);
  }

  private void executeUpdate(final AppDataRepository repository) {
    log.debug("Execute Gradle Spotless Update on: [ {} ]", repository);
    List<String> arguments = new LinkedList<>();
    arguments.add(repository.getRepoPath().toString());
    arguments.add(branchName);
    new ExecuteScriptFile(this.scriptFile.getScriptFileName(), arguments, Boolean.TRUE);
  }
}
