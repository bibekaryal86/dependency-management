package dep.mgmt.repository;

import com.mongodb.client.MongoDatabase;
import dep.mgmt.model.entity.DependencyEntity;

public class JavaPluginRepository extends MongoRepository<DependencyEntity> {
  public JavaPluginRepository(MongoDatabase database) {
    super(database, "java_plugin", DependencyEntity.class);
  }
}
