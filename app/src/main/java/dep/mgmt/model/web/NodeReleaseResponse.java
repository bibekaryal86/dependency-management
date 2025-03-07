package dep.mgmt.model.web;

import java.io.Serializable;

public class NodeReleaseResponse implements Serializable {
  private final String version;
  private final String lts;

  public NodeReleaseResponse(final String version, final String lts) {
    this.version = version;
    this.lts = lts;
  }

  public String getVersion() {
    return version;
  }

  public String getLts() {
    return lts;
  }

  @Override
  public String toString() {
    return "NodeReleaseResponse{" + "version='" + version + '\'' + ", lts='" + lts + '\'' + '}';
  }
}
