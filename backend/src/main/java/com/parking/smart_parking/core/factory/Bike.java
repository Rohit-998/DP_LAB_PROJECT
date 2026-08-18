package com.parking.smart_parking.core.factory;

public class Bike extends Vehicle {

    public Bike(String vehicleNumber, String ownerName) {
        super(vehicleNumber, ownerName);
    }

    @Override
    public String getType() {
        return "Bike";
    }

    @Override
    public int getSpaceRequired() {
        return 1;
    }
}
