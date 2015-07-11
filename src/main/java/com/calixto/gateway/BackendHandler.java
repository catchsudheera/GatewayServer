
package com.calixto.gateway;

import io.netty.channel.*;
import org.apache.log4j.Logger;

public class BackendHandler extends ChannelInboundHandlerAdapter {
    final static Logger logger = Logger.getLogger(BackendHandler.class);
    volatile Channel inboundChannel;


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        while (inboundChannel==null) {
            inboundChannel = ctx.channel().attr(Constants.inboundChannel).get();
        }
        ctx.channel().config().setAutoRead(true);

    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        logger.info("Message Received to gateway server from back end");
        inboundChannel.writeAndFlush(msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }
}
