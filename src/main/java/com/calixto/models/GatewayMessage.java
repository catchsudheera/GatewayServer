package com.calixto.models;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

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

        DBObject MJsonObj = (DBObject) JSON.parse(this.message);
        MJsonObj.removeField("_id");



        String newMessage = JSON.serialize(MJsonObj);
        int newMessageLength = newMessage.length();
        String newNumString = (new DecimalFormat("0000")).format(newMessageLength);

        return new GatewayMessage(newMessageLength, newMessage, newNumString);


    }

    public GatewayMessage getMessageWithID(String id) {

        DBObject MJsonObj = (DBObject) JSON.parse(this.message);
        MJsonObj.put("_id", id);

        String newMessage = JSON.serialize(MJsonObj);
        int newMessageLength = newMessage.length();
        String newNumString = (new DecimalFormat("0000")).format(newMessageLength);

        return new GatewayMessage(newMessageLength, newMessage, newNumString);
    }

    public String getID() {
        DBObject MJsonObj = (DBObject) JSON.parse(this.message);

        return MJsonObj.get("_id").toString();
    }

}
