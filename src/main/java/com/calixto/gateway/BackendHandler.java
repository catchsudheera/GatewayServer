
package com.calixto.gateway;

import io.netty.channel.*;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class BackendHandler extends ChannelInboundHandlerAdapter {
    final static Logger logger = Logger.getLogger(BackendHandler.class);
    volatile Channel inboundChannel=null;


    @Override
    public void channelActive(ChannelHandlerContext ctx) {

    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws InterruptedException {
        logger.info("3. Message Received to gateway server from back end");

        while (inboundChannel==null){
            inboundChannel = ctx.channel().attr(Constants.inboundChannel).getAndRemove();
            Thread.sleep(10);
        }

        inboundChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()){
                    inboundChannel=null;
                    logger.info("4. Message successfully sent to front end by gateway");
                } else {
                    logger.error("Error sending msg to front end ", future.cause());
                }
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }
}
