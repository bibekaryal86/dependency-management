package dep.mgmt.update;

import dep.mgmt.model.AppData;
import dep.mgmt.model.AppDataScriptFile;
import dep.mgmt.model.enums.RequestParams;
import dep.mgmt.util.ConstantUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public class UpdateBranchDelete {
  private static final Logger log = LoggerFactory.getLogger(UpdateBranchDelete.class);

  private final String repoHome;
  private final AppDataScriptFile scriptFile;
  private final boolean isDeleteUpdateDependenciesOnly;

  public UpdateBranchDelete(final AppData appData, final boolean isDeleteUpdateDependenciesOnly) {
    this.repoHome = appData.getArgsMap().get(ConstantUtils.ENV_REPO_HOME);
    this.scriptFile =
        appData.getScriptFiles().stream()
            .filter(sf -> sf.getScriptName().equals(ConstantUtils.SCRIPT_DELETE))
            .findFirst()
            .orElseThrow(
                () ->
                    new IllegalStateException("Github Branch Delete Script Not Found..."));
    this.isDeleteUpdateDependenciesOnly = isDeleteUpdateDependenciesOnly;
  }

  public void execute() {
    log.debug("Execute Github Branch Delete on: [ {} ]", this.repoHome);
    List<String> arguments = new LinkedList<>();
    arguments.add(this.repoHome);
    arguments.add(String.valueOf(this.isDeleteUpdateDependenciesOnly));
    new ExecuteScriptFile(this.scriptFile.getScriptFileName(), arguments, Boolean.TRUE);
  }
}
