package com.parking.smart_parking.core.mediator;

// Entry Gate component — delegates vehicle arrival to the mediator
public class EntryGate extends ParkingComponent {

    public EntryGate(ParkingMediator mediator) {
        super(mediator);
    }

    public void vehicleArrives(String vehicleNumber, String vehicleType, String ownerName) {
        System.out.println("ENTRY GATE: Vehicle " + vehicleNumber + " detected at entry.");
        mediator.vehicleArrived(vehicleNumber, vehicleType, ownerName);
    }

    public void displayMessage(String message) {
        System.out.println("ENTRY GATE DISPLAY: " + message);
    }
}
