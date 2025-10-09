package dep.mgmt.controller;

import dep.mgmt.config.ScheduleConfig;
import dep.mgmt.model.ProcessSummaries;
import dep.mgmt.model.web.GithubApiModel;
import dep.mgmt.server.Endpoints;
import dep.mgmt.service.GithubService;
import dep.mgmt.service.UpdateRepoService;
import dep.mgmt.util.ConstantUtils;
import dep.mgmt.util.ServerUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class AppTestController {

  private final UpdateRepoService updateRepoService;
  private final GithubService githubService;

  public AppTestController() {
    this.updateRepoService = new UpdateRepoService();
    this.githubService = new GithubService();
  }

  public void handleRequest(
      final FullHttpRequest fullHttpRequest, final ChannelHandlerContext ctx) {
    final String requestUriLessParams = ServerUtils.getRequestUriLessParams(fullHttpRequest.uri());

    switch (requestUriLessParams) {
      case Endpoints.APP_TESTS_PING ->
          ServerUtils.sendResponse(
              ctx,
              null,
              HttpResponseStatus.OK,
              String.format(ConstantUtils.JSON_RESPONSE, "ping", "successful"));
      case Endpoints.APP_TESTS_RESET -> {
        ServerUtils.sendResponse(
            ctx,
            null,
            HttpResponseStatus.OK,
            String.format(ConstantUtils.JSON_RESPONSE, "request", "submitted"));
        updateRepoService.recreateLocalCaches(Boolean.TRUE);
      }
      case Endpoints.APP_TESTS_RATE -> {
        GithubApiModel.RateLimitResponse rateLimitResponse =
            githubService.getCurrentGithubRateLimits();
        ServerUtils.sendResponse(ctx, rateLimitResponse, HttpResponseStatus.OK, null);
      }
      case Endpoints.APP_TESTS_TASKS -> {
        final String isRemainingTasksOnlyStr =
            ServerUtils.getQueryParam(fullHttpRequest.uri(), "remainingTasksOnly", "false");
        final Map<String, List<ProcessSummaries.ProcessSummary.ProcessTask>> processTaskQueues =
            updateRepoService.getAllProcessTaskQueues("true".equals(isRemainingTasksOnlyStr));
        ServerUtils.sendResponse(ctx, processTaskQueues, HttpResponseStatus.OK, null);
      }
      case Endpoints.APP_TESTS_CLEAR -> {
        ServerUtils.sendResponse(
            ctx,
            null,
            HttpResponseStatus.OK,
            String.format(ConstantUtils.JSON_RESPONSE, "request", "submitted"));
        updateRepoService.clearTaskQueues();
      }
      case Endpoints.APP_TESTS_SCHEDULE -> {
        final Map<String, LocalDateTime> nextRunTimes = ScheduleConfig.nextRunTimes();
        ServerUtils.sendResponse(ctx, nextRunTimes, HttpResponseStatus.OK, "");
      }
      case null, default ->
          ServerUtils.sendResponse(
              ctx, "AppTestController Mapping Not Found...", HttpResponseStatus.NOT_FOUND);
    }
  }
}
