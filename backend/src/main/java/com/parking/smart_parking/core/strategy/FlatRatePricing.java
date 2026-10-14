package com.parking.smart_parking.core.strategy;

// Flat Rate pricing — fixed fee regardless of duration
public class FlatRatePricing implements PricingStrategy {

    @Override
    public double calculatePrice(String vehicleType, long durationMinutes) {
        double flatRate;

        switch (vehicleType.toLowerCase()) {
            case "car":
                flatRate = 100.0;
                break;
            case "bike":
                flatRate = 50.0;
                break;
            case "truck":
                flatRate = 200.0;
                break;
            default:
                flatRate = 80.0;
        }

        System.out.println("STRATEGY [Flat Rate]: " + vehicleType + " = ₹" + flatRate + " (fixed)");
        return flatRate;
    }

    @Override
    public String getStrategyName() {
        return "Flat Rate Pricing";
    }
}
