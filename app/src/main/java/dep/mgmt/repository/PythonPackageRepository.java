package dep.mgmt.repository;

import com.mongodb.client.MongoDatabase;
import dep.mgmt.model.entity.DependencyEntity;
import dep.mgmt.util.ConstantUtils;

public class PythonPackageRepository extends MongoRepository<DependencyEntity> {
  public PythonPackageRepository(MongoDatabase database) {
    super(database, ConstantUtils.MONGODB_COLLECTION_PYTHON_PACKAGE, DependencyEntity.class);
  }
}
