package com.calixto.gateway;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 * Created by sudheera on 7/11/15.
 */
public class Constants {
    final static AttributeKey<Channel> inboundChannel = AttributeKey.valueOf("inboundChannel");
}
