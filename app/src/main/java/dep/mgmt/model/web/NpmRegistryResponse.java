package dep.mgmt.model.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class NpmRegistryResponse implements Serializable {
  private final DistTags distTags;

  public DistTags getDistTags() {
    return distTags;
  }

  @JsonCreator
  public NpmRegistryResponse(@JsonProperty("dist-tags") final DistTags distTags) {
    this.distTags = distTags;
  }

  @Override
  public String toString() {
    return "NpmRegistryResponse{" + "distTags='" + distTags + '\'' + '}';
  }

  public static class DistTags implements Serializable {
    private final String latest;

    @JsonCreator
    public DistTags(@JsonProperty("latest") final String latest) {
      this.latest = latest;
    }

    public String getLatest() {
      return latest;
    }

    @Override
    public String toString() {
      return "DistTags{" + "latest='" + latest + '\'' + '}';
    }
  }
}
