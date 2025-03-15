package dep.mgmt.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class DependencyResponse implements Serializable {
  private final List<Dependency> dependencies;

  @JsonCreator
  public DependencyResponse(@JsonProperty("dependencies") final List<Dependency> dependencies) {
    this.dependencies = dependencies;
  }

  public List<Dependency> getDependencies() {
    return dependencies;
  }

  @Override
  public String toString() {
    return "DependencyResponse{"
        + "dependencies='"
        + dependencies.size()
        + '}';
  }
}
