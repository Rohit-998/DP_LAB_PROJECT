package com.parking.smart_parking.core.singleton;

import com.parking.smart_parking.core.factory.Vehicle;
import com.parking.smart_parking.core.memento.ParkingLotMemento;
import com.parking.smart_parking.core.observer.ParkingObserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParkingLot {

    private static ParkingLot instance;

    private int totalSpots;
    private int availableSpots;
    private Map<String, Vehicle> parkedVehicles;
    private List<ParkingObserver> observers;

    private ParkingLot() {
        this.totalSpots = 100;
        this.availableSpots = 100;
        this.parkedVehicles = new HashMap<>();
        this.observers = new ArrayList<>();
        System.out.println("ParkingLot instance created. Total spots: " + totalSpots);
    }

    public static ParkingLot getInstance() {
        if (instance == null) {
            instance = new ParkingLot();
        }
        return instance;
    }

    // Observer methods
    public void addObserver(ParkingObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(ParkingObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (ParkingObserver observer : observers) {
            observer.update(availableSpots, totalSpots);
        }
    }

    // Parking operations
    public boolean parkVehicle(Vehicle vehicle) {
        if (vehicle == null) return false;

        int spaceNeeded = vehicle.getSpaceRequired();
        if (availableSpots < spaceNeeded) {
            System.out.println("Not enough spots for " + vehicle.getType());
            return false;
        }

        if (parkedVehicles.containsKey(vehicle.getVehicleNumber())) {
            System.out.println("Vehicle " + vehicle.getVehicleNumber() + " is already parked!");
            return false;
        }

        parkedVehicles.put(vehicle.getVehicleNumber(), vehicle);
        availableSpots -= spaceNeeded;
        System.out.println(vehicle.getType() + " " + vehicle.getVehicleNumber() + " parked. Spots left: " + availableSpots);

        notifyObservers();
        return true;
    }

    public boolean removeVehicle(String vehicleNumber) {
        Vehicle vehicle = parkedVehicles.remove(vehicleNumber);
        if (vehicle == null) {
            System.out.println("Vehicle " + vehicleNumber + " not found in parking lot.");
            return false;
        }

        availableSpots += vehicle.getSpaceRequired();
        System.out.println(vehicle.getType() + " " + vehicleNumber + " exited. Spots left: " + availableSpots);

        notifyObservers();
        return true;
    }

    public int getAvailableSpots() {
        return availableSpots;
    }

    public int getTotalSpots() {
        return totalSpots;
    }

    public boolean isVehicleParked(String vehicleNumber) {
        return parkedVehicles.containsKey(vehicleNumber);
    }

    public Map<String, Vehicle> getParkedVehicles() {
        return parkedVehicles;
    }

    // ─── Memento Pattern: Save & Restore state ────────────────────────

    public ParkingLotMemento saveState() {
        ParkingLotMemento memento = new ParkingLotMemento(availableSpots, totalSpots, parkedVehicles);
        System.out.println("MEMENTO: ParkingLot state saved. Available: " + availableSpots + "/" + totalSpots);
        return memento;
    }

    public void restoreState(ParkingLotMemento memento) {
        this.availableSpots = memento.getAvailableSpots();
        this.totalSpots = memento.getTotalSpots();
        this.parkedVehicles = memento.getParkedVehicles();
        System.out.println("MEMENTO: ParkingLot state restored. Available: " + availableSpots + "/" + totalSpots);
        notifyObservers();
    }
}

