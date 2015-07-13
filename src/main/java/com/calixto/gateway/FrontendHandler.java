
package com.calixto.gateway;

import com.calixto.models.GatewayMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.log4j.Logger;


public class FrontendHandler extends ChannelInboundHandlerAdapter {
    final static Logger logger = Logger.getLogger(FrontendHandler.class);
    private Channel outboundChannel;
    private final String REMOTE_HOST;
    private final int REMOTE_PORT;

    public FrontendHandler(Channel outboundChannel, String REMOTE_HOST, int REMOTE_PORT) {

        this.outboundChannel = outboundChannel;
        this.REMOTE_HOST = REMOTE_HOST;
        this.REMOTE_PORT = REMOTE_PORT;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws InterruptedException {

        if (msg instanceof GatewayMessage) {
            GatewayMessage gMessage = (GatewayMessage) msg;

            logger.info("1. Message Received to gateway server from client length : " + gMessage.getMessageLength() + ", message : " + gMessage.getMessage());
            outboundChannel.attr(Constants.inboundChannel).set(ctx.channel());

            /**
             * you can edit thr message here: just get the String message from the msg object
             * and edit it, create a new GatewayMessage object giving the message String, length int
             * and numString as the 4-character long messageLength
             *
             * use the newly created GatewayMessage in following writeAndFlush
             */

            writeToChannel(gMessage, outboundChannel);
        }

    }

    private void writeToChannel(final GatewayMessage msg, final Channel channel){
        channel.writeAndFlush(msg).addListeners(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()){
                    logger.info("2. Message successfully sent to backend server");
                } else {
                    logger.error("Message failed to send to back end server, reconnecting...");

                    final Bootstrap cb = new Bootstrap();
                    cb.group(channel.eventLoop()).channel(NioSocketChannel.class)
                            .option(ChannelOption.TCP_NODELAY, true)
                            .option(ChannelOption.SO_KEEPALIVE, true)
                            .option(ChannelOption.AUTO_READ, false);
                    cb.handler(new BackEndHandlerInitializer());

                    final ChannelFuture connectFuture = cb.connect(REMOTE_HOST, REMOTE_PORT);
                    connectFuture.addListener(new ChannelFutureListener() {
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (future.isSuccess()) {
                                Channel newChannel = connectFuture.channel();
                                FrontEndInitializer.outboundChannel=newChannel;
                                writeToChannel(msg, newChannel);
                            } else {
                                logger.error("Error connecting to the Backend Server on " + REMOTE_HOST + ':' + REMOTE_PORT + " ...");
                                logger.error("Retrying connect to the Backend Server on " + REMOTE_HOST + ':' + REMOTE_PORT + " ...");
                                Thread.sleep(10000);
                                writeToChannel(msg, channel);
                            }
                        }
                    });
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
