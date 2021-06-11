package com.example.beetalk.Models;

public class Message {
    private int feeling = -1;
    private String message, photoUrl, senderId;
    private long time;
    private String messageId;

    public Message(int feeling, String message, String photoUrl, String senderId, long time) {
        this.feeling = feeling;
        this.message = message;
        this.photoUrl = photoUrl;
        this.senderId = senderId;
        this.time = time;
    }

    public int getFeeling() {
        return feeling;
    }

    public void setFeeling(int feeling) {
        this.feeling = feeling;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
