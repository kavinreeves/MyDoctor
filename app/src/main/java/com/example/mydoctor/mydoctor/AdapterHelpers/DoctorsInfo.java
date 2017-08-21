package com.example.mydoctor.mydoctor.AdapterHelpers;

/**
 * Created by Wenso on 20-Feb-17.
 */

public class DoctorsInfo {

    private String name, spec, experience, city, doctor_id, onlinestatus, photo;

    public DoctorsInfo(String name, String doctor_id, String spec, String experience, String city, String onlinestatus, String photo) {
        this.name = name;
        this.spec = spec;
        this.experience = experience;
        this.city = city;
        this.doctor_id = doctor_id;
        this.onlinestatus = onlinestatus;
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(String doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getOnlinestatus() {
        return onlinestatus;
    }

    public void setOnlinestatus(String onlinestatus) {
        this.onlinestatus = onlinestatus;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
