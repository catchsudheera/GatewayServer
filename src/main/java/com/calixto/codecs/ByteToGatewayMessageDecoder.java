package com.calixto.codecs;

import com.calixto.models.GatewayMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;


import java.util.List;

/**
 * Created by sudheera on 7/11/15.
 */
public class ByteToGatewayMessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes()>=4){
            ByteBuf byteBuf = in.readBytes(4);

            String numString="";
            for (byte b : byteBuf.array()){
                numString+= (char)b;
            }

            int messageLength = Integer.parseInt(numString.replaceFirst("^0+(?!$)", ""));

            if (in.readableBytes() >= messageLength){
                ByteBuf byteBuf2 = in.readBytes(messageLength);

                String message="";
                for (byte b : byteBuf2.array()){
                    message+=(char)b;
                }
                out.add(new GatewayMessage(messageLength, message, numString));
            }

        }
    }
}
