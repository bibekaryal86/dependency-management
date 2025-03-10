package dep.mgmt.model.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class PythonPackageSearchResponse implements Serializable {
  private final PypiInfo info;

  @JsonCreator
  public PythonPackageSearchResponse(@JsonProperty("info") final PypiInfo info) {
    this.info = info;
  }

  public PypiInfo getInfo() {
    return info;
  }

  @Override
  public String toString() {
    return "PythonPackageSearchResponse{" + "info=" + info + '}';
  }

  public static class PypiInfo implements Serializable {
    private final String name;
    private final String version;
    private final Boolean yanked;

    @JsonCreator
    public PypiInfo(
        @JsonProperty("name") final String name,
        @JsonProperty("version") final String version,
        @JsonProperty("yanked") final Boolean yanked) {
      this.name = name;
      this.version = version;
      this.yanked = yanked;
    }

    public String getName() {
      return name;
    }

    public String getVersion() {
      return version;
    }

    public Boolean getYanked() {
      return yanked;
    }

    @Override
    public String toString() {
      return "PypiInfo{"
          + "name='"
          + name
          + '\''
          + ", version='"
          + version
          + '\''
          + ", yanked="
          + yanked
          + '}';
    }
  }
}
