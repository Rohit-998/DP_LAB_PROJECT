package com.parking.smart_parking.core.abstractfactory;

public class CoveredTicket extends ParkingTicket {

    public CoveredTicket(String vehicleNumber) {
        super(vehicleNumber, "Covered");
    }

    @Override
    public double getRate() {
        return 50.0;
    }

    @Override
    public String getTicketType() {
        return "Covered Zone Ticket";
    }
}
