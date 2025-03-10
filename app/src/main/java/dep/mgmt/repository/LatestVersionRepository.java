package dep.mgmt.repository;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import dep.mgmt.model.entity.LatestVersionEntity;
import java.util.Optional;

public class LatestVersionRepository extends MongoRepository<LatestVersionEntity> {
  public LatestVersionRepository(MongoDatabase database) {
    super(database, "latest_version", LatestVersionEntity.class);
  }

  public Optional<LatestVersionEntity> findFirstByOrderByUpdateDateTimeDesc() {
    return Optional.ofNullable(
        this.collection.find().sort(Sorts.descending("updateDateTime")).limit(1).first());
  }
}
