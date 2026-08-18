package com.parking.smart_parking.core.chain;

public class ValidationChainFactory {

    public static VehicleValidator getChain() {
        // Build chain: Registration → Availability → Type → Permit
        VehicleValidator permitCheck = new AccessPermitCheck();
        VehicleValidator typeCheck = new VehicleTypeCheck(permitCheck);
        VehicleValidator availabilityCheck = new ParkingAvailabilityCheck(typeCheck);
        VehicleValidator registrationCheck = new RegistrationCheck(availabilityCheck);

        return registrationCheck;
    }
}
