
package com.calixto.gateway;

import com.calixto.models.GatewayMessage;
import com.calixto.models.QueueElement;
import io.netty.channel.*;
import org.apache.log4j.Logger;

public class BackendHandler extends ChannelInboundHandlerAdapter {
    final static Logger logger = Logger.getLogger(BackendHandler.class);


    @Override
    public void channelActive(ChannelHandlerContext ctx) {

    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws InterruptedException {
        logger.info("3. Message Received to gateway server from back end");

        if (msg instanceof GatewayMessage) {
            GatewayMessage message = (GatewayMessage) msg;


            /**
             * you can edit the message here: just get the String message from the message object
             * and edit it, create a new GatewayMessage object giving the message String, length int
             * and numString as the 4-character long messageLength
             *
             * use the newly created GatewayMessage in following writeAndFlush(message)
             * e.g : message = new GatewayMessage(4, "ZXCV", "0004");
             */

            QueueElement poll = InboundChannelQueue.getInstance().poll();
            Channel inboundChannel = poll.getInboundChannel();
                while (inboundChannel == null) {
                    Thread.sleep(2);
                    poll = InboundChannelQueue.getInstance().poll();
                    inboundChannel = poll.getInboundChannel();
                }

                if (inboundChannel.isOpen()) {
                    inboundChannel.writeAndFlush(message.getMessageWithID(poll.getMessage().getID())).addListener(new ChannelFutureListener() {
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (future.isSuccess()) {
                                logger.info("4. Message successfully sent to front end by gateway");
                            } else {
                                logger.error("Error sending msg to front end ", future.cause());
                            }
                        }
                    });
                } else {
                    logger.error("Connected peer is closed");
                }
             }
        }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }
}
