package dep.mgmt.model.web;

import java.io.Serializable;
import java.util.List;

public class MavenSearchResponse implements Serializable {
  private final MavenResponse response;

  public MavenSearchResponse(final MavenResponse response) {
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

    public MavenResponse(final List<MavenDoc> docs) {
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

      public MavenDoc(final String g, final String a, final String v) {
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
