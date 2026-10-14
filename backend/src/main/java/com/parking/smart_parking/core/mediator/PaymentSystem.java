package com.parking.smart_parking.core.mediator;

// Payment System component — handles fee calculation and payment processing
public class PaymentSystem extends ParkingComponent {

    public PaymentSystem(ParkingMediator mediator) {
        super(mediator);
    }

    public double processPayment(String vehicleNumber, double amount) {
        System.out.println("PAYMENT SYSTEM: Processing ₹" + amount + " for vehicle " + vehicleNumber);
        // In a real system, this would connect to a payment gateway
        System.out.println("PAYMENT SYSTEM: Payment successful for " + vehicleNumber);
        mediator.notify(this, "PAYMENT_COMPLETE");
        return amount;
    }

    public void displayReceipt(String vehicleNumber, double amount) {
        System.out.println("PAYMENT SYSTEM: Receipt — Vehicle: " + vehicleNumber + " | Amount: ₹" + amount);
    }
}
