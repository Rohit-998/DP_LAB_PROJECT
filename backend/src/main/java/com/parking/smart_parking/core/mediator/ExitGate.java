package com.parking.smart_parking.core.mediator;

// Exit Gate component — delegates vehicle departure to the mediator
public class ExitGate extends ParkingComponent {

    public ExitGate(ParkingMediator mediator) {
        super(mediator);
    }

    public void vehicleLeaves(String vehicleNumber) {
        System.out.println("EXIT GATE: Vehicle " + vehicleNumber + " detected at exit.");
        mediator.vehicleDeparted(vehicleNumber);
    }

    public void openBarrier() {
        System.out.println("EXIT GATE: Barrier opened. Vehicle may leave.");
    }

    public void displayMessage(String message) {
        System.out.println("EXIT GATE DISPLAY: " + message);
    }
}
