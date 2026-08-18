package com.parking.smart_parking.core.observer;

public interface ParkingObserver {
    void update(int availableSpots, int totalSpots);
}
