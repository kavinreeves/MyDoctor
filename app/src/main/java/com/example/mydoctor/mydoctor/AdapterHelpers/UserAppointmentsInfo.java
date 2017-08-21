package com.example.mydoctor.mydoctor.AdapterHelpers;

/**
 * Created by Wenso on 01-Mar-17.
 */

public class UserAppointmentsInfo {

    private String time, date, docName, docSpec, city, photoUrl, status, aptType;

    public UserAppointmentsInfo(String time, String date, String docName, String docSpec, String city, String photoUrl, String status, String aptType) {
        this.time = time;
        this.date = date;
        this.docName = docName;
        this.docSpec = docSpec;
        this.city = city;
        this.photoUrl = photoUrl;

        this.status = status;
        this.aptType = aptType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getDocSpec() {
        return docSpec;
    }

    public void setDocSpec(String docSpec) {
        this.docSpec = docSpec;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAptType() {
        return aptType;
    }

    public void setAptType(String aptType) {
        this.aptType = aptType;
    }
}

