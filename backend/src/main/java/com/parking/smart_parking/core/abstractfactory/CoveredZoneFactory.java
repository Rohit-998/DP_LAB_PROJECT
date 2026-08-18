package com.parking.smart_parking.core.abstractfactory;

public class CoveredZoneFactory implements ParkingZoneFactory {

    @Override
    public ParkingSpot createSpot(String spotNumber) {
        System.out.println("Created Covered parking spot: " + spotNumber);
        return new CoveredSpot(spotNumber);
    }

    @Override
    public ParkingTicket createTicket(String vehicleNumber) {
        System.out.println("Generated Covered Zone Ticket for: " + vehicleNumber + " (₹50/hr)");
        return new CoveredTicket(vehicleNumber);
    }
}
