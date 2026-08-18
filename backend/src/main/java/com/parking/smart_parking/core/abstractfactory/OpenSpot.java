package com.parking.smart_parking.core.abstractfactory;

public class OpenSpot extends ParkingSpot {

    public OpenSpot(String spotNumber) {
        super(spotNumber, "Open");
    }

    @Override
    public String getZoneType() {
        return "Open (Rooftop)";
    }

    @Override
    public double getHourlyRate() {
        return 30.0;
    }
}
