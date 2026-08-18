package com.parking.smart_parking.core.chain;

import com.parking.smart_parking.core.singleton.ParkingLot;

public class ParkingAvailabilityCheck implements VehicleValidator {

    private VehicleValidator nextValidator;

    public ParkingAvailabilityCheck(VehicleValidator next) {
        this.nextValidator = next;
    }

    @Override
    public String validate(String vehicleNumber, String vehicleType) {
        ParkingLot lot = ParkingLot.getInstance();

        if (lot.getAvailableSpots() <= 0) {
            System.out.println("AVAILABILITY CHECK: Failed — parking lot is full.");
            return null;
        }

        System.out.println("AVAILABILITY CHECK: Passed — " + lot.getAvailableSpots() + " spots available.");

        if (nextValidator != null) {
            return nextValidator.validate(vehicleNumber, vehicleType);
        }
        return "ParkingAvailabilityCheck";
    }
}
