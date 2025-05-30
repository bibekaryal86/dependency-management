package dep.mgmt.config;

import dep.mgmt.util.ConstantUtils;
import dep.mgmt.util.ServerUtils;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class SecurityConfig extends ChannelDuplexHandler {

  @Override
  public void channelRead(
      @NotNull final ChannelHandlerContext channelHandlerContext, @NotNull final Object object)
      throws Exception {
    if (object instanceof FullHttpRequest fullHttpRequest) {
      final String requestUri = fullHttpRequest.uri();

      final boolean isNoAuth = isNoAuthCheck(requestUri);
      if (isNoAuth) {
        super.channelRead(channelHandlerContext, fullHttpRequest);
        return;
      }

      String authHeader = fullHttpRequest.headers().get(HttpHeaderNames.AUTHORIZATION);
      // just in case check
      if (CommonUtilities.isEmpty(authHeader)) {
        authHeader = fullHttpRequest.headers().get(HttpHeaderNames.AUTHORIZATION.toLowerCase());
      }

      if (CommonUtilities.isEmpty(authHeader)) {
        ServerUtils.sendResponse(
            channelHandlerContext,
            ConstantUtils.NOT_AUTHENTICATED,
            HttpResponseStatus.UNAUTHORIZED);
        return;
      }

      if (isBasicAuthenticated(authHeader)) {
        super.channelRead(channelHandlerContext, object);
      } else {
        ServerUtils.sendResponse(
            channelHandlerContext, ConstantUtils.NOT_AUTHORIZED, HttpResponseStatus.FORBIDDEN);
      }
    } else {
      super.channelRead(channelHandlerContext, object);
    }
  }

  private boolean isNoAuthCheck(final String requestUri) {
    return requestUri.matches("^.*(?:/tests/ping|/tests/status|/tests/check).*");
  }

  private boolean isBasicAuthenticated(final String actualAuth) {
    final String username = CommonUtilities.getSystemEnvProperty(ConstantUtils.ENV_SELF_USER);
    final String password = CommonUtilities.getSystemEnvProperty(ConstantUtils.ENV_SELF_PWD);
    final String expectedAuth = CommonUtilities.getBasicAuth(username, password);
    return Objects.equals(expectedAuth, actualAuth);
  }
}
