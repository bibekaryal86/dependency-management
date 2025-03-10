package dep.mgmt.repository;

import com.mongodb.client.MongoDatabase;
import dep.mgmt.model.entity.DependencyEntity;

public class NodeDependencyRepository extends MongoRepository<DependencyEntity> {
  public NodeDependencyRepository(MongoDatabase database) {
    super(database, "node_dependency", DependencyEntity.class);
  }
}
