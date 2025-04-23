package dep.mgmt.update;

import dep.mgmt.model.AppDataRepository;
import dep.mgmt.model.AppDataScriptFile;
import dep.mgmt.util.ConstantUtils;
import dep.mgmt.util.ProcessUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecuteScriptFile {
  private static final Logger log = LoggerFactory.getLogger(ExecuteScriptFile.class);

  public static void executeScript(
      final AppDataScriptFile scriptFile,
      final List<String> arguments,
      final boolean isRunAsync,
      final AppDataRepository repository) {
    final String repoName;
    final String type;
    if (repository == null) {
      repoName = null;
      type = null;
    } else {
      repoName = repository.getRepoName();
      type = repository.getType().toString();
    }
    final String scriptPath =
        ConstantUtils.JAVA_SYSTEM_TMPDIR
            + ConstantUtils.PATH_DELIMITER
            + ConstantUtils.SCRIPTS_DIRECTORY
            + ConstantUtils.PATH_DELIMITER
            + scriptFile.getScriptFileName();

    if (isRunAsync) {
      runAsync(scriptPath, repoName, type, arguments, isRunAsync);
      log.info("Script execution started asynchronously: [{}] | [{}]", scriptPath, repoName);
    } else {
      runSync(scriptPath, repoName, type, arguments, isRunAsync);
    }
  }

  private static void runAsync(
      final String scriptPath,
      final String repoName,
      final String type,
      final List<String> arguments,
      final boolean isRunAsync) {
    new Thread(
            () -> {
              try {
                Process process = startProcess(scriptPath, arguments, isRunAsync);
                processOutput(process, scriptPath, repoName, type);
              } catch (Exception ex) {
                log.error("Error in Execute Script (async): [{}] | [{}]", scriptPath, repoName, ex);
              }
            })
        .start();
  }

  private static void runSync(
      final String scriptPath,
      final String repoName,
      final String type,
      final List<String> arguments,
      final boolean isRunAsync) {
    log.debug("Running: [{}] | [{}]", scriptPath, repoName);
    try {
      final Process process = startProcess(scriptPath, arguments, isRunAsync);
      processOutput(process, scriptPath, repoName, type);
    } catch (Exception ex) {
      log.error("Error in Execute Script: [{}] | [{}]", scriptPath, repoName, ex);
    }
  }

  private static Process startProcess(
      final String scriptPath, final List<String> arguments, final boolean isRunAsync)
      throws IOException, InterruptedException {
    log.debug("Starting: [{}] | [{}]", scriptPath, arguments);
    try {
      List<String> command = new LinkedList<>();
      command.add(ConstantUtils.COMMAND_PATH);
      command.add(scriptPath);
      command.addAll(arguments);

      ProcessBuilder processBuilder = new ProcessBuilder(command);
      Process process = processBuilder.start();

      if (!isRunAsync) {
        process.waitFor();
      }

      return process;
    } catch (IOException ex) {
      throw new IOException(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      throw new InterruptedException(
          ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
    }
  }

  private static void processOutput(
      final Process process, final String scriptPath, final String repoName, final String type)
      throws IOException {
    StringBuilder stringBuilder = new StringBuilder();
    String line;
    boolean isError = false;

    try (BufferedReader readerError =
        new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
      while ((line = readerError.readLine()) != null) {
        stringBuilder.append("ERROR-- ").append(line).append("\n");
        isError = true;
      }

      try (BufferedReader readerInput =
          new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        while ((line = readerInput.readLine()) != null) {
          stringBuilder.append(line).append("\n");
          if (line.toLowerCase().contains("error") || line.toLowerCase().contains("fatal")) {
            isError = true;
          }
        }
      }

      if (isError) {
        log.info("ERROR in Process: [{}] | [{}]\n{}", scriptPath, repoName, stringBuilder);
      } else {
        log.debug("Process output: [{}] | [{}] \n{}", scriptPath, repoName, stringBuilder);
      }

      checkProcessedRepository(stringBuilder, scriptPath, repoName, type);
    } catch (IOException ex) {
      throw new IOException(
          "Error in Process Stream Output: "
              + ", "
              + scriptPath
              + " | "
              + repoName
              + "---"
              + ex.getCause().getMessage());
    }
  }

  private static void checkProcessedRepository(
      final StringBuilder stringBuilder,
      final String scriptPath,
      final String repoName,
      final String type) {
    if (scriptPath.contains(ConstantUtils.SCRIPT_UPDATE_EXEC)) {
      final boolean isPushedNewBranch = stringBuilder.toString().contains("Pushed new branch");
      ProcessUtils.addProcessedRepositories(repoName, type, isPushedNewBranch);
    }
  }
}
