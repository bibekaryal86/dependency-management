package dep.mgmt.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.bibekaryal86.shdsvc.dtos.ResponseMetadata;
import java.io.Serializable;
import java.util.Objects;

public class ResponseWithMetadata implements Serializable {
  private final ResponseMetadata responseMetadata;

  @JsonCreator
  public ResponseWithMetadata(@JsonProperty("responseMetadata") ResponseMetadata responseMetadata) {
    this.responseMetadata = responseMetadata;
  }

  public ResponseMetadata getResponseMetadata() {
    return responseMetadata;
  }

  @Override
  public String toString() {
    return "ResponseWithMetadata{" + "responseMetadata=" + responseMetadata + '}';
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (!(object instanceof ResponseWithMetadata that)) return false;
    return Objects.equals(responseMetadata, that.responseMetadata);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(responseMetadata);
  }
}
