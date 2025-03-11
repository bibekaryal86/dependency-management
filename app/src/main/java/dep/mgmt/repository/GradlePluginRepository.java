package dep.mgmt.repository;

import com.mongodb.client.MongoDatabase;
import dep.mgmt.model.entity.DependencyEntity;

public class GradlePluginRepository extends MongoRepository<DependencyEntity> {
  public GradlePluginRepository(MongoDatabase database) {
    super(database, "gradle_plugin", DependencyEntity.class);
  }
}
