package com.parking.smart_parking.core.template;

public class CarFeeCalculator extends ParkingFeeCalculator {

    @Override
    protected double getBaseFee() {
        return 30.0; // ₹30 base fee for cars
    }

    @Override
    protected double calculateDurationFee(long durationMinutes) {
        // ₹10 per hour
        return (durationMinutes / 60.0) * 10.0;
    }

    @Override
    protected String getVehicleCategory() {
        return "Car";
    }

    @Override
    protected double applySurcharge(long durationMinutes) {
        // Cars parked over 8 hours get a ₹20 overnight surcharge
        if (durationMinutes > 480) {
            return 20.0;
        }
        return 0;
    }
}
