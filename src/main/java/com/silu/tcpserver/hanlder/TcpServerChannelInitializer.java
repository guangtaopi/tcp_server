package com.silu.tcpserver.hanlder;

import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Created by piguangtao on 15/6/1.
 */
@Service
public class TcpServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Autowired
    @Qualifier("serverHandler")
    private ChannelInboundHandler serverHandler;
;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //增加编解码
        ch.pipeline().addLast("frameDecoder",new ProtobufVarint32FrameDecoder())
                .addLast("frameEncode",new ProtobufVarint32LengthFieldPrepender())
                .addLast(new LoggingHandler(LogLevel.INFO))
                .addLast(new ReaderIdleHandler(60))
                .addLast(serverHandler);
    }
}
