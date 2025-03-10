package dep.mgmt.model.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;

public class JavaReleaseResponse implements Serializable {
  private final List<JavaVersion> versions;

  @JsonCreator
  public JavaReleaseResponse(@JsonProperty("versions") final List<JavaVersion> versions) {
    this.versions = versions;
  }

  public List<JavaVersion> getVersions() {
    return versions;
  }

  @Override
  public String toString() {
    return "JavaReleaseResponse{" + "versions=" + versions + '}';
  }

  public static class JavaVersion implements Serializable {
    private final String semver;
    private final String optional;

    @JsonCreator
    public JavaVersion(
        @JsonProperty("semver") final String semver,
        @JsonProperty("optional") final String optional) {
      this.semver = semver;
      this.optional = optional;
    }

    public String getSemver() {
      return semver;
    }

    public String getOptional() {
      return optional;
    }

    @Override
    public String toString() {
      return "JavaVersion{" + "semver='" + semver + '\'' + ", optional='" + optional + '\'' + '}';
    }
  }
}
