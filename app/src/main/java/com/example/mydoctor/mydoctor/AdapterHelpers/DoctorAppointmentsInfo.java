package com.example.mydoctor.mydoctor.AdapterHelpers;

/**
 * Created by Wenso on 28-Feb-17.
 */

public class DoctorAppointmentsInfo {

    private String time, name, sex, age, city, apt_type, photoUrl, patientId, aptId, patUsername, status;

    public DoctorAppointmentsInfo(String time, String name, String sex, String age, String city, String patient_id, String photoUrl, String patientId, String aptId, String patUsername, String status) {
        this.time = time;
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.city = city;
        this.apt_type = patient_id;
        this.photoUrl = photoUrl;
        this.patientId = patientId;
        this.aptId = aptId;
        this.patUsername = patUsername;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getApt_type() {
        return apt_type;
    }

    public void setApt_type(String apt_type) {
        this.apt_type = apt_type;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getAptId() {
        return aptId;
    }

    public void setAptId(String aptId) {
        this.aptId = aptId;
    }

    public String getPatUsername() {
        return patUsername;
    }

    public void setPatUsername(String patUsername) {
        this.patUsername = patUsername;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
