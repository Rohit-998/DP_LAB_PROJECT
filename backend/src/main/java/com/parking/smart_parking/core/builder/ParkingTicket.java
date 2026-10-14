package com.parking.smart_parking.core.builder;

public class ParkingTicket {

    private String ticketId;
    private String vehicleNumber;
    private String vehicleType;
    private String ownerName;
    private String spotNumber;
    private String zone;
    private String entryTime;
    private String exitTime;
    private double fee;
    private boolean isPaid;

    // Private constructor — only Builder can create instances
    private ParkingTicket() {}

    // Getters
    public String getTicketId() { return ticketId; }
    public String getVehicleNumber() { return vehicleNumber; }
    public String getVehicleType() { return vehicleType; }
    public String getOwnerName() { return ownerName; }
    public String getSpotNumber() { return spotNumber; }
    public String getZone() { return zone; }
    public String getEntryTime() { return entryTime; }
    public String getExitTime() { return exitTime; }
    public double getFee() { return fee; }
    public boolean isPaid() { return isPaid; }

    // Setters for mutable fields (exit time, fee, payment)
    public void setExitTime(String exitTime) { this.exitTime = exitTime; }
    public void setFee(double fee) { this.fee = fee; }
    public void setPaid(boolean paid) { this.isPaid = paid; }

    @Override
    public String toString() {
        return "ParkingTicket{" +
                "ticketId='" + ticketId + '\'' +
                ", vehicle='" + vehicleType + " " + vehicleNumber + '\'' +
                ", owner='" + ownerName + '\'' +
                ", spot='" + spotNumber + '\'' +
                ", zone='" + zone + '\'' +
                ", entry='" + entryTime + '\'' +
                ", exit='" + exitTime + '\'' +
                ", fee=" + fee +
                ", paid=" + isPaid +
                '}';
    }

    // ─── Builder ─────────────────────────────────────────────────
    public static class Builder {

        private ParkingTicket ticket;

        public Builder() {
            ticket = new ParkingTicket();
        }

        public Builder ticketId(String ticketId) {
            ticket.ticketId = ticketId;
            return this;
        }

        public Builder vehicleNumber(String vehicleNumber) {
            ticket.vehicleNumber = vehicleNumber;
            return this;
        }

        public Builder vehicleType(String vehicleType) {
            ticket.vehicleType = vehicleType;
            return this;
        }

        public Builder ownerName(String ownerName) {
            ticket.ownerName = ownerName;
            return this;
        }

        public Builder spotNumber(String spotNumber) {
            ticket.spotNumber = spotNumber;
            return this;
        }

        public Builder zone(String zone) {
            ticket.zone = zone;
            return this;
        }

        public Builder entryTime(String entryTime) {
            ticket.entryTime = entryTime;
            return this;
        }

        public Builder exitTime(String exitTime) {
            ticket.exitTime = exitTime;
            return this;
        }

        public Builder fee(double fee) {
            ticket.fee = fee;
            return this;
        }

        public Builder isPaid(boolean isPaid) {
            ticket.isPaid = isPaid;
            return this;
        }

        public ParkingTicket build() {
            // Validate required fields
            if (ticket.ticketId == null || ticket.vehicleNumber == null) {
                throw new IllegalStateException("Ticket ID and Vehicle Number are required.");
            }
            System.out.println("BUILDER: Ticket built → " + ticket);
            return ticket;
        }
    }
}
