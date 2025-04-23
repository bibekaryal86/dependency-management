package dep.mgmt.migration.entities_old;

import java.io.Serializable;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

public class Dependencies implements Serializable {
  @BsonId private ObjectId id;
  private String mavenId;
  private String latestVersion;
  private boolean skipVersion;

  public Dependencies() {}

  public Dependencies(ObjectId id, String mavenId, String latestVersion, boolean skipVersion) {
    this.id = id;
    this.mavenId = mavenId;
    this.latestVersion = latestVersion;
    this.skipVersion = skipVersion;
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public String getMavenId() {
    return mavenId;
  }

  public void setMavenId(String mavenId) {
    this.mavenId = mavenId;
  }

  public String getLatestVersion() {
    return latestVersion;
  }

  public void setLatestVersion(String latestVersion) {
    this.latestVersion = latestVersion;
  }

  public boolean isSkipVersion() {
    return skipVersion;
  }

  public void setSkipVersion(boolean skipVersion) {
    this.skipVersion = skipVersion;
  }
}
