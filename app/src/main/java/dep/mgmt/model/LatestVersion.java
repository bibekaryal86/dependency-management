package dep.mgmt.model;

import java.io.Serializable;

public class LatestVersion implements Serializable {
  private final String versionActual;
  private final String versionFull;
  private final String versionMajor;
  private final String versionDocker;
  private final String versionGcp;

  public LatestVersion(
      final String versionActual,
      final String versionFull,
      final String versionMajor,
      final String versionDocker,
      final String versionGcp) {
    this.versionActual = versionActual;
    this.versionFull = versionFull;
    this.versionMajor = versionMajor;
    this.versionDocker = versionDocker;
    this.versionGcp = versionGcp;
  }

  public String getVersionActual() {
    return versionActual;
  }

  public String getVersionFull() {
    return versionFull;
  }

  public String getVersionMajor() {
    return versionMajor;
  }

  public String getVersionDocker() {
    return versionDocker;
  }

  public String getVersionGcp() {
    return versionGcp;
  }

  @Override
  public String toString() {
    return "LatestVersion{"
        + "versionActual='"
        + versionActual
        + '\''
        + ", versionFull='"
        + versionFull
        + '\''
        + ", versionMajor='"
        + versionMajor
        + '\''
        + ", versionDocker='"
        + versionDocker
        + '\''
        + ", versionGcp='"
        + versionGcp
        + '\''
        + '}';
  }
}
