package com.parking.smart_parking.core.observer;

import com.parking.smart_parking.core.bridge.NotificationChannel;

public class ExitDisplayBoard implements ParkingObserver {

    private NotificationChannel channel;

    public ExitDisplayBoard(NotificationChannel channel) {
        this.channel = channel;
    }

    @Override
    public void update(int availableSpots, int totalSpots) {
        String message = "Exit Gate: Thank you! " + availableSpots + "/" + totalSpots + " spots now available";
        channel.send(message);
    }
}
