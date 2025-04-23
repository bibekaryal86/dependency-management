package dep.mgmt.model.entity;

import java.io.Serializable;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

public class ExcludedRepoEntity implements Serializable {
  @BsonId private ObjectId id;
  private String name;

  public ExcludedRepoEntity() {}

  public ExcludedRepoEntity(final String name) {
    this.id = null;
    this.name = name;
  }

  public ExcludedRepoEntity(final ObjectId id, final String name) {
    this.id = id;
    this.name = name;
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "ExcludedRepo{" + "id=" + id + ", name='" + name + '}';
  }
}
