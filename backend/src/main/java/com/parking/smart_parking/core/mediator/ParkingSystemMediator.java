package com.parking.smart_parking.core.mediator;

import com.parking.smart_parking.core.chain.ValidationChainFactory;
import com.parking.smart_parking.core.chain.VehicleValidator;
import com.parking.smart_parking.core.factory.Vehicle;
import com.parking.smart_parking.core.factory.VehicleFactory;
import com.parking.smart_parking.core.singleton.ParkingLot;

// Concrete Mediator — coordinates EntryGate, ExitGate, PaymentSystem, DisplaySystem
// None of these components talk to each other directly; all communication flows through this mediator.
public class ParkingSystemMediator implements ParkingMediator {

    private EntryGate entryGate;
    private ExitGate exitGate;
    private PaymentSystem paymentSystem;
    private DisplaySystem displaySystem;

    private ParkingLot parkingLot;

    public ParkingSystemMediator() {
        this.parkingLot = ParkingLot.getInstance();

        // Create components and register them with this mediator
        this.entryGate = new EntryGate(this);
        this.exitGate = new ExitGate(this);
        this.paymentSystem = new PaymentSystem(this);
        this.displaySystem = new DisplaySystem(this);
    }

    @Override
    public void vehicleArrived(String vehicleNumber, String vehicleType, String ownerName) {
        System.out.println("MEDIATOR: Coordinating vehicle entry for " + vehicleNumber);

        // Step 1: Validate via Chain of Responsibility
        VehicleValidator chain = ValidationChainFactory.getChain();
        String result = chain.validate(vehicleNumber, vehicleType);
        if (result == null) {
            entryGate.displayMessage("Entry denied for " + vehicleNumber);
            return;
        }

        // Step 2: Create vehicle via Factory
        Vehicle vehicle = VehicleFactory.createVehicle(vehicleNumber, vehicleType, ownerName);
        if (vehicle == null) {
            entryGate.displayMessage("Unknown vehicle type: " + vehicleType);
            return;
        }

        // Step 3: Park vehicle
        String spotNumber = "S-" + (int)(Math.random() * 100);
        boolean parked = parkingLot.parkVehicle(vehicle);
        if (parked) {
            displaySystem.showWelcomeMessage(vehicleNumber, spotNumber);
            displaySystem.updateAvailability(parkingLot.getAvailableSpots(), parkingLot.getTotalSpots());
        } else {
            entryGate.displayMessage("Parking full. Cannot park " + vehicleNumber);
        }
    }

    @Override
    public void vehicleDeparted(String vehicleNumber) {
        System.out.println("MEDIATOR: Coordinating vehicle exit for " + vehicleNumber);

        // Step 1: Process payment
        double amount = 50.0; // Simplified
        paymentSystem.processPayment(vehicleNumber, amount);
        paymentSystem.displayReceipt(vehicleNumber, amount);

        // Step 2: Remove vehicle
        parkingLot.removeVehicle(vehicleNumber);

        // Step 3: Update displays and open barrier
        displaySystem.showGoodbyeMessage(vehicleNumber);
        displaySystem.updateAvailability(parkingLot.getAvailableSpots(), parkingLot.getTotalSpots());
        exitGate.openBarrier();
    }

    @Override
    public void notify(ParkingComponent sender, String event) {
        if ("PAYMENT_COMPLETE".equals(event)) {
            System.out.println("MEDIATOR: Payment complete event received. Opening exit barrier.");
            exitGate.openBarrier();
        }
    }

    // Getters for components
    public EntryGate getEntryGate() { return entryGate; }
    public ExitGate getExitGate() { return exitGate; }
    public PaymentSystem getPaymentSystem() { return paymentSystem; }
    public DisplaySystem getDisplaySystem() { return displaySystem; }
}
