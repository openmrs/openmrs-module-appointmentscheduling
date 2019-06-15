package org.openmrs.module.appointmentscheduling;

public class AppointmentDailyCount {

    private String date;

    private int dailyCount;

    public AppointmentDailyCount() {
    }

    public AppointmentDailyCount(String date, int dailyCount) {
        setDate(date);
        setDailyCount(dailyCount);
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