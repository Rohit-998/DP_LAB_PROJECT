package com.parking.smart_parking.api.controller;

import com.parking.smart_parking.core.bridge.*;
import com.parking.smart_parking.core.chain.*;
import com.parking.smart_parking.core.db.ParkingDAO;
import com.parking.smart_parking.core.factory.*;
import com.parking.smart_parking.core.observer.*;
import com.parking.smart_parking.core.singleton.ParkingLot;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ParkingController {

    private ParkingLot parkingLot;
    private ParkingDAO dao;

    public ParkingController() {
        this.parkingLot = ParkingLot.getInstance();
        this.dao = new ParkingDAO();

        // Register observers with different bridge channels
        parkingLot.addObserver(new EntryDisplayBoard(new LEDScreenChannel()));
        parkingLot.addObserver(new ExitDisplayBoard(new LEDScreenChannel()));
        parkingLot.addObserver(new AdminDashboard(new MobileAppChannel()));
    }

    // POST /api/entry — Vehicle enters parking
    @PostMapping("/entry")
    public Map<String, Object> vehicleEntry(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        String vehicleNumber = request.get("vehicleNumber");
        String vehicleType = request.get("vehicleType");
        String ownerName = request.get("ownerName");

        // Step 1: Chain of Responsibility validates the vehicle
        VehicleValidator chain = ValidationChainFactory.getChain();
        String validationResult = chain.validate(vehicleNumber, vehicleType);

        if (validationResult == null) {
            response.put("success", false);
            response.put("message", "Vehicle entry denied. Validation failed.");
            return response;
        }

        // Step 2: Factory creates the vehicle
        Vehicle vehicle = VehicleFactory.createVehicle(vehicleNumber, vehicleType, ownerName);
        if (vehicle == null) {
            response.put("success", false);
            response.put("message", "Unknown vehicle type.");
            return response;
        }

        // Step 3: Singleton parks the vehicle + notifies observers
        String spotNumber = "S-" + (int)(Math.random() * 100);
        boolean parked = parkingLot.parkVehicle(vehicle);
        if (!parked) {
            response.put("success", false);
            response.put("message", "Could not park vehicle.");
            return response;
        }

        // Step 4: Save to DB
        dao.insertTransaction(vehicleNumber, vehicleType, spotNumber, "general");

        response.put("success", true);
        response.put("message", vehicle.getType() + " parked successfully!");
        response.put("spotNumber", spotNumber);
        response.put("availableSpots", parkingLot.getAvailableSpots());
        return response;
    }

    // POST /api/exit — Vehicle exits parking
    @PostMapping("/exit")
    public Map<String, Object> vehicleExit(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        String vehicleNumber = request.get("vehicleNumber");

        if (!parkingLot.isVehicleParked(vehicleNumber)) {
            response.put("success", false);
            response.put("message", "Vehicle " + vehicleNumber + " is not parked here.");
            return response;
        }

        boolean removed = parkingLot.removeVehicle(vehicleNumber);
        if (removed) {
            double amount = 50.0; // simplified — in real app, calculate from entry time
            dao.exitTransaction(vehicleNumber, amount);

            response.put("success", true);
            response.put("message", "Vehicle " + vehicleNumber + " exited successfully.");
            response.put("amount", amount);
            response.put("availableSpots", parkingLot.getAvailableSpots());
        } else {
            response.put("success", false);
            response.put("message", "Error removing vehicle.");
        }
        return response;
    }

    // GET /api/spots — Get parking spot info
    @GetMapping("/spots")
    public Map<String, Object> getSpots() {
        Map<String, Object> response = new HashMap<>();
        response.put("totalSpots", parkingLot.getTotalSpots());
        response.put("availableSpots", parkingLot.getAvailableSpots());
        response.put("parkedVehicles", parkingLot.getParkedVehicles().size());
        return response;
    }

    // GET /api/transactions — Get all parking transactions from DB
    @GetMapping("/transactions")
    public List<Map<String, String>> getTransactions() {
        return dao.getAllTransactions();
    }

    // GET /api/transactions/active — Get currently parked vehicles
    @GetMapping("/transactions/active")
    public List<Map<String, String>> getActiveTransactions() {
        return dao.getActiveTransactions();
    }
}
