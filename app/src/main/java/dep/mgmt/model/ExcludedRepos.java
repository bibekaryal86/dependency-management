package dep.mgmt.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;

public class ExcludedRepos implements Serializable {
  private final List<ExcludedRepo> excludedRepos;

  @JsonCreator
  public ExcludedRepos(@JsonProperty("excludedRepos") List<ExcludedRepo> excludedRepos) {
    this.excludedRepos = excludedRepos;
  }

  public List<ExcludedRepo> getExcludedRepos() {
    return excludedRepos;
  }

  @Override
  public String toString() {
    return "ExcludedRepos{" + "excludedRepos=" + excludedRepos + '}';
  }

  public static class ExcludedRepo implements Serializable {
    private final String name;

    @JsonCreator
    public ExcludedRepo(@JsonProperty("name") final String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    @Override
    public String toString() {
      return "ExcludedRepo{" + "name=" + name + '}';
    }
  }
}
