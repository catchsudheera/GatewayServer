package com.calixto.models;

import io.netty.channel.Channel;

/**
 * Created by sudheera on 7/13/15.
 */
public class QueueElement {
    private final GatewayMessage message;
    private final Channel inboundChannel;

    public QueueElement(GatewayMessage message, Channel channel){
        inboundChannel = channel;
        this.message = message;
    }

    public GatewayMessage getMessage() {
        return message;
    }

    public Channel getInboundChannel() {
        return inboundChannel;
    }
}
