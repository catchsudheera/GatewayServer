
package com.calixto.gateway;

import com.calixto.models.GatewayMessage;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.SimpleChannelPool;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.concurrent.Future;

public class FrontendHandler extends ChannelInboundHandlerAdapter {
    final static Logger logger = Logger.getLogger(FrontendHandler.class);
    private final String remoteAddress;
    private final int remotePort;

    AbstractChannelPoolMap<InetSocketAddress, SimpleChannelPool> poolMap;

    public FrontendHandler(AbstractChannelPoolMap<InetSocketAddress, SimpleChannelPool> poolMap, String remoteAddress, int remotePort) {
        this.poolMap = poolMap;
        this.remoteAddress = remoteAddress;
        this.remotePort = remotePort;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws InterruptedException {

        if (msg instanceof GatewayMessage) {
            GatewayMessage gMessage = (GatewayMessage) msg;

            logger.info("Message Received to gateway server from client length : "+gMessage.getMessageLength()+", message : "+gMessage.getMessage());
            final Channel outboundChannel = getChannel();
            outboundChannel.attr(Constants.inboundChannel).set(ctx.channel());

            /**
             * you can edit thr message here: just get the String message from the msg object
             * and edit it, create a new GatewayMessage object giving the message String, length int
             * and numString as the 4-character long messageLength
             *
             * use the newly created GatewayMessage in following writeAndFlush
             */

            outboundChannel.writeAndFlush(msg);
        }

    }

    private Channel getChannel() throws InterruptedException {
        final SimpleChannelPool pool = poolMap.get(new InetSocketAddress(remoteAddress, remotePort));
        return pool.acquire().sync().getNow();
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
