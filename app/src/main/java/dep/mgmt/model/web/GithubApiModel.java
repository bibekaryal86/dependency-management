package dep.mgmt.model.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
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
      return "ListBranchesResponse{" +
              "name='" + name + '\'' +
              '}';
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
    private final String mergeMethod;

    @JsonCreator
    public MergePullRequestRequest(@JsonProperty("merge_method") String mergeMethod) {
      this.mergeMethod = mergeMethod;
    }

    public String getMergeMethod() {
      return mergeMethod;
    }

    @Override
    public String toString() {
      return "GithubApiModel.MergePullRequestRequest{" + "mergeMethod='" + mergeMethod + '\'' + '}';
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

      @JsonCreator
      public WorkflowRun(
          @JsonProperty("head_branch") String headBranch,
          @JsonProperty("event") String event,
          @JsonProperty("status") String status,
          @JsonProperty("conclusion") String conclusion) {
        this.headBranch = headBranch;
        this.event = event;
        this.status = status;
        this.conclusion = conclusion;
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
            + '}';
      }
    }
  }
}
