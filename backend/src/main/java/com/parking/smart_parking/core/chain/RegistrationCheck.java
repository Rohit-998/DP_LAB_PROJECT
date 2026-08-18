package com.parking.smart_parking.core.chain;

public class RegistrationCheck implements VehicleValidator {

    private VehicleValidator nextValidator;

    public RegistrationCheck(VehicleValidator next) {
        this.nextValidator = next;
    }

    @Override
    public String validate(String vehicleNumber, String vehicleType) {
        if (vehicleNumber == null || vehicleNumber.trim().isEmpty()) {
            System.out.println("REGISTRATION CHECK: Failed — vehicle number is empty.");
            return null;
        }

        System.out.println("REGISTRATION CHECK: Passed — " + vehicleNumber + " is registered.");

        if (nextValidator != null) {
            return nextValidator.validate(vehicleNumber, vehicleType);
        }
        return "RegistrationCheck";
    }
}
