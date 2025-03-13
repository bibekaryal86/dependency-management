package dep.mgmt.migration.entities_old;

import java.io.Serializable;

public class LatestVersion implements Serializable {
  private String versionActual;
  private String versionFull;
  private String versionMajor;
  private String versionDocker;
  private String versionGcp;

  public LatestVersion() {}

  public LatestVersion(
      String versionActual,
      String versionFull,
      String versionMajor,
      String versionDocker,
      String versionGcp) {
    this.versionActual = versionActual;
    this.versionFull = versionFull;
    this.versionMajor = versionMajor;
    this.versionDocker = versionDocker;
    this.versionGcp = versionGcp;
  }

  public String getVersionActual() {
    return versionActual;
  }

  public void setVersionActual(String versionActual) {
    this.versionActual = versionActual;
  }

  public String getVersionFull() {
    return versionFull;
  }

  public void setVersionFull(String versionFull) {
    this.versionFull = versionFull;
  }

  public String getVersionMajor() {
    return versionMajor;
  }

  public void setVersionMajor(String versionMajor) {
    this.versionMajor = versionMajor;
  }

  public String getVersionDocker() {
    return versionDocker;
  }

  public void setVersionDocker(String versionDocker) {
    this.versionDocker = versionDocker;
  }

  public String getVersionGcp() {
    return versionGcp;
  }

  public void setVersionGcp(String versionGcp) {
    this.versionGcp = versionGcp;
  }
}
