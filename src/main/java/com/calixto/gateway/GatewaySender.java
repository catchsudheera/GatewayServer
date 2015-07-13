package com.calixto.gateway;

import com.calixto.models.QueueElement;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.log4j.Logger;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

/**
 * Created by sudheera on 7/14/15.
 */
public class GatewaySender {
    final static Logger logger = Logger.getLogger(GatewaySender.class);
    public volatile Channel outboundChannel;
    public GatewaySender(final EventLoopGroup workers, final String remoteHost, final int remotePort){
        initChannel(workers, remoteHost, remotePort);
        final ConcurrentLinkedDeque<QueueElement> queue = MessageQueue.getInstance();

        workers.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                if (queue.peek() != null) {
                    QueueElement element = queue.poll();
                    sendToChannel(element, outboundChannel, workers, remoteHost, remotePort);
                } else {
                    logger.trace("Queue is empty");
                }
            }
        }, 10, 10, TimeUnit.MILLISECONDS);
    }

    private void sendToChannel(final QueueElement element, final Channel channel, final EventLoopGroup workers, final String remoteHost, final int remotePort) {
        logger.info("Sending the message to back end server");
        channel.attr(Constants.inboundChannel).set(element.getInboundChannel());
        channel.writeAndFlush(element.getMessage()).addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()){
                    logger.info("2. message sending to back end server is successful");
                } else {
                    logger.error("Error sending to the Backend Server on " + remoteHost + ':' + remotePort + " ...");
                    logger.info("Retrying send to the Backend Server on " + remoteHost + ':' + remotePort + " ...");

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
                                sendToChannel(element, outboundChannel, workers,remoteHost, remotePort);
                            } else {
                                workers.schedule(new Runnable() {
                                    public void run() {
                                        sendToChannel(element,outboundChannel, workers, remoteHost, remotePort);
                                    }
                                }, 100, TimeUnit.MILLISECONDS);
                            }

                        }
                    });


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
                    logger.info("Connected to the Backend Server on " + remoteHost + ':' + remotePort + " ...");
                }else {
                    logger.error("Error connecting to the Backend Server on " + remoteHost + ':' + remotePort + " ...");
                    logger.info("Retrying connect to the Backend Server on " + remoteHost + ':' + remotePort + " ...");
                    workers.schedule(new Runnable() {
                        public void run() {
                            initChannel(workers, remoteHost, remotePort);
                        }
                    }, 5, TimeUnit.SECONDS);

                }

            }
        });




    }
}
