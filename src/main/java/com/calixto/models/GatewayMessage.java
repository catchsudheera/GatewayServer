package com.calixto.models;

import org.json.JSONObject;

import java.text.DecimalFormat;

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

    public GatewayMessage getMessageWithoutId() {

        JSONObject jsonObject = new JSONObject(this.message);
        jsonObject.remove("_id");
        String newMessage = jsonObject.toString() ;
        int newMessageLength = newMessage.length();
        String newNumString = (new DecimalFormat("0000")).format(newMessageLength);

        return new GatewayMessage(newMessageLength, newMessage, newNumString);


    }

    public GatewayMessage getMessageWithID(String id) {
        JSONObject jsonObject = new JSONObject(this.message);
        jsonObject.accumulate("_id", id);

        String newMessage = jsonObject.toString();
        int newMessageLength = newMessage.length();
        String newNumString = (new DecimalFormat("0000")).format(newMessageLength);

        return new GatewayMessage(newMessageLength, newMessage, newNumString);
    }

    public String getID() {
        JSONObject jsonObject = new JSONObject(this.message);

        return jsonObject.getString("_id");
    }

}
