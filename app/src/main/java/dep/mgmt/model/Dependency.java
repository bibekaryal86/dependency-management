package dep.mgmt.model;

import java.io.Serializable;

public class Dependency implements Serializable {
  private final String name;
  private final String version;
  private final Boolean skipVersion;

  public Dependency(final String name, final String version, final Boolean skipVersion) {
    this.name = name;
    this.version = version;
    this.skipVersion = skipVersion;
  }

  public Dependency(final String name, final String version) {
    this.name = name;
    this.version = version;
    this.skipVersion = false;
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
