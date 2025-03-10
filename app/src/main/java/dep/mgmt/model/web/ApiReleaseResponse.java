package dep.mgmt.model.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiReleaseResponse implements Serializable {
  private final String tagName;
  private final String name;
  private final Boolean draft;
  private final Boolean prerelease;

  @JsonCreator
  public ApiReleaseResponse(
      @JsonProperty("tag_name") final String tagName,
      @JsonProperty("name") final String name,
      @JsonProperty("draft") final Boolean draft,
      @JsonProperty("prerelease") final Boolean prerelease) {
    this.tagName = tagName;
    this.name = name;
    this.draft = draft;
    this.prerelease = prerelease;
  }

  public String getTagName() {
    return tagName;
  }

  public String getName() {
    return name;
  }

  public Boolean getDraft() {
    return draft;
  }

  public Boolean getPrerelease() {
    return prerelease;
  }

  @Override
  public String toString() {
    return "ApiReleaseResponse{"
        + "tagName='"
        + tagName
        + '\''
        + ", name='"
        + name
        + '\''
        + ", draft="
        + draft
        + ", prerelease="
        + prerelease
        + '}';
  }
}
