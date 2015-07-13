
package com.calixto.gateway;

import com.calixto.conf.BackEnd;
import com.calixto.conf.FrontEnd;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.net.ConnectException;
import java.net.InetSocketAddress;

public final class Server {
    final static Logger logger = Logger.getLogger(Server.class);
    static final ApplicationContext context = new ClassPathXmlApplicationContext("ServerConfig.xml");

    public static void main(String[] args) throws Exception {
        FrontEnd fConf = (FrontEnd) context.getBean("frontEnd");
        BackEnd bConf = (BackEnd) context.getBean("backEnd");

        final int LOCAL_PORT = fConf.getPort();
        final String REMOTE_HOST = bConf.getAddress();
        final int REMOTE_PORT = bConf.getPort();

        logger.info("Initializing the gateway :" + LOCAL_PORT + " to " + REMOTE_HOST + ':' + REMOTE_PORT + " ...");

        // Configure the bootstrap.
        final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        final EventLoopGroup workerGroup = new NioEventLoopGroup();

        connect(bossGroup, workerGroup, REMOTE_HOST, REMOTE_PORT, LOCAL_PORT);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                logger.info("Shutting down the Server...");
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
                logger.info("Server successfully terminated");
            }
        });
    }

    public static void connect(final EventLoopGroup bossGroup, final EventLoopGroup workerGroup, final String REMOTE_HOST, final int REMOTE_PORT, final int LOCAL_PORT){

        logger.info("Connecting to the Backend Server on " + REMOTE_HOST + ':' + REMOTE_PORT + " ...");
        final Bootstrap cb = new Bootstrap();
        cb.group(workerGroup).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.AUTO_READ, false);
        cb.handler(new BackEndHandlerInitializer());

        final ChannelFuture connectFuture = cb.connect(REMOTE_HOST, REMOTE_PORT);
        connectFuture.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    final Channel outboundChannel=connectFuture.channel();
                    try {
                        ServerBootstrap b = new ServerBootstrap();
                        b.group(bossGroup, workerGroup)
                                .channel(NioServerSocketChannel.class)
                                .option(ChannelOption.TCP_NODELAY, true)
                                .option(ChannelOption.SO_KEEPALIVE, true)
                                .childHandler(new FrontEndInitializer(outboundChannel, REMOTE_HOST, REMOTE_PORT));

                        b.bind(LOCAL_PORT).sync();
                        logger.info("Server initialized and listening on port " + LOCAL_PORT);

                    } catch (Exception e){
                        logger.error("Exception occurred while bootstrapping", e);
                    }
                } else {
                    logger.error("Error connecting to the Backend Server on " + REMOTE_HOST + ':' + REMOTE_PORT + " ...");
                    logger.error("Retrying connect to the Backend Server on " + REMOTE_HOST + ':' + REMOTE_PORT + " ...");
                    Thread.sleep(10000);
                    connect(bossGroup, workerGroup, REMOTE_HOST, REMOTE_PORT, LOCAL_PORT);
                }
            }
        });
    }
}
