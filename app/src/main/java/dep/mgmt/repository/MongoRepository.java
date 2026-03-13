package dep.mgmt.repository;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import dep.mgmt.model.MongoQueryParams;
import dep.mgmt.util.ConstantUtils;
import java.time.LocalDateTime;
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
  public List<T> find(final MongoQueryParams params) {
    FindIterable<T> iterable = collection.find(params.getFilter());

    if (params.getSort() != null) {
      iterable = iterable.sort(params.getSort());
    }
    if (params.getSkip() > 0) {
      iterable = iterable.skip(params.getSkip());
    }
    if (params.getLimit() > 0) {
      iterable = iterable.limit(params.getLimit());
    }

    return iterable.into(new ArrayList<>());
  }

  public List<T> findAll() {
    return find(MongoQueryParams.builder().build());
  }

  public T findById(final ObjectId id) {
    return find(MongoQueryParams.builder().id(id).limit(1).build()).stream()
        .findFirst()
        .orElse(null);
  }

  public T findByAttribute(final String field, final Object value) {
    return find(MongoQueryParams.builder().eq(field, value).limit(1).build()).stream()
        .findFirst()
        .orElse(null);
  }

  public long count(final MongoQueryParams params) {
    return collection.countDocuments(params.getFilter());
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

  // UTILITIES
  public List<T> getUpdatedInPastDay() {
    final LocalDateTime end = LocalDateTime.now();
    final LocalDateTime start = end.minusHours(24L);
    return find(
        MongoQueryParams.builder()
            .dateRange(
                "lastUpdatedDate",
                start,
                MongoQueryParams.Bound.INCLUSIVE,
                end,
                MongoQueryParams.Bound.INCLUSIVE)
            .build());
  }
}
