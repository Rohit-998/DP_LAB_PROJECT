package com.parking.smart_parking.core.abstractfactory;

public class OpenZoneFactory implements ParkingZoneFactory {

    @Override
    public ParkingSpot createSpot(String spotNumber) {
        System.out.println("Created Open parking spot: " + spotNumber);
        return new OpenSpot(spotNumber);
    }

    @Override
    public ParkingTicket createTicket(String vehicleNumber) {
        System.out.println("Generated Open Zone Ticket for: " + vehicleNumber + " (₹30/hr)");
        return new OpenTicket(vehicleNumber);
    }
}
