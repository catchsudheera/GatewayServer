package com.calixto.gateway;

import com.calixto.codecs.GatewayMessageToByteEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.pool.ChannelPoolHandler;

/**
 * Created by sudheera on 7/11/15.
 */
public class BackEndHandlerInitializer implements ChannelPoolHandler {

    public void channelReleased(Channel channel) throws Exception {

    }

    public void channelAcquired(Channel channel) throws Exception {

    }

    public void channelCreated(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new GatewayMessageToByteEncoder());
        pipeline.addLast("bh", new BackendHandler());

    }
}
