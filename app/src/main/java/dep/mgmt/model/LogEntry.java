package dep.mgmt.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.LocalDateTime;

public class LogEntry implements Serializable {
  private final LocalDateTime logDateTime;
  private final String logEntries;

  @JsonCreator
  public LogEntry(
      @JsonProperty("logDateTime") LocalDateTime logDateTime,
      @JsonProperty("logEntries") String logEntries) {
    this.logDateTime = logDateTime;
    this.logEntries = logEntries;
  }

  public LocalDateTime getLogDate() {
    return logDateTime;
  }

  public String getLogEntries() {
    return logEntries;
  }

  @Override
  public String toString() {
    return "LogEntry{" + "logDateTime=" + logDateTime + ", logEntries=" + logEntries + '}';
  }
}
