package com.example.mydoctor.mydoctor.AdapterHelpers;

/**
 * Created by KAVIN on 19-02-2017.
 */

public class ChatMessage {

    private int usersId;
    private String message;
    private String sentAt;
    private String name;
    private String statusType;
    private String opponentPhotoUrl;

    // public constructor

    public ChatMessage(int usersId, String message, String sentAt, String name, String statusType, String opponentPhotoUrl) {

        this.usersId = usersId;
        this.message = message;
        this.sentAt = sentAt;
        this.name = name;
        this.statusType = statusType;
        this.opponentPhotoUrl = opponentPhotoUrl;
    }

    public int getUsersId() {
        return usersId;
    }

    public String getMessage() {
        return message;
    }

    public String getSentAt() {
        return sentAt;
    }

    public String getName() {
        return name;
    }

    public String getStatusType() {
        return statusType;
    }

    public void setStatusType(String statusType) {
        this.statusType = statusType;
    }

    public String getOpponentPhotoUrl() {
        return opponentPhotoUrl;
    }

    public void setOpponentPhotoUrl(String opponentPhotoUrl) {
        this.opponentPhotoUrl = opponentPhotoUrl;
    }
}
