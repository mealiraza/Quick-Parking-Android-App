package com.example.crashtest;

public class NotificationType{

    private String notificationName;
    private String notificationData;

    public NotificationType(String notificationName, String notificationData) {
        this.notificationName = notificationName;
        this.notificationData = notificationData;
    }

    public String getNotificationName() {
        return notificationName;
    }

    public void setNotificationName(String notificationName) {
        this.notificationName = notificationName;
    }

    public String getNotificationData() {
        return notificationData;
    }

    public void setNotificationData(String notificationData) {
        this.notificationData = notificationData;
    }
}