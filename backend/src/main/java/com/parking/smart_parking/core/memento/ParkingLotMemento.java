package com.parking.smart_parking.core.memento;

import com.parking.smart_parking.core.factory.Vehicle;

import java.util.HashMap;
import java.util.Map;

// Memento — immutable snapshot of ParkingLot state at a point in time
public class ParkingLotMemento {

    private final int availableSpots;
    private final int totalSpots;
    private final Map<String, Vehicle> parkedVehicles;
    private final String timestamp;

    public ParkingLotMemento(int availableSpots, int totalSpots, Map<String, Vehicle> parkedVehicles) {
        this.availableSpots = availableSpots;
        this.totalSpots = totalSpots;
        // Deep copy to prevent external mutation
        this.parkedVehicles = new HashMap<>(parkedVehicles);
        this.timestamp = java.time.LocalDateTime.now().toString();
    }

    public int getAvailableSpots() {
        return availableSpots;
    }

    public int getTotalSpots() {
        return totalSpots;
    }

    public Map<String, Vehicle> getParkedVehicles() {
        return new HashMap<>(parkedVehicles);
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Memento{time=" + timestamp +
                ", available=" + availableSpots + "/" + totalSpots +
                ", parked=" + parkedVehicles.size() + " vehicles}";
    }
}
