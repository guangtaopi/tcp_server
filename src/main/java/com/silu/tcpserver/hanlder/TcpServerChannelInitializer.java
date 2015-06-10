package com.silu.tcpserver.hanlder;

import com.silu.proto.chat.ClientServerMsg;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
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

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //增加编解码
        ch.pipeline()
                .addLast(new LoggingHandler(LogLevel.INFO))
                .addLast("frameDecoder", new ProtobufVarint32FrameDecoder())
                .addLast("pbDecoder", new ProtobufDecoder(ClientServerMsg.Req.getDefaultInstance()))
                .addLast("frameEncode", new ProtobufVarint32LengthFieldPrepender())
                .addLast("protobufEncode", new ProtobufEncoder())
                .addLast(new ReaderIdleHandler(60))
                .addLast(serverHandler);
    }
}
