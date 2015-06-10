package com.silu.tcpserver.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by piguangtao on 15/5/31.
 */
@Service
public class NettyServer implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);

    @Autowired
    private ChannelInitializer channelInitializer;

    @Value("${tcp.server.port}")
    private int port;

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    public void init() {
        LOGGER.info("[NettyServer] begin to init.");
        ServerBootstrap server = new ServerBootstrap();
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
        server.group(nioEventLoopGroup);
        try {
            ChannelFuture channelFuture = server.channel(NioServerSocketChannel.class)
                    .childHandler(channelInitializer)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .bind(port).sync();
            channelFuture.addListener(new GenericFutureListener() {

                @Override
                public void operationComplete(Future future) throws Exception {
                    if (future.isSuccess()) {
                        LOGGER.info("server success to start.");
                    } else {
                        LOGGER.error("server fails to start", future.cause());
                    }
                }
            });
            channelFuture.channel().closeFuture().sync();
            LOGGER.info("server begin to stop");
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            nioEventLoopGroup.shutdownGracefully();
        }
    }
}
