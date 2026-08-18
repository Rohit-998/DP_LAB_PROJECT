package com.parking.smart_parking.core.abstractfactory;

public class OpenTicket extends ParkingTicket {

    public OpenTicket(String vehicleNumber) {
        super(vehicleNumber, "Open");
    }

    @Override
    public double getRate() {
        return 30.0;
    }

    @Override
    public String getTicketType() {
        return "Open Zone Ticket";
    }
}
