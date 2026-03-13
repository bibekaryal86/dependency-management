package dep.mgmt.model;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import dep.mgmt.util.ConstantUtils;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

public class MongoQueryParams {

  final Bson filter;
  final Bson sort;

  final int skip;
  final int limit;

  public Bson getFilter() {
    return filter;
  }

  public Bson getSort() {
    return sort;
  }

  public int getSkip() {
    return skip;
  }

  public int getLimit() {
    return limit;
  }

  private MongoQueryParams(Builder builder) {
    this.filter =
        builder.filters.isEmpty()
            ? new Document() // empty filter = match all
            : Filters.and(builder.filters);
    this.sort = builder.sort;
    this.skip = builder.skip;
    this.limit = builder.limit;
  }

  public static Builder builder() {
    return new Builder();
  }

  public enum Bound {
    INCLUSIVE,
    EXCLUSIVE
  }

  public static final class Builder {
    private final List<Bson> filters = new ArrayList<>();
    private Bson sort = null;
    private int skip = 0;
    private int limit = 0;

    private Builder() {}

    public Builder eq(final String field, final Object value) {
      filters.add(Filters.eq(field, value));
      return this;
    }

    public Builder id(final ObjectId id) {
      return eq(ConstantUtils.MONGODB_COLUMN_ID, id);
    }

    public Builder dateRange(
        final String field,
        final LocalDateTime start,
        final Bound startBound,
        final LocalDateTime end,
        final Bound endBound) {
      filters.add(
          startBound == Bound.INCLUSIVE
              ? Filters.gte(field, toDate(start))
              : Filters.gt(field, toDate(start)));
      filters.add(
          endBound == Bound.INCLUSIVE
              ? Filters.lte(field, toDate(end))
              : Filters.lt(field, toDate(end)));
      return this;
    }

    public Builder filter(final Bson bsonFilter) {
      filters.add(bsonFilter);
      return this;
    }

    public Builder sortAsc(final String field) {
      this.sort = Sorts.ascending(field);
      return this;
    }

    public Builder sortDesc(final String field) {
      this.sort = Sorts.descending(field);
      return this;
    }

    public Builder sort(final Bson bsonSort) {
      this.sort = bsonSort;
      return this;
    }

    public Builder skipAndLimit(final int skip, final int limit) {
      this.skip = Math.max(skip, 0);
      this.limit = limit;
      return this;
    }

    public Builder page(final int pageNumber, final int pageSize) {
      final int safePage = Math.max(pageNumber - 1, 0);
      final int safeSize = (pageSize < 10 || pageSize > 1000) ? 100 : pageSize;
      return skipAndLimit(safePage * safeSize, safeSize);
    }

    public Builder limit(final int limit) {
      this.limit = limit;
      return this;
    }

    public MongoQueryParams build() {
      return new MongoQueryParams(this);
    }

    private static Date toDate(final LocalDateTime ldt) {
      return Date.from(ldt.toInstant(ZoneOffset.UTC));
    }
  }
}
