package com.parking.smart_parking.core.strategy;

// Strategy interface — defines interchangeable pricing algorithms
public interface PricingStrategy {

    double calculatePrice(String vehicleType, long durationMinutes);

    String getStrategyName();
}
