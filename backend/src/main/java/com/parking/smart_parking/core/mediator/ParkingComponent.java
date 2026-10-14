package com.parking.smart_parking.core.mediator;

// Base class for all components that participate in the Mediator pattern
public abstract class ParkingComponent {

    protected ParkingMediator mediator;

    public ParkingComponent(ParkingMediator mediator) {
        this.mediator = mediator;
    }

    public void setMediator(ParkingMediator mediator) {
        this.mediator = mediator;
    }
}
