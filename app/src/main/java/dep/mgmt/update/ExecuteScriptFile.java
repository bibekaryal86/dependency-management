package dep.mgmt.update;

import dep.mgmt.util.ConstantUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class ExecuteScriptFile {
  private static final Logger log = LoggerFactory.getLogger(ExecuteScriptFile.class);
  private final String scriptPath;
  private final List<String> arguments;
  private final boolean isRunAsync;

  public ExecuteScriptFile(final String scriptFileName, final List<String> arguments, final boolean isRunAsync) {
    this.arguments = arguments;
    this.scriptPath =
        ConstantUtils.JAVA_SYSTEM_TMPDIR
            + ConstantUtils.PATH_DELIMITER
            + ConstantUtils.SCRIPTS_DIRECTORY
            + ConstantUtils.PATH_DELIMITER
            + scriptFileName;
    this.isRunAsync = isRunAsync;
  }

  public String executeScript() {
    if (isRunAsync) {
      runAsync();
      return "Script execution started asynchronously.";
    } else {
      return runSync();
    }
  }

  private void runAsync() {
    new Thread(() -> {
      try {
        Process process = startProcess();
        processOutput(process);
      } catch (Exception ex) {
        log.error("Error in Execute Script (async): ", ex);
      }
    }).start();
  }

  private String runSync() {
    try {
      Process process = startProcess();
      return processOutput(process);
    } catch (Exception ex) {
      log.error("Error in Execute Script: ", ex);
      return null;
    }
  }

  private Process startProcess() throws IOException, InterruptedException {
    try {
      List<String> command = new LinkedList<>();
      command.add(ConstantUtils.COMMAND_PATH);
      command.add(this.scriptPath);
      command.addAll(this.arguments);

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
      throw new InterruptedException(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
    }
  }

  private String processOutput(final Process process) throws IOException {
    StringBuilder stringBuilder = new StringBuilder();
    String line;
    boolean isError = false;

    try (BufferedReader readerError = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
      while ((line = readerError.readLine()) != null) {
        stringBuilder.append("ERROR-- ").append(line).append("\n");
        isError = true;
      }

      try (BufferedReader readerInput = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        while ((line = readerInput.readLine()) != null) {
          stringBuilder.append(line).append("\n");
          if (line.toLowerCase().contains("error") || line.toLowerCase().contains("fatal")) {
            isError = true;
          }
        }
      }

      if (isError) {
        log.info("ERROR in Process: [ {} ]\n{}", this.scriptPath, stringBuilder);
      } else {
        log.debug("Process output: [ {} ]\n{}", this.scriptPath, stringBuilder);
      }
      return stringBuilder.toString();
    } catch (IOException ex) {
      throw new IOException("Error in Process Stream Output: " + ", " + this.scriptPath + ex.getCause().getMessage());
    }
  }
}
