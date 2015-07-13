
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

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new FrontEndInitializer());

            b.bind(LOCAL_PORT).sync();

            logger.info("Server initialized and listening on port " + LOCAL_PORT);

        } catch (Exception e){
            logger.error("Exception occurred while bootstrapping", e);
        }

        new GatewaySender(workerGroup, REMOTE_HOST, REMOTE_PORT);

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
}
