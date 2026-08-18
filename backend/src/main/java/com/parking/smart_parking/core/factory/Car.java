package com.parking.smart_parking.core.factory;

public class Car extends Vehicle {

    public Car(String vehicleNumber, String ownerName) {
        super(vehicleNumber, ownerName);
    }

    @Override
    public String getType() {
        return "Car";
    }

    @Override
    public int getSpaceRequired() {
        return 2;
    }
}
