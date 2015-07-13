
package com.calixto.gateway;

import com.calixto.codecs.ByteToGatewayMessageDecoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.SocketChannel;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;

public class FrontEndInitializer extends ChannelInitializer<SocketChannel> {
    final static Logger logger = Logger.getLogger(FrontEndInitializer.class);

    AbstractChannelPoolMap<InetSocketAddress, SimpleChannelPool> poolMap;
    public static volatile Channel outboundChannel;
    private String REMOTE_HOST;
    private int REMOTE_PORT;


    public FrontEndInitializer(Channel outboundChannel, String REMOTE_HOST, int REMOTE_PORT) {
        this.outboundChannel=outboundChannel;
        this.REMOTE_HOST = REMOTE_HOST;
        this.REMOTE_PORT = REMOTE_PORT;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ch.pipeline().addLast(new ByteToGatewayMessageDecoder());
        ch.pipeline().addLast(new FrontendHandler(outboundChannel, REMOTE_HOST, REMOTE_PORT));
    }
}
