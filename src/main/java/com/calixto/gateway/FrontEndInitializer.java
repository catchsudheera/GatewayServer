
package com.calixto.gateway;

import com.calixto.codecs.ByteToGatewayMessageDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.SocketChannel;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;

public class FrontEndInitializer extends ChannelInitializer<SocketChannel> {
    final static Logger logger = Logger.getLogger(FrontEndInitializer.class);
    private final String remoteAddress;
    private final int remotePort;

    AbstractChannelPoolMap<InetSocketAddress, SimpleChannelPool> poolMap;


    public FrontEndInitializer(AbstractChannelPoolMap<InetSocketAddress, SimpleChannelPool> poolMap, String remoteAddress, int remotePort) {
        this.poolMap = poolMap;
        this.remoteAddress = remoteAddress;
        this.remotePort = remotePort;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ch.pipeline().addLast(new ByteToGatewayMessageDecoder());
        ch.pipeline().addLast(new FrontendHandler(this.poolMap, remoteAddress, remotePort));
    }
}
