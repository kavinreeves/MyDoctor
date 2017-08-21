package com.example.mydoctor.mydoctor.AdapterHelpers;

/**
 * Created by Wenso on 03-May-17.
 */

public class DoctorAllAppointmentsObj {

    String patName, patAgeGender, patLocation, aptType, aptStatus, dateTime;

    public DoctorAllAppointmentsObj(String patName, String patAgeGender, String patLocation, String aptType, String aptStatus, String dateTime) {
        this.patName = patName;
        this.patAgeGender = patAgeGender;
        this.patLocation = patLocation;
        this.aptType = aptType;
        this.aptStatus = aptStatus;
        this.dateTime = dateTime;
    }

    public String getPatName() {
        return patName;
    }

    public void setPatName(String patName) {
        this.patName = patName;
    }

    public String getPatAgeGender() {
        return patAgeGender;
    }

    public void setPatAgeGender(String patAgeGender) {
        this.patAgeGender = patAgeGender;
    }

    public String getPatLocation() {
        return patLocation;
    }

    public void setPatLocation(String patLocation) {
        this.patLocation = patLocation;
    }

    public String getAptType() {
        return aptType;
    }

    public void setAptType(String aptType) {
        this.aptType = aptType;
    }

    public String getAptStatus() {
        return aptStatus;
    }

    public void setAptStatus(String aptStatus) {
        this.aptStatus = aptStatus;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
