package com.example.mydoctor.mydoctor.AdapterHelpers;

/**
 * Created by Wenso on 09-Mar-17.
 */

public class AvailableAppointments {

    String time, availability;

    public AvailableAppointments(String time, String availability) {
        this.time = time;
        this.availability = availability;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }
}
