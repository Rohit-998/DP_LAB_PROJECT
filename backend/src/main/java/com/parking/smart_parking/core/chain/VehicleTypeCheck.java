package com.parking.smart_parking.core.chain;

import java.util.Arrays;
import java.util.List;

public class VehicleTypeCheck implements VehicleValidator {

    private VehicleValidator nextValidator;
    private static final List<String> ALLOWED_TYPES = Arrays.asList("car", "bike", "truck");

    public VehicleTypeCheck(VehicleValidator next) {
        this.nextValidator = next;
    }

    @Override
    public String validate(String vehicleNumber, String vehicleType) {
        if (!ALLOWED_TYPES.contains(vehicleType.toLowerCase())) {
            System.out.println("VEHICLE TYPE CHECK: Failed — '" + vehicleType + "' is not allowed.");
            return null;
        }

        System.out.println("VEHICLE TYPE CHECK: Passed — " + vehicleType + " is allowed.");

        if (nextValidator != null) {
            return nextValidator.validate(vehicleNumber, vehicleType);
        }
        return "VehicleTypeCheck";
    }
}
