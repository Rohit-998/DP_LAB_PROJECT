package com.parking.smart_parking.core.factory;

public class Truck extends Vehicle {

    public Truck(String vehicleNumber, String ownerName) {
        super(vehicleNumber, ownerName);
    }

    @Override
    public String getType() {
        return "Truck";
    }

    @Override
    public int getSpaceRequired() {
        return 4;
    }
}
