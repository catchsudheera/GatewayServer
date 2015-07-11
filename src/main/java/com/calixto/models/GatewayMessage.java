package com.calixto.models;

/**
 * Created by sudheera on 7/11/15.
 */
public class GatewayMessage {
    final private int messageLength;
    final private String message;
    final private String numString;

    public GatewayMessage(int messageLength, String message, String numString) {
        this.messageLength = messageLength;
        this.message = message;
        this.numString = numString;
    }

    public int getMessageLength() {
        return messageLength;
    }

    public String getMessage() {
        return message;
    }

    public byte[] getByteArray(){
        return (numString+message).getBytes();
    }
}
