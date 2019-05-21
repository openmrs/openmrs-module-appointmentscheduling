package org.openmrs.module.appointmentscheduling;

import java.util.Date;
import java.util.List;

public class AppointmentDailyCount {

    private String date;

    private int dailyCount;

    public AppointmentDailyCount() {
    }

    public AppointmentDailyCount(String date, int dailyCount) {
        this.date = date;
        this.dailyCount = dailyCount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDailyCount() {
        return dailyCount;
    }

    public void setDailyCount(int dailyCount) {
        this.dailyCount = dailyCount;
    }
}
