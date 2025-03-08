package dep.mgmt.model.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class MavenSearchResponse implements Serializable {
  private final MavenResponse response;

  @JsonCreator
  public MavenSearchResponse(@JsonProperty("response") final MavenResponse response) {
    this.response = response;
  }

  public MavenResponse getResponse() {
    return response;
  }

  @Override
  public String toString() {
    return "MavenSearchResponse{" + "response=" + response + '}';
  }

  public static class MavenResponse implements Serializable {
    private final List<MavenDoc> docs;

    @JsonCreator
    public MavenResponse(@JsonProperty("docs") final List<MavenDoc> docs) {
      this.docs = docs;
    }

    public List<MavenDoc> getDocs() {
      return docs;
    }

    @Override
    public String toString() {
      return "MavenResponse{" + "docs='" + docs + '\'' + '}';
    }

    public static class MavenDoc implements Serializable {
      private final String g;
      private final String a;
      private final String v;

      @JsonCreator
      public MavenDoc(@JsonProperty("g") final String g, @JsonProperty("a") final String a, @JsonProperty("v") final String v) {
        this.g = g;
        this.a = a;
        this.v = v;
      }

      public String getG() {
        return g;
      }

      public String getA() {
        return a;
      }

      public String getV() {
        return v;
      }

      @Override
      public String toString() {
        return "MavenDoc{" + "g='" + g + '\'' + ", a='" + a + '\'' + ", v='" + v + '\'' + '}';
      }
    }
  }
}
