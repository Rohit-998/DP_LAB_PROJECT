package com.parking.smart_parking.core.abstractfactory;

public abstract class ParkingSpot {
    protected String spotNumber;
    protected String zone;
    protected boolean isOccupied;
    protected String vehicleNumber;

    public ParkingSpot(String spotNumber, String zone) {
        this.spotNumber = spotNumber;
        this.zone = zone;
        this.isOccupied = false;
        this.vehicleNumber = null;
    }

    public abstract String getZoneType();
    public abstract double getHourlyRate();

    public void occupy(String vehicleNumber) {
        this.isOccupied = true;
        this.vehicleNumber = vehicleNumber;
    }

    public void free() {
        this.isOccupied = false;
        this.vehicleNumber = null;
    }

    public String getSpotNumber() { return spotNumber; }
    public String getZone() { return zone; }
    public boolean isOccupied() { return isOccupied; }
    public String getVehicleNumber() { return vehicleNumber; }
}
