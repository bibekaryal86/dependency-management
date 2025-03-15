package dep.mgmt.repository;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import dep.mgmt.model.ProcessSummaries;
import dep.mgmt.model.entity.ProcessSummaryEntity;
import dep.mgmt.util.ConstantUtils;
import dep.mgmt.util.ConvertUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.bson.conversions.Bson;

public class ProcessSummaryRepository extends MongoRepository<ProcessSummaryEntity> {
  public ProcessSummaryRepository(MongoDatabase database) {
    super(database, ConstantUtils.MONGODB_COLLECTION_PROCESS_SUMMARY, ProcessSummaryEntity.class);
  }

  // find by updateType with pagination
  public ProcessSummaries findAll(final int pageNumber, final int pageSize) {
    final Bson sort = Sorts.descending(ConstantUtils.MONGODB_COLUMN_UPDATE_DATETIME);
    final int totalItems = (int) collection.countDocuments();
    final int totalPages = (int) Math.ceil((double) totalItems / pageSize);

    final int pageNumberToUse = pageNumber > 0 ? pageNumber - 1 : 0;
    final int pageSizeToUse = pageSize < 10 || pageSize > 1000 ? 100 : pageSize;

    final List<ProcessSummaryEntity> processSummaryEntities =
        collection
            .find()
            .sort(sort)
            .skip(pageNumberToUse * pageSizeToUse)
            .limit(pageSizeToUse)
            .into(new ArrayList<>());
    final List<ProcessSummaries.ProcessSummary> processSummaries =
        ConvertUtils.convertProcessSummaryEntities(processSummaryEntities);
    return new ProcessSummaries(
        processSummaries, pageNumberToUse, totalPages, totalItems, pageSizeToUse);
  }

  public ProcessSummaries findByUpdateType(
      final String updateType, final int pageNumber, final int pageSize) {
    final Bson filter = Filters.eq(ConstantUtils.MONGODB_COLUMN_UPDATE_TYPE, updateType);
    final Bson sort = Sorts.descending(ConstantUtils.MONGODB_COLUMN_UPDATE_DATETIME);

    final int totalItems = (int) collection.countDocuments(filter);
    final int totalPages = (int) Math.ceil((double) totalItems / pageSize);

    final int pageNumberToUse = pageNumber > 0 ? pageNumber - 1 : 0;
    final int pageSizeToUse = pageSize < 10 || pageSize > 1000 ? 100 : pageSize;

    final List<ProcessSummaryEntity> processSummaryEntities =
        collection
            .find(filter)
            .sort(sort)
            .skip(pageNumberToUse * pageSizeToUse)
            .limit(pageSizeToUse)
            .into(new ArrayList<>());
    final List<ProcessSummaries.ProcessSummary> processSummaries =
        ConvertUtils.convertProcessSummaryEntities(processSummaryEntities);

    return new ProcessSummaries(
        processSummaries, pageNumberToUse, totalPages, totalItems, pageSizeToUse);
  }

  // find by updateDateTime
  public ProcessSummaries findByUpdateDate(
      final LocalDateTime startOfDay, final LocalDateTime endOfDay) {
    final Bson filter =
        Filters.and(
            Filters.gte(ConstantUtils.MONGODB_COLUMN_UPDATE_DATETIME, startOfDay),
            Filters.lt(ConstantUtils.MONGODB_COLUMN_UPDATE_DATETIME, endOfDay));
    final List<ProcessSummaryEntity> processSummaryEntities =
        collection.find(filter).into(new ArrayList<>());
    final List<ProcessSummaries.ProcessSummary> processSummaries =
        ConvertUtils.convertProcessSummaryEntities(processSummaryEntities);

    return new ProcessSummaries(processSummaries, -1, -1, -1, -1);
  }

  // find by updateType and updateDateTime
  public ProcessSummaries findByUpdateTypeAndUpdateDate(
      final String updateType, final LocalDateTime startOfDay, final LocalDateTime endOfDay) {
    final Bson filter =
        Filters.and(
            Filters.eq(ConstantUtils.MONGODB_COLUMN_UPDATE_TYPE, updateType),
            Filters.gte(ConstantUtils.MONGODB_COLUMN_UPDATE_DATETIME, startOfDay),
            Filters.lt(ConstantUtils.MONGODB_COLUMN_UPDATE_DATETIME, endOfDay));
    final List<ProcessSummaryEntity> processSummaryEntities =
        collection.find(filter).into(new ArrayList<>());
    final List<ProcessSummaries.ProcessSummary> processSummaries =
        ConvertUtils.convertProcessSummaryEntities(processSummaryEntities);

    return new ProcessSummaries(processSummaries, -1, -1, -1, -1);
  }

  // Delete all entities where updateDateTime is before the given date
  public void deleteByUpdateDateTimeBefore(final LocalDateTime date) {
    final Bson filter = Filters.lt(ConstantUtils.MONGODB_COLUMN_UPDATE_DATETIME, date);
    collection.deleteMany(filter);
  }
}
