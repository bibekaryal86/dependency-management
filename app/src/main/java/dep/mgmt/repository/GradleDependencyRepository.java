package dep.mgmt.repository;

import com.mongodb.client.MongoDatabase;
import dep.mgmt.model.entity.DependencyEntity;

public class GradleDependencyRepository extends MongoRepository<DependencyEntity> {
  public GradleDependencyRepository(MongoDatabase database) {
    super(database, "gradle_dependency", DependencyEntity.class);
  }
}
