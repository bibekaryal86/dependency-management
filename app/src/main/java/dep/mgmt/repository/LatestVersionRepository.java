package dep.mgmt.repository;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import dep.mgmt.model.entity.LatestVersionEntity;

public class LatestVersionRepository extends MongoRepository<LatestVersionEntity> {
  public LatestVersionRepository(MongoDatabase database) {
    super(database, "latest_version", LatestVersionEntity.class);
  }

  public LatestVersionEntity findFirstByOrderByUpdateDateTimeDesc() {
    return this.collection.find().sort(Sorts.descending("updateDateTime")).limit(1).first();
  }
}
