package dep.mgmt.repository;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import dep.mgmt.model.entity.LogEntryEntity;
import dep.mgmt.util.ConstantUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.bson.conversions.Bson;

public class LogEntryRepository extends MongoRepository<LogEntryEntity> {
  public LogEntryRepository(MongoDatabase database) {
    super(database, ConstantUtils.MONGODB_COLLECTION_LOG_ENTRY, LogEntryEntity.class);
  }

  public List<LogEntryEntity> getLogEntriesByDate(final LocalDate logDate) {
    LocalDateTime logDateTimeStart = logDate.atStartOfDay();
    LocalDateTime logDateTimeEnd = logDate.plusDays(1).atStartOfDay();
    Bson filter =
        Filters.and(
            Filters.gte("updateDateTime", logDateTimeStart),
            Filters.lt("updateDateTime", logDateTimeEnd));
    return this.collection
        .find(filter)
        .sort(Sorts.descending(ConstantUtils.MONGODB_COLUMN_UPDATE_DATETIME))
        .into(new ArrayList<>());
  }
}
