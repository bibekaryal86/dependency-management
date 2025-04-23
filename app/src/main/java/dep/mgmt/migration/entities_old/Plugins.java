package dep.mgmt.migration.entities_old;

import java.io.Serializable;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

public class Plugins implements Serializable {
  @BsonId private ObjectId id;
  private String group;
  private String version;
  private boolean skipVersion;

  public Plugins() {}

  public Plugins(ObjectId id, String group, String version, boolean skipVersion) {
    this.id = id;
    this.group = group;
    this.version = version;
    this.skipVersion = skipVersion;
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
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
