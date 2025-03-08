package dep.mgmt.util;

import dep.mgmt.model.AppDataScriptFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptUtils {

  private static final Logger log = LoggerFactory.getLogger(ScriptUtils.class);

  private static final String TEMP_SCRIPTS_DIRECTORY =
      ConstantUtils.JAVA_SYSTEM_TMPDIR
          + ConstantUtils.PATH_DELIMITER
          + ConstantUtils.SCRIPTS_DIRECTORY;
  private final Path tempScriptsDirectoryPath = Path.of(TEMP_SCRIPTS_DIRECTORY);

  public void deleteTempScriptFiles() {
    log.info("Delete Temp Script Files...");

    try {
      if (Files.exists(this.tempScriptsDirectoryPath)) {
        try (Stream<Path> paths = Files.walk(tempScriptsDirectoryPath)) {
          paths.sorted(Comparator.reverseOrder()).forEach(this::delete);
        }
      }
    } catch (IOException ex) {
      log.error("ERROR Delete Temp Script Files: [ {} ]", ex.getMessage(), ex);
    }
  }

  public void createTempScriptFiles() {
    boolean isError = createTempScriptsDirectory();
    if (isError) {
      throw new RuntimeException("Unable to create temp directory to store scripts...");
    }

    List<AppDataScriptFile> scriptFiles = AppDataUtils.appData().getScriptFiles();
    for (final AppDataScriptFile scriptFile : scriptFiles) {
      isError = createTempScriptFile(scriptFile);
      if (isError) {
        throw new RuntimeException(
            String.format(
                "Unable to create and temp script file %s...", scriptFile.getScriptFileName()));
      } else {
        giveExecutePermissionToFile(scriptFile);
      }
    }
  }

  public boolean isScriptFilesMissingInFileSystem() {
    try {
      if (!Files.exists(this.tempScriptsDirectoryPath)) {
        return true;
      }

      List<AppDataScriptFile> scriptFiles = AppDataUtils.setAppData().getScriptFiles();
      for (final AppDataScriptFile scriptFile : scriptFiles) {
        Path filePath =
            Path.of(
                TEMP_SCRIPTS_DIRECTORY
                    + ConstantUtils.PATH_DELIMITER
                    + scriptFile.getScriptFileName());
        if (!Files.exists(filePath)) {
          return true;
        }
      }
    } catch (RuntimeException ex) {
      log.error("Error checking if Script files exist in Directory", ex);
      return true;
    }
    return false;
  }

  private void delete(final Path path) {
    try {
      boolean isDeleted = Files.deleteIfExists(path);
      log.info("Delete: [ {} ] | [ {} ]", path, isDeleted);
    } catch (IOException ex) {
      log.info("ERROR Delete: [ {} ]", path, ex);
    }
  }

  private boolean createTempScriptsDirectory() {
    try {
      if (!Files.exists(this.tempScriptsDirectoryPath)) {
        log.debug("Creating temp script directory: [ {} ]", this.tempScriptsDirectoryPath);
        Files.createDirectory(this.tempScriptsDirectoryPath);
      }
      return false;
    } catch (IOException ex) {
      log.error("Error creating temp script directory", ex);
      return true;
    }
  }

  private boolean createTempScriptFile(final AppDataScriptFile scriptFile) {
    try {
      Path filePath =
          Files.createFile(
              Path.of(
                  TEMP_SCRIPTS_DIRECTORY
                      + ConstantUtils.PATH_DELIMITER
                      + scriptFile.getScriptFileName()));
      try (InputStream inputStream =
          getClass()
              .getClassLoader()
              .getResourceAsStream(
                  ConstantUtils.SCRIPTS_DIRECTORY
                      + ConstantUtils.PATH_DELIMITER
                      + scriptFile.getScriptFileName())) {
        assert inputStream != null;
        Files.write(filePath, inputStream.readAllBytes(), StandardOpenOption.WRITE);
        log.info("Written to file: [ {} ]", filePath);
        return false;
      }
    } catch (IOException | NullPointerException ex) {
      log.error("Error creating temp script file: [ {} ]", scriptFile, ex);
      return true;
    }
  }

  private void giveExecutePermissionToFile(final AppDataScriptFile scriptFile) {
    try {
      String scriptPath =
          ConstantUtils.JAVA_SYSTEM_TMPDIR
              + ConstantUtils.PATH_DELIMITER
              + ConstantUtils.SCRIPTS_DIRECTORY
              + ConstantUtils.PATH_DELIMITER
              + scriptFile.getScriptFileName();
      new ProcessBuilder(ConstantUtils.COMMAND_PATH, ConstantUtils.CHMOD_COMMAND + scriptPath)
          .start();
    } catch (IOException ex) {
      log.error(
          "Error on Give Execute Permission to File: [ {} ]", scriptFile.getScriptFileName(), ex);
    }
  }
}
