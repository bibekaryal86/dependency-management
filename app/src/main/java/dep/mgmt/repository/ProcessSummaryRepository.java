package dep.mgmt.repository;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import dep.mgmt.model.entity.ProcessSummaryEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.bson.conversions.Bson;

public class ProcessSummaryRepository extends MongoRepository<ProcessSummaryEntity> {
  public ProcessSummaryRepository(MongoDatabase database) {
    super(database, "process_summary", ProcessSummaryEntity.class);
  }

  // find by updateType with pagination
  public List<ProcessSummaryEntity> findByUpdateType(
      String updateType, int pageNumber, int pageSize) {
    Bson filter = Filters.eq("updateType", updateType);
    Bson sort = Sorts.descending("updateDateTime");

    return collection
        .find(filter)
        .sort(sort)
        .skip(pageNumber * pageSize)
        .limit(pageSize)
        .into(new ArrayList<>());
  }

  // find by updateType and updateDateTime
  public List<ProcessSummaryEntity> findByUpdateDate(
      LocalDateTime startOfDay, LocalDateTime endOfDay) {
    Bson filter =
        Filters.and(
            Filters.gte("updateDateTime", startOfDay), Filters.lt("updateDateTime", endOfDay));
    return collection.find(filter).into(new ArrayList<>());
  }

  // find by updateType and updateDateTime
  public List<ProcessSummaryEntity> findByUpdateTypeAndUpdateDate(
      String updateType, LocalDateTime startOfDay, LocalDateTime endOfDay) {
    Bson filter =
        Filters.and(
            Filters.eq("updateType", updateType),
            Filters.gte("updateDateTime", startOfDay),
            Filters.lt("updateDateTime", endOfDay));
    return collection.find(filter).into(new ArrayList<>());
  }

  // Delete all entities where updateDateTime is before the given date
  public void deleteByUpdateDateTimeBefore(LocalDateTime date) {
    Bson filter = Filters.lt("updateDateTime", date);
    collection.deleteMany(filter);
  }
}
