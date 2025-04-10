package dep.mgmt.server;

import dep.mgmt.controller.AppTestController;
import dep.mgmt.controller.MongoRepoController;
import dep.mgmt.controller.UpdateRepoController;
import dep.mgmt.util.ConstantUtils;
import dep.mgmt.util.ServerUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletHandler extends ChannelInboundHandlerAdapter {
  private static final Logger log = LoggerFactory.getLogger(ServletHandler.class);

  private final AppTestController appTestController;
  private final MongoRepoController mongoRepoController;
  private final UpdateRepoController updateRepoController;

  public ServletHandler() {
    this.appTestController = new AppTestController();
    this.mongoRepoController = new MongoRepoController();
    this.updateRepoController = new UpdateRepoController();
  }

  @Override
  public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
    if (msg instanceof FullHttpRequest fullHttpRequest) {
      final HttpMethod httpMethod = fullHttpRequest.method();
      if (httpMethod.equals(HttpMethod.OPTIONS)) {
        ServerUtils.sendErrorResponse(ctx, "", HttpResponseStatus.NO_CONTENT);
        return;
      }

      final String requestUri = fullHttpRequest.uri();
      final String requestId = ctx.channel().attr(ConstantUtils.REQUEST_ID).get();

      if (requestUri.startsWith(Endpoints.APP_TESTS_CONTROLLER)) {
        log.info("[{}] Routing to AppTestController...", requestId);
        appTestController.handleRequest(fullHttpRequest, ctx);
      } else if (requestUri.startsWith(Endpoints.MONGO_REPO_CONTROLLER)) {
        log.info("[{}] Routing to MongoRepoController...", requestId);
        mongoRepoController.handleRequest(fullHttpRequest, ctx);
      } else if (requestUri.startsWith(Endpoints.UPDATE_DEPENDENCIES_CONTROLLER)) {
        log.info("[{}] Routing to UpdateRepoController...", requestId);
        updateRepoController.handleRequest(fullHttpRequest, ctx);
      } else {
        ServerUtils.sendErrorResponse(
            ctx, "Servlet Mapping Not Found...", HttpResponseStatus.NOT_FOUND);
      }
    } else {
      super.channelRead(ctx, msg);
    }
  }

  @Override
  public void exceptionCaught(
      final ChannelHandlerContext channelHandlerContext, final Throwable throwable) {
    final String requestId = channelHandlerContext.channel().attr(ConstantUtils.REQUEST_ID).get();
    log.error("[{}] Servlet Handler Exception Caught...", requestId, throwable);

    ServerUtils.sendErrorResponse(
        channelHandlerContext,
        "Servlet Handler Exception Caught: " + throwable.getMessage(),
        HttpResponseStatus.INTERNAL_SERVER_ERROR);
  }
}
