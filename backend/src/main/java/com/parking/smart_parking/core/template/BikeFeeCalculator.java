package com.parking.smart_parking.core.template;

public class BikeFeeCalculator extends ParkingFeeCalculator {

    @Override
    protected double getBaseFee() {
        return 10.0; // ₹10 base fee for bikes
    }

    @Override
    protected double calculateDurationFee(long durationMinutes) {
        // ₹5 per hour
        return (durationMinutes / 60.0) * 5.0;
    }

    @Override
    protected String getVehicleCategory() {
        return "Bike";
    }

    @Override
    protected double applyDiscount(long durationMinutes) {
        // Bikes get a ₹5 discount if parked under 2 hours
        if (durationMinutes <= 120) {
            return 5.0;
        }
        return 0;
    }
}
