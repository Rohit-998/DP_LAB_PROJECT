package com.parking.smart_parking.core.observer;

import com.parking.smart_parking.core.bridge.NotificationChannel;

public class AdminDashboard implements ParkingObserver {

    private NotificationChannel channel;

    public AdminDashboard(NotificationChannel channel) {
        this.channel = channel;
    }

    @Override
    public void update(int availableSpots, int totalSpots) {
        int occupied = totalSpots - availableSpots;
        int occupancy = (occupied * 100) / totalSpots;
        String message = "Admin Dashboard: Occupancy " + occupancy + "% (" + occupied + "/" + totalSpots + " occupied)";
        channel.send(message);
    }
}
