package com.parking.smart_parking.core.mediator;

// Mediator interface — coordinates communication between parking subsystems
public interface ParkingMediator {

    void vehicleArrived(String vehicleNumber, String vehicleType, String ownerName);

    void vehicleDeparted(String vehicleNumber);

    void notify(ParkingComponent sender, String event);
}
