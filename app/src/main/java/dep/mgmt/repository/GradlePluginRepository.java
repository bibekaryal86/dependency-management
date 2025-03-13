package dep.mgmt.repository;

import com.mongodb.client.MongoDatabase;
import dep.mgmt.model.entity.DependencyEntity;
import dep.mgmt.util.ConstantUtils;

public class GradlePluginRepository extends MongoRepository<DependencyEntity> {
  public GradlePluginRepository(MongoDatabase database) {
    super(database, ConstantUtils.MONGODB_COLLECTION_GRADLE_PLUGIN, DependencyEntity.class);
  }
}
