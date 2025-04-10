package dep.mgmt.controller;

import dep.mgmt.model.enums.RequestParams;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UpdateRepoController {

  public void handleRequest(final FullHttpRequest fullHttpRequest, final ChannelHandlerContext ctx) {}

  private boolean isBranchDateValid(final String branchDate, final RequestParams.UpdateType updateType) {
    if (updateType.equals(RequestParams.UpdateType.SNAPSHOT)
            || updateType.equals(RequestParams.UpdateType.SPOTLESS)
            || updateType.equals(RequestParams.UpdateType.PULL_REQ)
            || updateType.equals(RequestParams.UpdateType.MERGE)
    ) {
      try {
        LocalDate.parse(branchDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
      } catch (Exception ignored) {
        return false;
      }
    }
    return true;
  }
}
