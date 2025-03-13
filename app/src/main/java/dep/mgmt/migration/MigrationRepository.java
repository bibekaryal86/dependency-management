package dep.mgmt.migration;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;

public class MigrationRepository<T> {
  protected final MongoCollection<T> collection;

  public MigrationRepository(MongoDatabase database, String collectionName, Class<T> entityClass) {
    this.collection = database.getCollection(collectionName, entityClass);
  }

  public void insertAll(List<T> entities) {
    collection.insertMany(entities);
  }

  public List<T> findAll() {
    return collection.find().into(new ArrayList<>());
  }

  public void deleteAll() {
    collection.deleteMany(new Document()); // Empty filter deletes all documents
  }
}
