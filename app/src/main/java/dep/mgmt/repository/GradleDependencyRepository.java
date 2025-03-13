package dep.mgmt.repository;

import com.mongodb.client.MongoDatabase;
import dep.mgmt.model.entity.DependencyEntity;
import dep.mgmt.util.ConstantUtils;

public class GradleDependencyRepository extends MongoRepository<DependencyEntity> {
  public GradleDependencyRepository(MongoDatabase database) {
    super(database, ConstantUtils.MONGODB_COLLECTION_GRADLE_DEPENDENCY, DependencyEntity.class);
  }
}
