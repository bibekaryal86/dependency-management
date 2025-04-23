package dep.mgmt.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

public class LogEntryEntity implements Serializable {
  @BsonId private ObjectId id;
  private LocalDateTime updateDateTime;
  private String logEntries;

  public LogEntryEntity() {}

  public LogEntryEntity(ObjectId id, LocalDateTime updateDateTime, String logEntries) {
    this.id = id;
    this.updateDateTime = updateDateTime;
    this.logEntries = logEntries;
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public LocalDateTime getUpdateDateTime() {
    return updateDateTime;
  }

  public void setUpdateDateTime(LocalDateTime updateDateTime) {
    this.updateDateTime = updateDateTime;
  }

  public String getLogEntries() {
    return logEntries;
  }

  public void setLogEntries(String logEntries) {
    this.logEntries = logEntries;
  }

  @Override
  public String toString() {
    return "LogEntryEntity{"
        + "id="
        + id
        + ", updateDateTime="
        + updateDateTime
        + ", logEntries='"
        + logEntries
        + '\''
        + '}';
  }
}
