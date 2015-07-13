
package com.calixto.gateway;

import com.calixto.models.GatewayMessage;
import com.calixto.models.QueueElement;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import org.apache.log4j.Logger;


public class FrontendHandler extends ChannelInboundHandlerAdapter {
    final static Logger logger = Logger.getLogger(FrontendHandler.class);

    public FrontendHandler(){
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws InterruptedException {

        if (msg instanceof GatewayMessage) {
            GatewayMessage gMessage = (GatewayMessage) msg;

            logger.info("1. Message Received to gateway server from client length : " + gMessage.getMessageLength() + ", message : " + gMessage.getMessage());

            /**
             * you can edit the message here: just get the String message from the msg object
             * and edit it, create a new GatewayMessage object giving the message String, length int
             * and numString as the 4-character long messageLength
             *
             * use the newly created GatewayMessage in following enqueuing
             */

            logger.info("Enqueuing the Message > length : " + gMessage.getMessageLength() + ", message : " + gMessage.getMessage());
            MessageQueue.getInstance().add(new QueueElement(gMessage, ctx.channel()));

        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        closeOnFlush(ctx.channel());
    }

    /**
     * Closes the specified channel after all queued write requests are flushed.
     */
    public static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
