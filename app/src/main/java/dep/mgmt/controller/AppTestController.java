package dep.mgmt.controller;

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
    final String requestUri = ServerUtils.getRequestUriLessParams(fullHttpRequest.uri());

    switch (requestUri) {
      case Endpoints.APP_TESTS_PING ->
          ServerUtils.sendResponse(
              ctx, null, HttpResponseStatus.OK, ConstantUtils.RESPONSE_TESTS_PING);
      case Endpoints.APP_TESTS_RESET -> {
        ServerUtils.sendResponse(
            ctx, null, HttpResponseStatus.OK, ConstantUtils.RESPONSE_REQUEST_SUBMITTED);
        updateRepoService.recreateLocalCaches();
      }
      case Endpoints.APP_TESTS_RATE -> {
        GithubApiModel.RateLimitResponse rateLimitResponse =
            githubService.getCurrentGithubRateLimits();
        ServerUtils.sendResponse(ctx, rateLimitResponse, HttpResponseStatus.OK, null);
      }
      case Endpoints.APP_TESTS_TASKS -> {
        final Map<String, List<ProcessSummaries.ProcessSummary.ProcessTask>> processTaskQueues =
            updateRepoService.getAllProcessTaskQueues();
        ServerUtils.sendResponse(ctx, processTaskQueues, HttpResponseStatus.OK, null);
      }
      case Endpoints.APP_TESTS_CLEAR -> {
        ServerUtils.sendResponse(
            ctx, null, HttpResponseStatus.OK, ConstantUtils.RESPONSE_REQUEST_SUBMITTED);
        updateRepoService.clearTaskQueues();
      }
      case null, default ->
          ServerUtils.sendResponse(
              ctx, "AppTestController Mapping Not Found...", HttpResponseStatus.NOT_FOUND);
    }
  }
}
