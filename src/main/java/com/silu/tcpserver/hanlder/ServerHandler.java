package com.silu.tcpserver.hanlder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by piguangtao on 15/6/1.
 */
@Service
public class ServerHandler extends ChannelInboundHandlerAdapter {
    private static Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(">>>>> I'm server.");
//        ctx.write("Hello world!");
//        ctx.flush();
        String msg = "Are you ok?";
        ByteBuf encoded = ctx.alloc().buffer(msg.length());
        encoded.writeBytes(msg.getBytes());
        ctx.write(encoded);
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        try {
            while (in.isReadable()) { // (1)
                LOGGER.info("Server收到客户端的消息:" + msg);
                LOGGER.info(String.valueOf((char) in.readByte()));
            }
        } finally {
            ReferenceCountUtil.release(msg); // (2)
        }
    }
}
