package com.parking.smart_parking.core.template;

public class TruckFeeCalculator extends ParkingFeeCalculator {

    @Override
    protected double getBaseFee() {
        return 60.0; // ₹60 base fee for trucks (takes more space)
    }

    @Override
    protected double calculateDurationFee(long durationMinutes) {
        // ₹20 per hour
        return (durationMinutes / 60.0) * 20.0;
    }

    @Override
    protected String getVehicleCategory() {
        return "Truck";
    }

    @Override
    protected double applySurcharge(long durationMinutes) {
        // Trucks get a ₹50 surcharge if parked over 6 hours (heavy vehicle penalty)
        if (durationMinutes > 360) {
            return 50.0;
        }
        return 0;
    }
}
