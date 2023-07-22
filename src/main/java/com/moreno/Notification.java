package com.moreno;

import java.util.Date;

public class Notification {
    private NotifyType type;
    private NotifyLocation notifyLocation;
    private String title;
    private String message;
    private Date date;

    public Notification(NotifyType type, NotifyLocation notifyLocation, String title, String message) {
        this.type = type;
        this.notifyLocation = notifyLocation;
        this.title = title;
        this.message = message;
        this.date=new Date();
    }

    public Date getDate() {
        return date;
    }

    public NotifyType getType() {
        return type;
    }

    public void setType(NotifyType type) {
        this.type = type;
    }

    public NotifyLocation getLocationNotify() {
        return notifyLocation;
    }

    public void setLocationNotify(NotifyLocation notifyLocation) {
        this.notifyLocation = notifyLocation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
