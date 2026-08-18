package com.parking.smart_parking.core.abstractfactory;

public interface ParkingZoneFactory {
    ParkingSpot createSpot(String spotNumber);
    ParkingTicket createTicket(String vehicleNumber);
}
