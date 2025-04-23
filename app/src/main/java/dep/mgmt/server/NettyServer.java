package dep.mgmt.server;

import dep.mgmt.config.SecurityConfig;
import dep.mgmt.util.ConstantUtils;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServer {
  private static final Logger log = LoggerFactory.getLogger(NettyServer.class);

  public static void init() throws Exception {
    final EventLoopGroup bossGroup = new NioEventLoopGroup(ConstantUtils.BOSS_GROUP_THREADS);
    final EventLoopGroup workerGroup = new NioEventLoopGroup(ConstantUtils.WORKER_GROUP_THREADS);

    try {
      final ServerBootstrap serverBootstrap = new ServerBootstrap();
      serverBootstrap
          .group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, ConstantUtils.CONNECT_TIMEOUT_MILLIS)
          .childHandler(
              new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(final SocketChannel socketChannel) throws Exception {
                  socketChannel
                      .pipeline()
                      .addLast(new HttpServerCodec())
                      .addLast(new HttpObjectAggregator(ConstantUtils.MAX_CONTENT_LENGTH))
                      .addLast(new ServerLogging())
                      .addLast(new SecurityConfig())
                      .addLast(new ServletHandler());
                }
              });

      final int serverPort =
          Integer.parseInt(
              CommonUtilities.getSystemEnvProperty(
                  ConstantUtils.ENV_SERVER_PORT, ConstantUtils.ENV_PORT_DEFAULT));
      final ChannelFuture channelFuture = serverBootstrap.bind(serverPort).sync();

      log.info("Dependency Management Started on Port [{}]...", serverPort);
      channelFuture.channel().closeFuture().sync();
    } finally {
      workerGroup.shutdownGracefully();
      bossGroup.shutdownGracefully();
      log.info("Dependency Management Stopped...");
    }
  }
}
