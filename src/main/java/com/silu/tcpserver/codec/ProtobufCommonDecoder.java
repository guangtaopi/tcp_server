package com.silu.tcpserver.codec;

import com.google.protobuf.MessageLite;
import io.netty.handler.codec.protobuf.ProtobufDecoder;

/**
 * Created by piguangtao on 15/6/10.
 */
public class ProtobufCommonDecoder extends ProtobufDecoder {
    /**
     * Creates a new instance.
     *
     * @param prototype
     */
    public ProtobufCommonDecoder(MessageLite prototype) {
        super(prototype);
    }
}
