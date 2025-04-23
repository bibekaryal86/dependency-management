package dep.mgmt.repository;

import com.mongodb.client.MongoDatabase;
import dep.mgmt.model.entity.ExcludedRepoEntity;
import dep.mgmt.util.ConstantUtils;

public class ExcludedRepoRepository extends MongoRepository<ExcludedRepoEntity> {
  public ExcludedRepoRepository(MongoDatabase database) {
    super(database, ConstantUtils.MONGODB_COLLECTION_EXCLUDED_REPO, ExcludedRepoEntity.class);
  }
}
