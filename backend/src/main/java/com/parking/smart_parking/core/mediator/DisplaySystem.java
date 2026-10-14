package com.parking.smart_parking.core.mediator;

// Display System component — updates displays across the parking lot
public class DisplaySystem extends ParkingComponent {

    public DisplaySystem(ParkingMediator mediator) {
        super(mediator);
    }

    public void updateAvailability(int available, int total) {
        System.out.println("DISPLAY SYSTEM: Updated — " + available + "/" + total + " spots available.");
    }

    public void showWelcomeMessage(String vehicleNumber, String spotNumber) {
        System.out.println("DISPLAY SYSTEM: Welcome " + vehicleNumber + "! Proceed to spot " + spotNumber);
    }

    public void showGoodbyeMessage(String vehicleNumber) {
        System.out.println("DISPLAY SYSTEM: Goodbye " + vehicleNumber + "! Thank you for parking.");
    }
}
