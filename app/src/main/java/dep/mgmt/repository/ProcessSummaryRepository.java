package dep.mgmt.repository;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import dep.mgmt.model.ProcessSummary;
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
  public ProcessSummary findAll(final int pageNumber, final int pageSize) {
    final Bson sort = Sorts.descending("updateDateTime");
    final int totalItems = (int) collection.countDocuments();
    final int totalPages = (int) Math.ceil((double) totalItems / pageSize);

    final int pageNumberToUse = pageNumber > 0 ? pageNumber - 1 : 0;
    final int pageSizeToUse = pageSize < 10 || pageSize > 1000 ? 100 : pageSize;

    final List<ProcessSummaryEntity> processSummaries =
        collection
            .find()
            .sort(sort)
            .skip(pageNumberToUse * pageSizeToUse)
            .limit(pageSizeToUse)
            .into(new ArrayList<>());

    return new ProcessSummary(
        processSummaries, pageNumberToUse, totalPages, totalItems, pageSizeToUse);
  }

  public ProcessSummary findByUpdateType(
      final String updateType, final int pageNumber, final int pageSize) {
    final Bson filter = Filters.eq("updateType", updateType);
    final Bson sort = Sorts.descending("updateDateTime");

    final int totalItems = (int) collection.countDocuments(filter);
    final int totalPages = (int) Math.ceil((double) totalItems / pageSize);

    final int pageNumberToUse = pageNumber > 0 ? pageNumber - 1 : 0;
    final int pageSizeToUse = pageSize < 10 || pageSize > 1000 ? 100 : pageSize;

    final List<ProcessSummaryEntity> processSummaries =
        collection
            .find(filter)
            .sort(sort)
            .skip(pageNumberToUse * pageSizeToUse)
            .limit(pageSizeToUse)
            .into(new ArrayList<>());

    return new ProcessSummary(
        processSummaries, pageNumberToUse, totalPages, totalItems, pageSizeToUse);
  }

  // find by updateType and updateDateTime
  public List<ProcessSummaryEntity> findByUpdateDate(
      final LocalDateTime startOfDay, final LocalDateTime endOfDay) {
    final Bson filter =
        Filters.and(
            Filters.gte("updateDateTime", startOfDay), Filters.lt("updateDateTime", endOfDay));
    return collection.find(filter).into(new ArrayList<>());
  }

  // find by updateType and updateDateTime
  public List<ProcessSummaryEntity> findByUpdateTypeAndUpdateDate(
      final String updateType, final LocalDateTime startOfDay, final LocalDateTime endOfDay) {
    final Bson filter =
        Filters.and(
            Filters.eq("updateType", updateType),
            Filters.gte("updateDateTime", startOfDay),
            Filters.lt("updateDateTime", endOfDay));
    return collection.find(filter).into(new ArrayList<>());
  }

  // Delete all entities where updateDateTime is before the given date
  public void deleteByUpdateDateTimeBefore(final LocalDateTime date) {
    final Bson filter = Filters.lt("updateDateTime", date);
    collection.deleteMany(filter);
  }
}
