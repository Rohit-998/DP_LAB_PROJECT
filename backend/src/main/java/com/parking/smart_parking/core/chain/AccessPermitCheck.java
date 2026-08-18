package com.parking.smart_parking.core.chain;

public class AccessPermitCheck implements VehicleValidator {

    @Override
    public String validate(String vehicleNumber, String vehicleType) {
        // In a real system, this would check the DB for permit validity
        // For now, all vehicles with a valid number pass
        System.out.println("ACCESS PERMIT CHECK: Passed — " + vehicleNumber + " has a valid permit.");
        return "AccessPermitCheck";
    }
}
