package com.parking.smart_parking.core.strategy;

// Context class — holds a PricingStrategy and delegates price calculation to it.
// The strategy can be swapped at runtime.
public class PricingContext {

    private PricingStrategy strategy;

    public PricingContext(PricingStrategy strategy) {
        this.strategy = strategy;
        System.out.println("PRICING CONTEXT: Initialized with strategy — " + strategy.getStrategyName());
    }

    public void setStrategy(PricingStrategy strategy) {
        this.strategy = strategy;
        System.out.println("PRICING CONTEXT: Strategy switched to — " + strategy.getStrategyName());
    }

    public PricingStrategy getStrategy() {
        return strategy;
    }

    public double executeStrategy(String vehicleType, long durationMinutes) {
        return strategy.calculatePrice(vehicleType, durationMinutes);
    }
}
