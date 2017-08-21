package com.example.mydoctor.mydoctor.AdapterHelpers;

/**
 * Created by Wenso on 05-May-17.
 */

public class DoctorBookingsObj {

    String patName, aptId, patientId, status, dateTime, ageGender, photo, aptType;

    public DoctorBookingsObj(String patName, String aptId, String patientId, String status, String dateTime, String ageGender, String photo, String aptType) {
        this.patName = patName;
        this.aptId = aptId;
        this.patientId = patientId;
        this.status = status;
        this.dateTime = dateTime;
        this.ageGender = ageGender;
        this.photo = photo;
        this.aptType = aptType;
    }

    public String getPatName() {
        return patName;
    }

    public void setPatName(String patName) {
        this.patName = patName;
    }

    public String getAptId() {
        return aptId;
    }

    public void setAptId(String aptId) {
        this.aptId = aptId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getAgeGender() {
        return ageGender;
    }

    public void setAgeGender(String ageGender) {
        this.ageGender = ageGender;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getAptType() {
        return aptType;
    }

    public void setAptType(String aptType) {
        this.aptType = aptType;
    }
}
