package dep.mgmt.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;

public class Dependencies implements Serializable {
  private final List<Dependency> dependencies;

  @JsonCreator
  public Dependencies(@JsonProperty("dependencies") final List<Dependency> dependencies) {
    this.dependencies = dependencies;
  }

  public List<Dependency> getDependencies() {
    return dependencies;
  }

  @Override
  public String toString() {
    return "DependencyResponse{" + "dependencies='" + dependencies.size() + '}';
  }

  public static class Dependency implements Serializable {
    private final String name;
    private final String version;
    private final Boolean skipVersion;

    @JsonCreator
    public Dependency(
        @JsonProperty("name") final String name,
        @JsonProperty("version") final String version,
        @JsonProperty("skipVersion") final Boolean skipVersion) {
      this.name = name;
      this.version = version;
      this.skipVersion = skipVersion;
    }

    public String getName() {
      return name;
    }

    public String getVersion() {
      return version;
    }

    public Boolean getSkipVersion() {
      return skipVersion;
    }

    @Override
    public String toString() {
      return "Dependency{"
          + "name='"
          + name
          + '\''
          + ", version='"
          + version
          + '\''
          + ", skipVersion="
          + skipVersion
          + '}';
    }
  }
}
