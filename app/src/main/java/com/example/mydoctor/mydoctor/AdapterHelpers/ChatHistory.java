package com.example.mydoctor.mydoctor.AdapterHelpers;

/**
 * Created by Wenso on 16-Mar-17.
 */

public class ChatHistory {

    String senderName, gender, photo, onlinestatus, lastonline;
    int toUserid;


    public ChatHistory(String senderName, String gender, String photo, String onlinestatus, String lastonline, int toUserid) {
        this.senderName = senderName;
        this.gender = gender;
        this.photo = photo;
        this.onlinestatus = onlinestatus;
        this.lastonline = lastonline;
        this.toUserid = toUserid;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getOnlinestatus() {
        return onlinestatus;
    }

    public void setOnlinestatus(String onlinestatus) {
        this.onlinestatus = onlinestatus;
    }

    public String getLastonline() {
        return lastonline;
    }

    public void setLastonline(String lastonline) {
        this.lastonline = lastonline;
    }

    public int getToUserid() {
        return toUserid;
    }

    public void setToUserid(int toUserid) {
        this.toUserid = toUserid;
    }
}
