package dep.mgmt.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import dep.mgmt.util.ConstantUtils;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;

public class MongoRepository<T> {
  protected final MongoCollection<T> collection;

  public MongoRepository(
      final MongoDatabase database, final String collectionName, final Class<T> entityClass) {
    this.collection = database.getCollection(collectionName, entityClass);
  }

  // CREATE
  public void insert(final T entity) {
    collection.insertOne(entity);
  }

  // READ
  public List<T> findAll() {
    return collection.find().into(new ArrayList<>());
  }

  public T findById(final ObjectId id) {
    return collection.find(Filters.eq(ConstantUtils.MONGODB_COLUMN_ID, id)).first();
  }

  // UPDATE
  public void update(final ObjectId id, final T updatedEntity) {
    collection.replaceOne(Filters.eq(ConstantUtils.MONGODB_COLUMN_ID, id), updatedEntity);
  }

  // DELETE
  public long delete(final ObjectId id) {
    return collection.deleteOne(Filters.eq(ConstantUtils.MONGODB_COLUMN_ID, id)).getDeletedCount();
  }

  public long delete(final String columnName, final Object columnValue) {
    return collection.deleteMany(Filters.eq(columnName, columnValue)).getDeletedCount();
  }

  public long deleteAll() {
    return collection.deleteMany(Filters.empty()).getDeletedCount();
  }
}
