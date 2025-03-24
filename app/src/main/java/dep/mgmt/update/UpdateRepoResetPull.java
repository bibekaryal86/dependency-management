package dep.mgmt.update;

import dep.mgmt.model.AppData;
import dep.mgmt.model.AppDataScriptFile;
import dep.mgmt.util.ConstantUtils;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateRepoResetPull {
  private static final Logger log = LoggerFactory.getLogger(UpdateRepoResetPull.class);

  private final String repoHome;
  private final AppDataScriptFile scriptFile;
  private final boolean isPull;
  private final boolean isReset;

  public UpdateRepoResetPull(final AppData appData, final boolean isPull, final boolean isReset) {
    this.repoHome = appData.getArgsMap().get(ConstantUtils.ENV_REPO_HOME);
    this.scriptFile =
        appData.getScriptFiles().stream()
            .filter(sf -> sf.getScriptName().equals(ConstantUtils.SCRIPT_RESET_PULL))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Github Pull Reset Script Not Found..."));
    this.isPull = isPull;
    this.isReset = isReset;
  }

  public void execute() {
    log.debug("Execute Github Pull Reset on: [ {} ]", this.repoHome);
    List<String> arguments = new LinkedList<>();
    arguments.add(this.repoHome);
    arguments.add(String.valueOf(this.isReset));
    arguments.add(String.valueOf(this.isPull));
    new ExecuteScriptFile(this.scriptFile.getScriptFileName(), arguments, Boolean.TRUE);
  }
}
