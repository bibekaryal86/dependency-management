package dep.mgmt.repository;

import com.mongodb.client.MongoDatabase;
import dep.mgmt.model.entity.DependencyEntity;

public class JavaDependencyRepository extends MongoRepository<DependencyEntity> {
  public JavaDependencyRepository(MongoDatabase database) {
    super(database, "java_dependency", DependencyEntity.class);
  }
}
