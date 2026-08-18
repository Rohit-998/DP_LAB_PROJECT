package com.parking.smart_parking.core.abstractfactory;

public class CoveredSpot extends ParkingSpot {

    public CoveredSpot(String spotNumber) {
        super(spotNumber, "Covered");
    }

    @Override
    public String getZoneType() {
        return "Covered (Basement)";
    }

    @Override
    public double getHourlyRate() {
        return 50.0;
    }
}
