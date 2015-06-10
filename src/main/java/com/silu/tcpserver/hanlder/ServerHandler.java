package com.silu.tcpserver.hanlder;

import com.google.protobuf.ByteString;
import com.silu.proto.chat.ClientServerMsg;
import com.silu.tcpserver.constant.MsgTypeConstant;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by piguangtao on 15/6/1.
 */
@Service
@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {
    private static Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);

    private ConcurrentHashMap<String, Channel> userChannels = new ConcurrentHashMap<String, Channel>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(">>>>> I'm server.");
////        ctx.write("Hello world!");
////        ctx.flush();
//        String msg = "Are you ok?";
//        ByteBuf encoded = ctx.alloc().buffer(msg.length());
//        encoded.writeBytes(msg.getBytes());
//        ctx.write(encoded);
//        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ClientServerMsg.Req) {
            ClientServerMsg.Req req = (ClientServerMsg.Req) msg;
            switch (req.getType()) {
                case MsgTypeConstant.LOGIN: {
                    String sessionId = req.getContent().toStringUtf8();
                    userChannels.put(sessionId, ctx.channel());
                    ClientServerMsg.Rsp.Builder rsp = ClientServerMsg.Rsp.newBuilder();
                    rsp.setType(MsgTypeConstant.LOGIN);
                    rsp.setContent(ByteString.copyFrom("1".getBytes()));
                    ctx.channel().writeAndFlush(rsp.build());
                    break;
                }
                default: {
                    System.out.println(String.format("type:%s not support.", req.getType()));
                    break;
                }
            }

        } else {
            System.out.println("receive other msg." + msg);
        }

    }
}
