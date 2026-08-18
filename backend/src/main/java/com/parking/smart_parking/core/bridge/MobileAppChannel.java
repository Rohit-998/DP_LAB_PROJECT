package com.parking.smart_parking.core.bridge;

public class MobileAppChannel implements NotificationChannel {

    @Override
    public void send(String message) {
        System.out.println("MOBILE APP: " + message);
    }
}
