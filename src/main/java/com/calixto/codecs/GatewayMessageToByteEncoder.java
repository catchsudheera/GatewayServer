package com.calixto.codecs;

import com.calixto.models.GatewayMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by sudheera on 7/11/15.
 */
public class GatewayMessageToByteEncoder extends MessageToByteEncoder<GatewayMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, GatewayMessage msg, ByteBuf out) throws Exception {
        out.writeBytes(msg.getByteArray());
    }
}
