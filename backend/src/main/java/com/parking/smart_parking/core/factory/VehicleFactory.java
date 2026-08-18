package com.parking.smart_parking.core.factory;

public class VehicleFactory {

    public static Vehicle createVehicle(String vehicleNumber, String vehicleType, String ownerName) {
        switch (vehicleType.toLowerCase()) {
            case "car":
                return new Car(vehicleNumber, ownerName);
            case "bike":
                return new Bike(vehicleNumber, ownerName);
            case "truck":
                return new Truck(vehicleNumber, ownerName);
            default:
                System.out.println("Unknown vehicle type: " + vehicleType);
                return null;
        }
    }
}
