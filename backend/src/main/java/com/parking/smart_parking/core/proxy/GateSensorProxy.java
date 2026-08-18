package com.parking.smart_parking.core.proxy;

import com.parking.smart_parking.core.chain.VehicleValidator;
import com.parking.smart_parking.core.singleton.ParkingLot;

public class GateSensorProxy implements VehicleValidator {

    private VehicleValidator realValidator;

    public GateSensorProxy(VehicleValidator realValidator) {
        this.realValidator = realValidator;
    }

    @Override
    public String validate(String vehicleNumber, String vehicleType) {
        // Pre-check 1: vehicle number not empty
        if (vehicleNumber == null || vehicleNumber.trim().isEmpty()) {
            System.out.println("GATE PROXY: Blocked — no vehicle number detected.");
            return null;
        }

        // Pre-check 2: parking lot is not full
        ParkingLot lot = ParkingLot.getInstance();
        if (lot.getAvailableSpots() <= 0) {
            System.out.println("GATE PROXY: Blocked — parking lot is full.");
            return null;
        }

        // Pre-check 3: vehicle type is provided
        if (vehicleType == null || vehicleType.trim().isEmpty()) {
            System.out.println("GATE PROXY: Blocked — vehicle type not detected.");
            return null;
        }

        System.out.println("GATE PROXY: Pre-checks passed. Forwarding to validation chain...");
        return realValidator.validate(vehicleNumber, vehicleType);
    }
}
