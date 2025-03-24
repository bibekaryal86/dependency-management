package dep.mgmt.model.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class GithubPullRequestApi implements Serializable {
  public static class CreateRequest implements Serializable {
    private final String title;
    private final String body;
    private final String head;
    private final String base;

    @JsonCreator
    public CreateRequest(
        @JsonProperty("title") String title,
        @JsonProperty("body") String body,
        @JsonProperty("head") String head,
        @JsonProperty("base") String base) {
      this.title = title;
      this.body = body;
      this.head = head;
      this.base = base;
    }

    public String getTitle() {
      return title;
    }

    public String getBody() {
      return body;
    }

    public String getHead() {
      return head;
    }

    public String getBase() {
      return base;
    }

    @Override
    public String toString() {
      return "GithubApiPullRequest.CreateRequest{"
          + "title='"
          + title
          + '\''
          + ", body='"
          + body
          + '\''
          + ", head='"
          + head
          + '\''
          + ", base='"
          + base
          + '\''
          + '}';
    }
  }

  public static class CreateResponse implements Serializable {
    private final Integer number;

    @JsonCreator
    public CreateResponse(@JsonProperty("number") Integer number) {
      this.number = number;
    }

    public Integer getNumber() {
      return number;
    }

    @Override
    public String toString() {
      return "GithubApiPullRequest.CreateResponse{" + "number=" + number + '}';
    }
  }

  public static class MergeRequest implements Serializable {
    private final String mergeMethod;

    @JsonCreator
    public MergeRequest(@JsonProperty("merge_method") String mergeMethod) {
      this.mergeMethod = mergeMethod;
    }

    public String getMergeMethod() {
      return mergeMethod;
    }

    @Override
    public String toString() {
      return "GithubApiPullRequest.MergeRequest{" + "mergeMethod='" + mergeMethod + '\'' + '}';
    }
  }

  public static class MergeResponse implements Serializable {
    private final Boolean merged;

    @JsonCreator
    public MergeResponse(@JsonProperty("merged") Boolean merged) {
      this.merged = merged;
    }

    public Boolean getMerged() {
      return merged;
    }

    @Override
    public String toString() {
      return "GithubApiPullRequest.MergeResponse{" + "merged=" + merged + '}';
    }
  }
}
