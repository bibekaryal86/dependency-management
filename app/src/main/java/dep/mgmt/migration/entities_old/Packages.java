package dep.mgmt.migration.entities_old;

import java.io.Serializable;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

public class Packages implements Serializable {
  @BsonId private ObjectId id;
  private String name;
  private String version;
  private boolean skipVersion;

  public Packages() {}

  public Packages(ObjectId id, String name, String version, boolean skipVersion) {
    this.id = id;
    this.name = name;
    this.version = version;
    this.skipVersion = skipVersion;
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public boolean isSkipVersion() {
    return skipVersion;
  }

  public void setSkipVersion(boolean skipVersion) {
    this.skipVersion = skipVersion;
  }
}
