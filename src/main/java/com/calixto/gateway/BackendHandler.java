
package com.calixto.gateway;

import io.netty.channel.*;
import org.apache.log4j.Logger;

public class BackendHandler extends ChannelInboundHandlerAdapter {
    final static Logger logger = Logger.getLogger(BackendHandler.class);
    volatile Channel inboundChannel;


    @Override
    public void channelActive(ChannelHandlerContext ctx) {

    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        logger.info("3. Message Received to gateway server from back end");
        inboundChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()){
                    logger.info("4. Message successfully delivered to front end by gateway");
                    inboundChannel=null;
                }
                else {
                    logger.error(" Message delivery to front end failed by gateway", future.cause());
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
