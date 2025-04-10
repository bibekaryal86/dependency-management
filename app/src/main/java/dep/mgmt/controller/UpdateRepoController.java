package dep.mgmt.controller;

import dep.mgmt.model.RequestMetadata;
import dep.mgmt.model.enums.RequestParams;
import dep.mgmt.server.Endpoints;
import dep.mgmt.service.UpdateRepoService;
import dep.mgmt.util.ConstantUtils;
import dep.mgmt.util.ServerUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class UpdateRepoController {

  private final UpdateRepoService updateRepoService;

  public UpdateRepoController() {
    this.updateRepoService = new UpdateRepoService();
  }

  public void handleRequest(
      final FullHttpRequest fullHttpRequest, final ChannelHandlerContext ctx) {
    final String requestUri = ServerUtils.getRequestUriLessParams(fullHttpRequest.uri());

    switch (requestUri) {
      case Endpoints.UPDATE_DEPENDENCIES_EXECUTE -> {
        final RequestMetadata requestMetadata = getRequestMetadata(fullHttpRequest);
        if (requestMetadata == null) {
          ServerUtils.sendResponse(
              ctx, "Missing Request Metadata...", HttpResponseStatus.BAD_REQUEST);
          return;
        }

        if (!isBranchDateValid(
            String.valueOf(requestMetadata.getBranchDate()), requestMetadata.getUpdateType())) {
          ServerUtils.sendResponse(
              ctx, "Missing or Invalid Branch Date...", HttpResponseStatus.BAD_REQUEST);
          return;
        }

        updateRepoService.updateRepos(requestMetadata, Boolean.FALSE);
        ServerUtils.sendResponse(
            ctx, null, HttpResponseStatus.ACCEPTED, ConstantUtils.RESPONSE_REQUEST_SUBMITTED);
      }
      case null, default ->
          ServerUtils.sendResponse(
              ctx, "UpdateRepoController Mapping Not Found...", HttpResponseStatus.NOT_FOUND);
    }
  }

  private RequestMetadata getRequestMetadata(final FullHttpRequest fullHttpRequest) {
    final RequestMetadata requestMetadata =
        ServerUtils.getRequestBody(fullHttpRequest, RequestMetadata.class);
    if (requestMetadata == null) {
      return null;
    }

    return new RequestMetadata(
        requestMetadata.getUpdateType(),
        Optional.ofNullable(requestMetadata.getRecreateCaches()).orElse(false),
        Optional.ofNullable(requestMetadata.getRecreateScriptFiles()).orElse(false),
        Optional.ofNullable(requestMetadata.getGithubResetRequired()).orElse(false),
        Optional.ofNullable(requestMetadata.getIsGithubPullRequired()).orElse(false),
        Optional.ofNullable(requestMetadata.getProcessSummaryRequired()).orElse(false),
        Optional.ofNullable(requestMetadata.getDeleteUpdateDependenciesOnly()).orElse(false),
        Optional.ofNullable(requestMetadata.getIncludeDebugLogs()).orElse(false),
        Optional.ofNullable(requestMetadata.getBranchDate()).orElse(LocalDate.now()),
        requestMetadata.getRepoName());
  }

  private boolean isBranchDateValid(
      final String branchDate, final RequestParams.UpdateType updateType) {
    if (updateType.equals(RequestParams.UpdateType.SNAPSHOT)
        || updateType.equals(RequestParams.UpdateType.SPOTLESS)
        || updateType.equals(RequestParams.UpdateType.PULL_REQ)
        || updateType.equals(RequestParams.UpdateType.MERGE)) {
      try {
        LocalDate.parse(branchDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
      } catch (Exception ignored) {
        return false;
      }
    }
    return true;
  }
}
