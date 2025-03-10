package dep.mgmt.util;

import dep.mgmt.model.RequestMetadata;
import dep.mgmt.model.enums.RequestParams;
import io.github.bibekaryal86.shdsvc.dtos.ResponseMetadata;
import io.github.bibekaryal86.shdsvc.dtos.ResponseWithMetadata;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
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
import io.netty.handler.codec.http.QueryStringDecoder;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ServerUtils {

  public static void sendErrorResponse(
      final ChannelHandlerContext ctx, final String errMsg, final HttpResponseStatus status) {
    final ResponseWithMetadata responseWithMetadata =
        new ResponseWithMetadata(
            new ResponseMetadata(
                new ResponseMetadata.ResponseStatusInfo(errMsg),
                ResponseMetadata.emptyResponseCrudInfo(),
                ResponseMetadata.emptyResponsePageInfo()));
    sendResponse(ctx, responseWithMetadata, status);
  }

  private static void sendResponse(
      final ChannelHandlerContext ctx, final Object object, final HttpResponseStatus status) {
    final byte[] jsonResponse = CommonUtilities.writeValueAsBytesNoEx(object);
    final FullHttpResponse fullHttpResponse =
        new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1, status, Unpooled.wrappedBuffer(jsonResponse));
    fullHttpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, jsonResponse.length);
    fullHttpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
    ctx.writeAndFlush(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
  }

  public static Map<String, List<String>> getQueryParams(final String requestUri) {
    QueryStringDecoder decoder = new QueryStringDecoder(requestUri);
    return decoder.parameters();
  }

  public static String getQueryParam(
      final String requestUri, final String paramName, final String defaultValue) {
    Map<String, List<String>> parameters = getQueryParams(requestUri);

    if (CommonUtilities.isEmpty(parameters)) {
      return defaultValue;
    }

    return parameters.getOrDefault(paramName, List.of(defaultValue)).getFirst();
  }

  public static String getRequestUriLessParams(final String requestUri) {
    final String[] parts = requestUri.split("\\?");
    if (parts[0].endsWith("/")) {
      return parts[0].substring(0, parts[0].length() - 1);
    }
    return parts[0];
  }

  public static RequestMetadata parseRequestMetadata(final FullHttpRequest request) {
    final Map<String, List<String>> queryParams = getQueryParams(request.uri());

    final RequestParams.UpdateType updateType =
        RequestParams.UpdateType.valueOf(
            queryParams
                .getOrDefault("updateType", List.of(RequestParams.UpdateType.ALL.toString()))
                .getFirst());
    final boolean isRecreateCaches =
        Boolean.parseBoolean(
            queryParams.getOrDefault("isRecreateCaches", List.of("false")).getFirst());
    final boolean isRecreateScriptFiles =
        Boolean.parseBoolean(
            queryParams.getOrDefault("isRecreateScriptFiles", List.of("false")).getFirst());
    final boolean isGithubResetPullRequired =
        Boolean.parseBoolean(
            queryParams.getOrDefault("isGithubResetPullRequired", List.of("true")).getFirst());
    final boolean isProcessSummaryRequired =
        Boolean.parseBoolean(
            queryParams.getOrDefault("isProcessSummaryRequired", List.of("false")).getFirst());
    final boolean isForceCreatePr =
        Boolean.parseBoolean(
            queryParams.getOrDefault("isForceCreatePr", List.of("false")).getFirst());
    final boolean isDeleteUpdateDependenciesOnly =
        Boolean.parseBoolean(
            queryParams.getOrDefault("isDeleteUpdateDependenciesOnly", List.of("true")).getFirst());
    final LocalDate branchDate =
        LocalDate.parse(
            queryParams.getOrDefault("branchDate", List.of(LocalDate.now().toString())).getFirst());
    final String repoName = queryParams.getOrDefault("sortColumn", List.of("")).getFirst();

    return new RequestMetadata(
        updateType,
        isRecreateCaches,
        isRecreateScriptFiles,
        isGithubResetPullRequired,
        isProcessSummaryRequired,
        isForceCreatePr,
        isDeleteUpdateDependenciesOnly,
        branchDate,
        repoName);
  }
}
