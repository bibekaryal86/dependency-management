package dep.mgmt.repository;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import dep.mgmt.model.entity.LatestVersionEntity;
import dep.mgmt.util.ConstantUtils;
import java.time.LocalDateTime;
import org.bson.conversions.Bson;

public class LatestVersionRepository extends MongoRepository<LatestVersionEntity> {
  public LatestVersionRepository(MongoDatabase database) {
    super(database, ConstantUtils.MONGODB_COLLECTION_LATEST_VERSION, LatestVersionEntity.class);
  }

  public LatestVersionEntity findFirstByOrderByUpdateDateTimeDesc() {
    return this.collection
        .find()
        .sort(Sorts.descending(ConstantUtils.MONGODB_COLUMN_UPDATE_DATETIME))
        .limit(1)
        .first();
  }

  // Delete all entities where updateDateTime is before the given date
  public void deleteByUpdateDateTimeBefore(final LocalDateTime date) {
    final Bson filter = Filters.lt(ConstantUtils.MONGODB_COLUMN_UPDATE_DATETIME, date);
    collection.deleteMany(filter);
  }
}
