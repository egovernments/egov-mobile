package com.egovernments.egov.models;


import java.util.Calendar;

public class Notification {


    private String title;
    private String details;
    private Calendar time;
    private int forwarding_activity;

    public Notification(String title, String details, Calendar time, int forwarding_activity) {
        this.title = title;
        this.details = details;
        this.time = time;
        this.forwarding_activity = forwarding_activity;
    }

    public String getDetails() {
        return details;
    }

    public Calendar getTime() {
        return time;
    }

    public int getForwarding_activity() {
        return forwarding_activity;
    }

    public String getTitle() {
        return title;
    }

}
