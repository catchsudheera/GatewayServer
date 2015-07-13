
package com.calixto.gateway;

import com.calixto.codecs.ByteToGatewayMessageDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import org.apache.log4j.Logger;


public class FrontEndInitializer extends ChannelInitializer<SocketChannel> {
    final static Logger logger = Logger.getLogger(FrontEndInitializer.class);

    public FrontEndInitializer() {
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ch.pipeline().addLast(new ByteToGatewayMessageDecoder());
        ch.pipeline().addLast(new FrontendHandler());
    }
}
