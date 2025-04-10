package dep.mgmt.controller;

import dep.mgmt.model.ProcessSummaries;
import dep.mgmt.model.web.GithubApiModel;
import dep.mgmt.server.Endpoints;
import dep.mgmt.service.GithubService;
import dep.mgmt.service.UpdateManagerService;
import dep.mgmt.util.ProcessUtils;
import dep.mgmt.util.ServerUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.util.List;

public class AppTestController {

  private static final String TESTS_PING_RESPONSE = "{\"ping\": \"successful\"}";
  private static final String TESTS_RESET_RESPONSE = "{\"reset\": \"requested\"}";

  private final UpdateManagerService updateManagerService;
  private final GithubService githubService;

  public AppTestController() {
    this.updateManagerService = new UpdateManagerService();
    this.githubService = new GithubService();
  }

  public void handleRequest(
      final FullHttpRequest fullHttpRequest, final ChannelHandlerContext ctx) {
    final String requestUri = ServerUtils.getRequestUriLessParams(fullHttpRequest.uri());

    switch (requestUri) {
      case Endpoints.APP_TESTS_PING -> sendResponse(TESTS_PING_RESPONSE, ctx);
      case Endpoints.APP_TESTS_RESET -> {
        sendResponse(TESTS_RESET_RESPONSE, ctx);
        updateManagerService.recreateLocalCaches();
      }
      case Endpoints.APP_TESTS_RATE -> {
        GithubApiModel.RateLimitResponse rateLimitResponse =
            githubService.getCurrentGithubRateLimits();
        ServerUtils.sendResponse(ctx, rateLimitResponse, HttpResponseStatus.OK);
      }
      case Endpoints.APP_TESTS_TASKS -> {
        List<ProcessSummaries.ProcessSummary.ProcessTask> processTasks =
            ProcessUtils.getProcessedTasks().values().stream().toList();
        ServerUtils.sendResponse(ctx, processTasks, HttpResponseStatus.OK);
      }
      case null, default ->
          ServerUtils.sendErrorResponse(
              ctx, "AppTestController Mapping Not Found...", HttpResponseStatus.NOT_FOUND);
    }
  }

  private void sendResponse(
      final String jsonResponse, final ChannelHandlerContext channelHandlerContext) {
    final FullHttpResponse fullHttpResponse =
        new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1,
            HttpResponseStatus.OK,
            Unpooled.wrappedBuffer(jsonResponse.getBytes()));
    fullHttpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
    fullHttpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, jsonResponse.length());
    channelHandlerContext.writeAndFlush(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
  }
}
