package dep.mgmt.repository;

import com.mongodb.client.MongoDatabase;
import dep.mgmt.model.entity.DependencyEntity;
import dep.mgmt.util.ConstantUtils;

public class NpmDependencyRepository extends MongoRepository<DependencyEntity> {
  public NpmDependencyRepository(MongoDatabase database) {
    super(database, ConstantUtils.MONGODB_COLLECTION_NPM_DEPENDENCY, DependencyEntity.class);
  }
}
