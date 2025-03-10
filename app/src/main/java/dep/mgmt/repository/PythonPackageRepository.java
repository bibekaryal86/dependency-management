package dep.mgmt.repository;

import com.mongodb.client.MongoDatabase;
import dep.mgmt.model.entity.DependencyEntity;

public class PythonPackageRepository extends MongoRepository<DependencyEntity> {
  public PythonPackageRepository(MongoDatabase database) {
    super(database, "python_package", DependencyEntity.class);
  }
}
