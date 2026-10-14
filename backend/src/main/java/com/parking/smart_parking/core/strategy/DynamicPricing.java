package com.parking.smart_parking.core.strategy;

// Dynamic pricing — adjusts price based on occupancy and demand
public class DynamicPricing implements PricingStrategy {

    private double occupancyPercent;

    public DynamicPricing(double occupancyPercent) {
        this.occupancyPercent = occupancyPercent;
    }

    public void setOccupancyPercent(double occupancyPercent) {
        this.occupancyPercent = occupancyPercent;
    }

    @Override
    public double calculatePrice(String vehicleType, long durationMinutes) {
        double baseRate;

        switch (vehicleType.toLowerCase()) {
            case "car":
                baseRate = 15.0;
                break;
            case "bike":
                baseRate = 8.0;
                break;
            case "truck":
                baseRate = 30.0;
                break;
            default:
                baseRate = 12.0;
        }

        // Surge multiplier: 1.0x at 0% occupancy → 3.0x at 100% occupancy
        double surgeMultiplier = 1.0 + (occupancyPercent / 100.0) * 2.0;
        double hours = Math.ceil(durationMinutes / 60.0);
        double total = hours * baseRate * surgeMultiplier;

        System.out.println("STRATEGY [Dynamic]: " + vehicleType +
                " × " + hours + "h × ₹" + baseRate +
                " × " + String.format("%.1f", surgeMultiplier) + "x surge" +
                " (occupancy " + String.format("%.0f", occupancyPercent) + "%)" +
                " = ₹" + String.format("%.2f", total));
        return total;
    }

    @Override
    public String getStrategyName() {
        return "Dynamic Pricing";
    }
}
