package com.calixto.gateway;

import com.calixto.models.QueueElement;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.log4j.Logger;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by sudheera on 7/14/15.
 */
public class GatewaySender {
    final static Logger logger = Logger.getLogger(GatewaySender.class);
    public volatile Channel outboundChannel;
    final AtomicBoolean connection = new AtomicBoolean();
    final ConcurrentLinkedDeque<QueueElement> queue = MessageQueue.getInstance();
    final ConcurrentLinkedDeque<Channel> inboundChannelQueue = InboundChannelQueue.getInstance();

    public GatewaySender(final EventLoopGroup workers, final String remoteHost, final int remotePort) throws InterruptedException {
        initChannel(workers, remoteHost, remotePort);
        connection.set(false);


        while (true) {
            if (queue.peek() != null && connection.get()) {
                QueueElement element = queue.poll();
                sendToChannel(element, outboundChannel, workers, remoteHost, remotePort);
            } else {
                logger.trace("Queue is empty");
                Thread.sleep(2);
            }
        }
    }

    private void sendToChannel(final QueueElement element, final Channel channel, final EventLoopGroup workers, final String remoteHost, final int remotePort) {
        logger.info("Sending the message to back end server");
        inboundChannelQueue.add(element.getInboundChannel());
        channel.writeAndFlush(element.getMessage()).addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()){
                    logger.info("2. message sending to back end server is successful");
                } else {

                    inboundChannelQueue.removeLastOccurrence(element.getInboundChannel());
                    queue.addFirst(element);
                }
            }
        });
    }


    private void initChannel(final EventLoopGroup workers, final String remoteHost, final int remotePort) {
        logger.info("Connecting to the Backend Server on " + remoteHost + ':' + remotePort + " ...");
        final Bootstrap cb = new Bootstrap();
        cb.group(workers).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true);
        cb.handler(new BackEndHandlerInitializer());

        final ChannelFuture connectFuture = cb.connect(remoteHost, remotePort);
        connectFuture.addListener(new ChannelFutureListener() {

            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()){
                    outboundChannel=connectFuture.channel();
                    connection.set(true);
                    outboundChannel.closeFuture().addListener(new ChannelFutureListener() {
                        public void operationComplete(ChannelFuture future) throws Exception {
                            connection.set(false);
                            initChannel(workers, remoteHost, remotePort);
                        }
                    });
                    logger.info("Connected to the Backend Server on " + remoteHost + ':' + remotePort + " ...");
                }else {
                    logger.error("Error connecting to the Backend Server on " + remoteHost + ':' + remotePort + " ...");
                    logger.info("Retrying connect to the Backend Server on " + remoteHost + ':' + remotePort + " ...");
                    workers.schedule(new Runnable() {
                        public void run() {
                            initChannel(workers, remoteHost, remotePort);
                        }
                    }, 2, TimeUnit.SECONDS);

                }

            }
        });
    }
}
