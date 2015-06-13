package com.silu.tcpserver.sample;

import com.google.protobuf.ByteString;
import com.silu.proto.chat.ClientServerMsg;
import com.silu.tcpserver.constant.MsgTypeConstant;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.nio.charset.StandardCharsets;
import java.util.UUID;


/**
 * Created by piguangtao on 15/6/10.
 * 模拟连个客户端之间相互发送消息
 */
public class ChatClient {
    private static final String serverIp = "127.0.0.1";
    private static final int serverPort = 18080;

    public void init() {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new LoggingHandler(LogLevel.INFO))
//                                .addLast("frameDecoder", new ProtobufVarint32FrameDecoder())
                                .addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65536,0,4,0,4))
                                .addLast("pbDecoder", new ProtobufDecoder(ClientServerMsg.Rsp.getDefaultInstance()))
//                                .addLast("frameEncode", new ProtobufVarint32LengthFieldPrepender())
                                .addLast("frameEncode", new LengthFieldPrepender(4))
                                .addLast("protobufEncode", new ProtobufEncoder())
                                .addLast(new ChatClientHandler());
                    }
                });
        try {
            ChannelFuture f = bootstrap.connect(serverIp, serverPort).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static class ChatClientHandler extends ChannelInboundHandlerAdapter {
        // 接收server端的消息，并打印出来
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof ClientServerMsg.Rsp) {
                ClientServerMsg.Rsp rsp = (ClientServerMsg.Rsp) msg;
                switch (rsp.getType()) {
                    case MsgTypeConstant.LOGIN: {
                        String status = rsp.getContent().toStringUtf8();
                        System.out.println(String.format("login result.%s", status));
                        break;
                    }
                    default: {
                        System.out.println(String.format("type:%s not support.", rsp.getType()));
                        break;
                    }
                }

            } else {
                System.out.println("receive other msg." + msg);
            }
        }

        // 连接成功后，向server发送消息
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            String sessionId = UUID.randomUUID().toString();
            ClientServerMsg.Req.Builder builder = ClientServerMsg.Req.newBuilder();
            builder.setType(MsgTypeConstant.LOGIN);
            builder.setContent(ByteString.copyFrom(sessionId.getBytes(StandardCharsets.UTF_8)));
            ctx.channel().writeAndFlush(builder.build());
        }
    }

    public static void main(String[] args) {

        ChatClient chatClient = new ChatClient();
        chatClient.init();
    }
}
