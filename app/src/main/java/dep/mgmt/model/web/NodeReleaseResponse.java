package dep.mgmt.model.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class NodeReleaseResponse implements Serializable {
  private final String version;
  private final String lts;

  @JsonCreator
  public NodeReleaseResponse(
      @JsonProperty("version") final String version, @JsonProperty("lts") final String lts) {
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
