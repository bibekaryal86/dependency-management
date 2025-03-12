package dep.mgmt.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;

public class MongoRepository<T> {
  protected final MongoCollection<T> collection;

  public MongoRepository(MongoDatabase database, String collectionName, Class<T> entityClass) {
    this.collection = database.getCollection(collectionName, entityClass);
  }

  // CREATE
  public void insert(T entity) {
    collection.insertOne(entity);
  }

  // READ
  public List<T> findAll() {
    return collection.find().into(new ArrayList<>());
  }

  public T findById(ObjectId id) {
    return collection.find(Filters.eq("_id", id)).first();
  }

  // UPDATE
  public void update(ObjectId id, T updatedEntity) {
    collection.replaceOne(Filters.eq("_id", id), updatedEntity);
  }

  // DELETE
  public void delete(ObjectId id) {
    collection.deleteOne(Filters.eq("_id", id));
  }
}
