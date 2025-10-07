package dep.mgmt.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.LocalDateTime;
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
    return "DependencyResponse{" + "dependencies=" + dependencies.size() + '}';
  }

  public static class Dependency implements Serializable {
    private final String name;
    private final String version;
    private final Boolean skipVersion;
    private final LocalDateTime lastCheckedDate;
    private final LocalDateTime lastUpdatedDate;

    @JsonCreator
    public Dependency(
        @JsonProperty("name") final String name,
        @JsonProperty("version") final String version,
        @JsonProperty("skipVersion") final Boolean skipVersion,
        @JsonProperty("lastCheckedDate") final LocalDateTime lastCheckedDate,
        @JsonProperty("lastUpdatedDate") final LocalDateTime lastUpdatedDate) {
      this.name = name;
      this.version = version;
      this.skipVersion = skipVersion;
      this.lastCheckedDate = lastCheckedDate;
      this.lastUpdatedDate = lastUpdatedDate;
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

    public LocalDateTime getLastCheckedDate() {
      return lastCheckedDate;
    }

    public LocalDateTime getLastUpdatedDate() {
      return lastUpdatedDate;
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
          + ", lastCheckedDate="
          + lastCheckedDate
          + ", lastUpdatedDate="
          + lastUpdatedDate
          + '}';
    }
  }
}
