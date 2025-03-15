package dep.mgmt.controller;

import dep.mgmt.config.CacheConfig;
import dep.mgmt.server.Endpoints;
import dep.mgmt.service.GradleDependencyVersionService;
import dep.mgmt.service.GradlePluginVersionService;
import dep.mgmt.service.NpmDependencyVersionService;
import dep.mgmt.service.PythonPackageVersionService;
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
import java.util.concurrent.CompletableFuture;

public class AppTestController {

  private static final String TESTS_PING_RESPONSE = "{\"ping\": \"successful\"}";
  private static final String TESTS_RESET_RESPONSE = "{\"reset\": \"successful\"}";

  private final PythonPackageVersionService pythonPackageVersionService;
  private final NpmDependencyVersionService npmDependencyVersionService;
  private final GradlePluginVersionService gradlePluginVersionService;
  private final GradleDependencyVersionService gradleDependencyVersionService;

  public AppTestController() {
    this.pythonPackageVersionService = new PythonPackageVersionService();
    this.npmDependencyVersionService = new NpmDependencyVersionService();
    this.gradlePluginVersionService = new GradlePluginVersionService();
    this.gradleDependencyVersionService = new GradleDependencyVersionService();
  }

  public void handleRequest(
      final FullHttpRequest fullHttpRequest, final ChannelHandlerContext ctx) {
    final String requestUri = ServerUtils.getRequestUriLessParams(fullHttpRequest.uri());

    switch (requestUri) {
      case Endpoints.APP_TESTS_PING -> sendResponse(TESTS_PING_RESPONSE, ctx);
      case Endpoints.APP_TESTS_RESET -> {
        // TODO check if update is going on, if update is going on - do not reset
        CacheConfig.resetPythonPackagesMap();
        CacheConfig.resetNpmDependenciesMap();
        CacheConfig.resetGradlePluginsMap();
        CacheConfig.resetGradleDependenciesMap();
        CacheConfig.resetAppData();

        CompletableFuture.runAsync(
            () -> {
              pythonPackageVersionService.getPythonPackagesMap();
              npmDependencyVersionService.getNpmDependenciesMap();
              gradlePluginVersionService.getGradlePluginsMap();
              gradleDependencyVersionService.getGradleDependenciesMap();
            });

        sendResponse(TESTS_RESET_RESPONSE, ctx);
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
