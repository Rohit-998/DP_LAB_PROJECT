package com.parking.smart_parking.core.factory;

public abstract class Vehicle {
    protected String vehicleNumber;
    protected String ownerName;

    public Vehicle(String vehicleNumber, String ownerName) {
        this.vehicleNumber = vehicleNumber;
        this.ownerName = ownerName;
    }

    public abstract String getType();
    public abstract int getSpaceRequired();

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }
}
