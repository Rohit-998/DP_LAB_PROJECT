package com.parking.smart_parking.core.prototype;

import com.parking.smart_parking.core.factory.Vehicle;
import com.parking.smart_parking.core.factory.Car;
import com.parking.smart_parking.core.factory.Bike;
import com.parking.smart_parking.core.factory.Truck;

import java.util.HashMap;
import java.util.Map;

public class VehiclePrototypeRegistry {

    private static Map<String, Vehicle> prototypes = new HashMap<>();

    // Pre-register default prototypes
    static {
        prototypes.put("car", new Car("PROTO-CAR", "Prototype"));
        prototypes.put("bike", new Bike("PROTO-BIKE", "Prototype"));
        prototypes.put("truck", new Truck("PROTO-TRUCK", "Prototype"));
    }

    // Clone a prototype and customize it with actual details
    public static Vehicle getClone(String type, String vehicleNumber, String ownerName) {
        Vehicle prototype = prototypes.get(type.toLowerCase());
        if (prototype == null) {
            System.out.println("Unknown vehicle prototype: " + type);
            return null;
        }

        Vehicle clone = prototype.clone();
        clone.setVehicleNumber(vehicleNumber);
        clone.setOwnerName(ownerName);
        System.out.println("PROTOTYPE: Cloned " + type + " → " + vehicleNumber + " (Owner: " + ownerName + ")");
        return clone;
    }

    // Register a custom prototype at runtime
    public static void addPrototype(String key, Vehicle vehicle) {
        prototypes.put(key.toLowerCase(), vehicle);
        System.out.println("PROTOTYPE: Registered new prototype — " + key);
    }
}
