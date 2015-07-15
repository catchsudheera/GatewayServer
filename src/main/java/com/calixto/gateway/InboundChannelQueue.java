package com.calixto.gateway;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by sudheera on 7/14/15.
 */
public class InboundChannelQueue {
    private static ConcurrentLinkedDeque<Channel> bus = new ConcurrentLinkedDeque<Channel>();

    //ommit the use of construct
    private InboundChannelQueue(){

    }

    public static ConcurrentLinkedDeque<Channel> getInstance(){
        return bus;
    }
}
