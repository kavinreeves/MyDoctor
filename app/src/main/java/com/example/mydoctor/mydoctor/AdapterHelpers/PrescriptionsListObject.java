package com.example.mydoctor.mydoctor.AdapterHelpers;

/**
 * Created by Wenso on 12-Apr-17.
 */

public class PrescriptionsListObject {

    String name, aptId, apptType, dateTime, photoUrl, prescription, patient_id;


    public PrescriptionsListObject(String name, String aptId, String apptType, String dateTime, String photoUrl, String presc_done_status, String patient_id) {
        this.name = name;
        this.aptId = aptId;
        this.apptType = apptType;
        this.dateTime = dateTime;
        this.photoUrl = photoUrl;
        this.prescription = presc_done_status;
        this.patient_id = patient_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAptId() {
        return aptId;
    }

    public void setAptId(String aptId) {
        this.aptId = aptId;
    }

    public String getApptType() {
        return apptType;
    }

    public void setApptType(String apptType) {
        this.apptType = apptType;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }
}
