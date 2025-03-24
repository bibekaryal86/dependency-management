package dep.mgmt.repository;

import com.mongodb.client.MongoDatabase;
import dep.mgmt.model.entity.DependencyEntity;
import dep.mgmt.util.ConstantUtils;

public class NodeDependencyRepository extends MongoRepository<DependencyEntity> {
  public NodeDependencyRepository(MongoDatabase database) {
    super(database, ConstantUtils.MONGODB_COLLECTION_NODE_DEPENDENCY, DependencyEntity.class);
  }
}
