package dep.mgmt.model.entity;

import java.io.Serializable;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

public class DependencyEntity implements Serializable {
  @BsonId private final ObjectId id;
  private final String name;
  private final String version;
  private final Boolean skipVersion;

  public DependencyEntity(
      final ObjectId id, final String name, final String version, final Boolean skipVersion) {
    this.id = id;
    this.name = name;
    this.version = version;
    this.skipVersion = skipVersion;
  }

  public DependencyEntity(final String name, final String version, final Boolean skipVersion) {
    this.id = null;
    this.name = name;
    this.version = version;
    this.skipVersion = skipVersion;
  }

  public DependencyEntity(final String name, final String version) {
    this.id = null;
    this.name = name;
    this.version = version;
    this.skipVersion = false;
  }

  public ObjectId getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getVersion() {
    return version;
  }

  public Boolean getSkipVersion() {
    return skipVersion;
  }

  @Override
  public String toString() {
    return "Dependency{"
        + "id="
        + id
        + ", name='"
        + name
        + '\''
        + ", version='"
        + version
        + '\''
        + ", skipVersion="
        + skipVersion
        + '}';
  }
}
