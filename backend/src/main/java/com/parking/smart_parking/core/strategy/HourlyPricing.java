package com.parking.smart_parking.core.strategy;

// Hourly pricing — charges per hour based on vehicle type
public class HourlyPricing implements PricingStrategy {

    @Override
    public double calculatePrice(String vehicleType, long durationMinutes) {
        double ratePerHour;

        switch (vehicleType.toLowerCase()) {
            case "car":
                ratePerHour = 20.0;
                break;
            case "bike":
                ratePerHour = 10.0;
                break;
            case "truck":
                ratePerHour = 40.0;
                break;
            default:
                ratePerHour = 15.0;
        }

        double hours = Math.ceil(durationMinutes / 60.0);
        double total = hours * ratePerHour;
        System.out.println("STRATEGY [Hourly]: " + vehicleType + " × " + hours + "h × ₹" + ratePerHour + " = ₹" + total);
        return total;
    }

    @Override
    public String getStrategyName() {
        return "Hourly Pricing";
    }
}
