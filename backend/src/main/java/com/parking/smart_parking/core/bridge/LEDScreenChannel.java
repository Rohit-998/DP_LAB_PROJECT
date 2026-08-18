package com.parking.smart_parking.core.bridge;

public class LEDScreenChannel implements NotificationChannel {

    @Override
    public void send(String message) {
        System.out.println("LED SCREEN: " + message);
    }
}
