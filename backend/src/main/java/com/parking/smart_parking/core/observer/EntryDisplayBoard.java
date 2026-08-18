package com.parking.smart_parking.core.observer;

import com.parking.smart_parking.core.bridge.NotificationChannel;

public class EntryDisplayBoard implements ParkingObserver {

    private NotificationChannel channel;

    public EntryDisplayBoard(NotificationChannel channel) {
        this.channel = channel;
    }

    @Override
    public void update(int availableSpots, int totalSpots) {
        String message = "Entry Gate: " + availableSpots + "/" + totalSpots + " spots available";
        channel.send(message);
    }
}
