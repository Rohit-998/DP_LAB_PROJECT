package com.parking.smart_parking.core.bridge;

public class SMSChannel implements NotificationChannel {

    @Override
    public void send(String message) {
        System.out.println("SMS: " + message);
    }
}
