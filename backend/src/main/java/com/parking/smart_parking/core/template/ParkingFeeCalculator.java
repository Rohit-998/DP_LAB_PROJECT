package com.parking.smart_parking.core.template;

// Template Method pattern — defines the skeleton of the fee calculation algorithm.
// Subclasses override specific steps (base fee, surcharge, discount) without changing the overall structure.
public abstract class ParkingFeeCalculator {

    // ─── Template Method (final — subclasses cannot change the algorithm structure) ───
    public final double calculateTotalFee(long durationMinutes) {
        double baseFee = getBaseFee();
        double durationFee = calculateDurationFee(durationMinutes);
        double surcharge = applySurcharge(durationMinutes);
        double discount = applyDiscount(durationMinutes);

        double total = baseFee + durationFee + surcharge - discount;
        if (total < 0) total = 0;

        System.out.println("TEMPLATE [" + getVehicleCategory() + "]: " +
                "Base=₹" + baseFee +
                " + Duration=₹" + durationFee +
                " + Surcharge=₹" + surcharge +
                " - Discount=₹" + discount +
                " = Total=₹" + total);

        return total;
    }

    // ─── Abstract steps — each vehicle type provides its own implementation ───
    protected abstract double getBaseFee();
    protected abstract double calculateDurationFee(long durationMinutes);
    protected abstract String getVehicleCategory();

    // ─── Hook methods — optional overrides with defaults ───
    protected double applySurcharge(long durationMinutes) {
        // Default: No surcharge
        return 0;
    }

    protected double applyDiscount(long durationMinutes) {
        // Default: No discount
        return 0;
    }
}
