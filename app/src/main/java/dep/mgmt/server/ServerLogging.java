package dep.mgmt.server;

import dep.mgmt.util.ConstantUtils;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class ServerLogging extends ChannelDuplexHandler {
  private static final Logger log = LoggerFactory.getLogger(ServerLogging.class);

  @Override
  public void channelRead(final ChannelHandlerContext channelHandlerContext, final Object object)
      throws Exception {
    if (object instanceof FullHttpRequest fullHttpRequest) {
      final String requestId = UUID.randomUUID().toString();

      final String requestContentLength =
          fullHttpRequest
              .headers()
              .get(HttpHeaderNames.CONTENT_LENGTH, ConstantUtils.CONTENT_LENGTH_DEFAULT);
      log.info(
          "[{}] Request IN: [{}], [{}], [{}]",
          requestId,
          fullHttpRequest.method(),
          fullHttpRequest.uri(),
          requestContentLength);
      channelHandlerContext.channel().attr(ConstantUtils.REQUEST_ID).set(requestId);
    }
    super.channelRead(channelHandlerContext, object);
  }

  @Override
  public void write(
      final ChannelHandlerContext channelHandlerContext,
      final Object object,
      final ChannelPromise channelPromise)
      throws Exception {
    if (object instanceof FullHttpResponse fullHttpResponse) {
      final String responseContentLength =
          fullHttpResponse
              .headers()
              .get(HttpHeaderNames.CONTENT_LENGTH, ConstantUtils.CONTENT_LENGTH_DEFAULT);
      final HttpResponseStatus responseStatus = fullHttpResponse.status();
      final String requestId = channelHandlerContext.channel().attr(ConstantUtils.REQUEST_ID).get();

      log.info("[{}] Response OUT: [{}], [{}]", requestId, responseStatus, responseContentLength);
    }
    super.write(channelHandlerContext, object, channelPromise);
  }
}
