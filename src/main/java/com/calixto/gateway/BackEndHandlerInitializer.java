package com.calixto.gateway;

import com.calixto.codecs.GatewayMessageToByteEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * Created by sudheera on 7/11/15.
 */
public class BackEndHandlerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new GatewayMessageToByteEncoder());
        pipeline.addLast("bh", new BackendHandler());
    }
}
