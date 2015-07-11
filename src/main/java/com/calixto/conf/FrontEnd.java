package com.calixto.conf;

import org.springframework.context.annotation.Configuration;

/**
 * Created by sudheera on 7/11/15.
 */
@Configuration
public class FrontEnd {
    // port that listening to
    private int port;

    public FrontEnd(int port){
        this.port = port;
    }

    public FrontEnd() {
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
