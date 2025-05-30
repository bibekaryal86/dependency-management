package dep.mgmt.model.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class GithubApiModel implements Serializable {

  public static class ListBranchesResponse implements Serializable {
    private final String name;

    @JsonCreator
    public ListBranchesResponse(@JsonProperty("name") final String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    @Override
    public String toString() {
      return "ListBranchesResponse{" + "name='" + name + '\'' + '}';
    }
  }

  public static class CreatePullRequest implements Serializable {
    private final String title;
    private final String body;
    private final String head;
    private final String base;

    @JsonCreator
    public CreatePullRequest(
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
      return "GithubApiModel.CreatePullRequest{"
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

  public static class CreatePullRequestResponse implements Serializable {
    private final Integer number;

    @JsonCreator
    public CreatePullRequestResponse(@JsonProperty("number") Integer number) {
      this.number = number;
    }

    public Integer getNumber() {
      return number;
    }

    @Override
    public String toString() {
      return "GithubApiModel.CreatePullRequestResponse{" + "number=" + number + '}';
    }
  }

  public static class ListPullRequestsResponse implements Serializable {
    private final Integer number;
    private final String state;

    @JsonCreator
    public ListPullRequestsResponse(
        @JsonProperty("number") Integer number, @JsonProperty("state") String state) {
      this.number = number;
      this.state = state;
    }

    public Integer getNumber() {
      return number;
    }

    public String getState() {
      return state;
    }

    @Override
    public String toString() {
      return "ListPullRequestsResponse{" + "number=" + number + ", state='" + state + '\'' + '}';
    }
  }

  public static class MergePullRequestRequest implements Serializable {
    private final String commitTitle;
    private final String commitMessage;
    private final String mergeMethod;

    @JsonCreator
    public MergePullRequestRequest(
        @JsonProperty("commit_title") String commitTitle,
        @JsonProperty("commit_message") String commitMessage,
        @JsonProperty("merge_method") String mergeMethod) {
      this.commitTitle = commitTitle;
      this.commitMessage = commitMessage;
      this.mergeMethod = mergeMethod;
    }

    public String getCommitTitle() {
      return commitTitle;
    }

    public String getCommitMessage() {
      return commitMessage;
    }

    public String getMergeMethod() {
      return mergeMethod;
    }

    @Override
    public String toString() {
      return "MergePullRequestRequest{"
          + "commitTitle='"
          + commitTitle
          + '\''
          + ", commitMessage='"
          + commitMessage
          + '\''
          + ", mergeMethod='"
          + mergeMethod
          + '\''
          + '}';
    }
  }

  public static class MergePullRequestResponse implements Serializable {
    private final Boolean merged;

    @JsonCreator
    public MergePullRequestResponse(@JsonProperty("merged") Boolean merged) {
      this.merged = merged;
    }

    public Boolean getMerged() {
      return merged;
    }

    @Override
    public String toString() {
      return "GithubApiModel.MergePullRequestResponse{" + "merged=" + merged + '}';
    }
  }

  public static class ListWorkflowRunsResponse implements Serializable {
    private final List<WorkflowRun> workflowRuns;

    @JsonCreator
    public ListWorkflowRunsResponse(@JsonProperty("workflow_runs") List<WorkflowRun> workflowRuns) {
      this.workflowRuns = workflowRuns;
    }

    public List<WorkflowRun> getWorkflowRuns() {
      return workflowRuns;
    }

    @Override
    public String toString() {
      return "ListWorkflowRunsResponse{" + "workflowRuns=" + workflowRuns + '}';
    }

    public static class WorkflowRun implements Serializable {
      private final String headBranch;
      private final String event;
      private final String status;
      private final String conclusion;
      // list only populated if an open pull request exists
      private final List<CreatePullRequestResponse> pullRequests;

      @JsonCreator
      public WorkflowRun(
          @JsonProperty("head_branch") String headBranch,
          @JsonProperty("event") String event,
          @JsonProperty("status") String status,
          @JsonProperty("conclusion") String conclusion,
          @JsonProperty("pull_requests") List<CreatePullRequestResponse> pullRequests) {
        this.headBranch = headBranch;
        this.event = event;
        this.status = status;
        this.conclusion = conclusion;
        this.pullRequests = pullRequests;
      }

      public String getHeadBranch() {
        return headBranch;
      }

      public String getEvent() {
        return event;
      }

      public String getStatus() {
        return status;
      }

      public String getConclusion() {
        return conclusion;
      }

      public List<CreatePullRequestResponse> getPullRequests() {
        return pullRequests;
      }

      @Override
      public String toString() {
        return "WorkflowRun{"
            + "headBranch='"
            + headBranch
            + '\''
            + ", event='"
            + event
            + '\''
            + ", status='"
            + status
            + '\''
            + ", conclusion='"
            + conclusion
            + '\''
            + ", pullRequests="
            + pullRequests
            + '}';
      }
    }
  }

  public static class RateLimitResponse implements Serializable {
    private final Resources resources;
    private final Rate rate;

    @JsonCreator
    public RateLimitResponse(
        @JsonProperty("resources") Resources resources, @JsonProperty("rate") Rate rate) {
      this.resources = resources;
      this.rate = rate;
    }

    public Resources getResources() {
      return resources;
    }

    public Rate getRate() {
      return rate;
    }

    @Override
    public String toString() {
      return "RateLimitResponse{" + "resources=" + resources + ", rate=" + rate + '}';
    }

    public static class Resources implements Serializable {
      private final Rate core;
      private final Rate graphql;

      @JsonCreator
      public Resources(@JsonProperty("core") Rate core, @JsonProperty("graphql") Rate graphql) {
        this.core = core;
        this.graphql = graphql;
      }

      public Rate getCore() {
        return core;
      }

      public Rate getGraphql() {
        return graphql;
      }

      @Override
      public String toString() {
        return "Resource{" + "core=" + core + ", graphql=" + graphql + '}';
      }
    }

    public static class Rate implements Serializable {
      private final Integer limit;
      private final Integer used;
      private final Integer remaining;
      private final Integer reset;
      private final LocalDateTime resetAt;
      private final Long resetAfterInMinutes;

      @JsonCreator
      public Rate(
          @JsonProperty("limit") Integer limit,
          @JsonProperty("used") Integer used,
          @JsonProperty("remaining") Integer remaining,
          @JsonProperty("reset") Integer reset) {
        this.limit = limit;
        this.used = used;
        this.remaining = remaining;
        this.reset = reset;
        this.resetAt =
            LocalDateTime.ofInstant(Instant.ofEpochSecond(reset), ZoneId.systemDefault());
        this.resetAfterInMinutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), this.resetAt);
      }

      public Integer getLimit() {
        return limit;
      }

      public Integer getUsed() {
        return used;
      }

      public Integer getRemaining() {
        return remaining;
      }

      public Integer getReset() {
        return reset;
      }

      public LocalDateTime getResetAt() {
        return resetAt;
      }

      public Long getResetAfterInMinutes() {
        return resetAfterInMinutes;
      }

      @Override
      public String toString() {
        return "Rate{"
            + "limit="
            + limit
            + ", used="
            + used
            + ", remaining="
            + remaining
            + ", reset="
            + reset
            + ", resetAt="
            + resetAt
            + ", resetAfterInMinutes="
            + resetAfterInMinutes
            + '}';
      }
    }
  }
}
