package dep.mgmt.model;

import dep.mgmt.model.enums.RequestParams;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class AppDataRepository implements Serializable {
  private final Path repoPath;
  private final RequestParams.UpdateType type;
  private final String repoName;
  private final String currentGradleVersion;
  private final List<String> gradleModules;
  private final List<String> requirementsTxts;

  public AppDataRepository(Path repoPath, RequestParams.UpdateType type) {
    this.repoPath = repoPath;
    this.type = type;
    this.repoName = repoPath.getFileName().toString();
    this.currentGradleVersion = "";
    this.gradleModules = Collections.emptyList();
    this.requirementsTxts = Collections.emptyList();
  }

  public AppDataRepository(Path repoPath, RequestParams.UpdateType type, List<String> stringList) {
    this.repoPath = repoPath;
    this.type = type;
    this.repoName = repoPath.getFileName().toString();
    this.currentGradleVersion = "";
    if (type.equals(RequestParams.UpdateType.GRADLE_DEPENDENCIES)) {
      this.gradleModules = stringList;
      this.requirementsTxts = Collections.emptyList();
    } else if (type.equals(RequestParams.UpdateType.PYTHON_DEPENDENCIES)) {
      this.gradleModules = Collections.emptyList();
      this.requirementsTxts = stringList;
    } else {
      this.gradleModules = Collections.emptyList();
      this.requirementsTxts = Collections.emptyList();
    }
  }

  public AppDataRepository(
      Path repoPath,
      RequestParams.UpdateType type,
      List<String> gradleModules,
      String currentGradleVersion) {
    this.repoPath = repoPath;
    this.type = type;
    this.gradleModules = gradleModules;
    this.repoName = repoPath.getFileName().toString();
    this.currentGradleVersion = currentGradleVersion;
    this.requirementsTxts = Collections.emptyList();
  }

  public Path getRepoPath() {
    return repoPath;
  }

  public RequestParams.UpdateType getType() {
    return type;
  }

  public String getRepoName() {
    return repoName;
  }

  public String getCurrentGradleVersion() {
    return currentGradleVersion;
  }

  public List<String> getGradleModules() {
    return gradleModules;
  }

  public List<String> getRequirementsTxts() {
    return requirementsTxts;
  }

  @Override
  public String toString() {
    return "AppDataRepository{"
        + "repoPath="
        + repoPath
        + ", type="
        + type
        + ", repoName='"
        + repoName
        + '\''
        + ", currentGradleVersion='"
        + currentGradleVersion
        + '\''
        + ", gradleModules="
        + gradleModules
        + ", requirementsTxts="
        + requirementsTxts
        + '}';
  }
}
