package dep.mgmt.repository;

import com.mongodb.client.MongoDatabase;
import dep.mgmt.model.entity.DependencyEntity;

public class NpmDependencyRepository extends MongoRepository<DependencyEntity> {
  public NpmDependencyRepository(MongoDatabase database) {
    super(database, "npm_dependency", DependencyEntity.class);
  }
}
