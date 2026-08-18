package com.parking.smart_parking.core.abstractfactory;

public abstract class ParkingTicket {
    protected String vehicleNumber;
    protected String zone;
    protected long entryTime;

    public ParkingTicket(String vehicleNumber, String zone) {
        this.vehicleNumber = vehicleNumber;
        this.zone = zone;
        this.entryTime = System.currentTimeMillis();
    }

    public abstract double getRate();
    public abstract String getTicketType();

    public double calculateAmount() {
        long duration = System.currentTimeMillis() - entryTime;
        long hours = Math.max(1, duration / (1000 * 60 * 60)); // minimum 1 hour
        return hours * getRate();
    }

    public String getVehicleNumber() { return vehicleNumber; }
    public String getZone() { return zone; }
    public long getEntryTime() { return entryTime; }
}
