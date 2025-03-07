package dep.mgmt.model.web;

import java.io.Serializable;

public class PypiSearchResponse implements Serializable {
  private final PypiInfo info;

  public PypiSearchResponse(final PypiInfo info) {
    this.info = info;
  }

  public PypiInfo getInfo() {
    return info;
  }

  @Override
  public String toString() {
    return "PyPiSearchResponse{" + "info=" + info + '}';
  }

  public static class PypiInfo implements Serializable {
    private final String name;
    private final String version;
    private final Boolean yanked;

    public PypiInfo(final String name, final String version, final boolean yanked) {
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
