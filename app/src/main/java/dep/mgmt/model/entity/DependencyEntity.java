package dep.mgmt.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

public class DependencyEntity implements Serializable {
  @BsonId private ObjectId id;
  private String name;
  private String version;
  private Boolean skipVersion;
  private LocalDateTime lastCheckedDate;
  private LocalDateTime lastUpdatedDate;

  public DependencyEntity() {}

  public DependencyEntity(
      final ObjectId id,
      final String name,
      final String version,
      final Boolean skipVersion,
      final LocalDateTime lastCheckedDate,
      final LocalDateTime lastUpdatedDate) {
    this.id = id;
    this.name = name;
    this.version = version;
    this.skipVersion = skipVersion;
    this.lastCheckedDate = lastCheckedDate;
    this.lastUpdatedDate = lastUpdatedDate;
  }

  public DependencyEntity(
      final ObjectId id, final String name, final String version, final Boolean skipVersion) {
    this.id = id;
    this.name = name;
    this.version = version;
    this.skipVersion = skipVersion;
    this.lastCheckedDate = LocalDateTime.now();
    this.lastUpdatedDate = LocalDateTime.now();
  }

  public DependencyEntity(
      final ObjectId id,
      final String name,
      final String version,
      final Boolean skipVersion,
      final LocalDateTime lastUpdatedDate) {
    this.id = id;
    this.name = name;
    this.version = version;
    this.skipVersion = skipVersion;
    this.lastCheckedDate = LocalDateTime.now();
    this.lastUpdatedDate = lastUpdatedDate;
  }

  public DependencyEntity(final String name, final String version) {
    this.id = null;
    this.name = name;
    this.version = version;
    this.skipVersion = false;
    this.lastCheckedDate = LocalDateTime.now();
    this.lastUpdatedDate = LocalDateTime.now();
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(final ObjectId id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(final String version) {
    this.version = version;
  }

  public Boolean getSkipVersion() {
    return skipVersion;
  }

  public void setSkipVersion(final Boolean skipVersion) {
    this.skipVersion = skipVersion;
  }

  public LocalDateTime getLastCheckedDate() {
    return lastCheckedDate;
  }

  public void setLastCheckedDate(final LocalDateTime lastCheckedDate) {
    this.lastCheckedDate = lastCheckedDate;
  }

  public LocalDateTime getLastUpdatedDate() {
    return lastUpdatedDate;
  }

  public void setLastUpdatedDate(final LocalDateTime lastUpdatedDate) {
    this.lastUpdatedDate = lastUpdatedDate;
  }

  @Override
  public String toString() {
    return "DependencyEntity{"
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
        + ", lastCheckedDate="
        + lastCheckedDate
        + ", lastUpdatedDate="
        + lastUpdatedDate
        + '}';
  }
}
