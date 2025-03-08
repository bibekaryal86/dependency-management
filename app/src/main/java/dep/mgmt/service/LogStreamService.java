package dep.mgmt.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class LogStreamService {

  public List<String> getLogFileNames(final String logHome) {
    try (Stream<Path> pathStream = Files.walk(Paths.get(logHome))) {
      List<String> logFileNames =
          new ArrayList<>(
              pathStream.filter(Files::isRegularFile).toList().stream()
                  .map(path -> path.getFileName().toString())
                  .filter(path -> !path.equals("app-dependency-update.log"))
                  .sorted(Comparator.reverseOrder())
                  .toList());
      logFileNames.addFirst("app-dependency-update.log");
      return logFileNames;
    } catch (Exception ex) {
      throw new RuntimeException("Logs not found in the repo path provided!", ex);
    }
  }

  public String getLogFileContent(Path logPath) throws IOException {
    return Files.readString(logPath);
  }
}
