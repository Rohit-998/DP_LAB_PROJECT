package com.parking.smart_parking.api.controller;

import com.parking.smart_parking.core.bridge.*;
import com.parking.smart_parking.core.builder.ParkingTicket;
import com.parking.smart_parking.core.chain.*;
import com.parking.smart_parking.core.db.ParkingDAO;
import com.parking.smart_parking.core.factory.*;
import com.parking.smart_parking.core.mediator.*;
import com.parking.smart_parking.core.memento.ParkingLotCaretaker;
import com.parking.smart_parking.core.memento.ParkingLotMemento;
import com.parking.smart_parking.core.observer.*;
import com.parking.smart_parking.core.prototype.VehiclePrototypeRegistry;
import com.parking.smart_parking.core.singleton.ParkingLot;
import com.parking.smart_parking.core.strategy.*;
import com.parking.smart_parking.core.template.*;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ParkingController {

    private ParkingLot parkingLot;
    private ParkingDAO dao;

    // NEW: Mediator — coordinates subsystems
    private ParkingSystemMediator mediator;

    // NEW: Strategy — swappable pricing
    private PricingContext pricingContext;

    // NEW: Memento — undo / state snapshots
    private ParkingLotCaretaker caretaker;

    // NEW: Tickets built via Builder
    private Map<String, ParkingTicket> activeTickets;

    public ParkingController() {
        this.parkingLot = ParkingLot.getInstance();
        this.dao = new ParkingDAO();

        // Register observers with different bridge channels
        parkingLot.addObserver(new EntryDisplayBoard(new LEDScreenChannel()));
        parkingLot.addObserver(new ExitDisplayBoard(new LEDScreenChannel()));
        parkingLot.addObserver(new AdminDashboard(new MobileAppChannel()));

        // Initialize Mediator
        this.mediator = new ParkingSystemMediator();

        // Initialize Strategy with default Hourly pricing
        this.pricingContext = new PricingContext(new HourlyPricing());

        // Initialize Memento Caretaker
        this.caretaker = new ParkingLotCaretaker();

        // Initialize ticket store
        this.activeTickets = new HashMap<>();
    }

    // ═══════════════════════════════════════════════════════════════════
    //  EXISTING ENDPOINTS (Factory + Chain + Singleton + Observer + Bridge)
    // ═══════════════════════════════════════════════════════════════════

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

        // MEMENTO: Save state before modification
        caretaker.save(parkingLot.saveState());

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

        // BUILDER: Create a ParkingTicket for this entry
        ParkingTicket ticket = new ParkingTicket.Builder()
                .ticketId("TKT-" + System.currentTimeMillis())
                .vehicleNumber(vehicleNumber)
                .vehicleType(vehicleType)
                .ownerName(ownerName)
                .spotNumber(spotNumber)
                .zone("general")
                .entryTime(LocalDateTime.now().toString())
                .fee(0)
                .isPaid(false)
                .build();
        activeTickets.put(vehicleNumber, ticket);

        response.put("success", true);
        response.put("message", vehicle.getType() + " parked successfully!");
        response.put("spotNumber", spotNumber);
        response.put("ticketId", ticket.getTicketId());
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

        // MEMENTO: Save state before modification
        caretaker.save(parkingLot.saveState());

        // STRATEGY: Calculate fee using current pricing strategy
        long durationMinutes = 120; // Simplified — in real app, calculate from entry time
        double amount = pricingContext.executeStrategy(
                parkingLot.getParkedVehicles().get(vehicleNumber).getType(),
                durationMinutes
        );

        boolean removed = parkingLot.removeVehicle(vehicleNumber);
        if (removed) {
            dao.exitTransaction(vehicleNumber, amount);

            // BUILDER: Update the ticket with exit info
            ParkingTicket ticket = activeTickets.get(vehicleNumber);
            if (ticket != null) {
                ticket.setExitTime(LocalDateTime.now().toString());
                ticket.setFee(amount);
                ticket.setPaid(true);
            }

            response.put("success", true);
            response.put("message", "Vehicle " + vehicleNumber + " exited successfully.");
            response.put("amount", amount);
            response.put("strategy", pricingContext.getStrategy().getStrategyName());
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

    // ═══════════════════════════════════════════════════════════════════
    //  PROTOTYPE PATTERN — Clone vehicles from prototypes
    // ═══════════════════════════════════════════════════════════════════

    // POST /api/prototype/clone — Create a vehicle by cloning a prototype
    @PostMapping("/prototype/clone")
    public Map<String, Object> cloneVehicle(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        String vehicleNumber = request.get("vehicleNumber");
        String vehicleType = request.get("vehicleType");
        String ownerName = request.get("ownerName");

        Vehicle cloned = VehiclePrototypeRegistry.getClone(vehicleType, vehicleNumber, ownerName);
        if (cloned == null) {
            response.put("success", false);
            response.put("message", "Failed to clone vehicle prototype: " + vehicleType);
            return response;
        }

        // MEMENTO: Save state before modification
        caretaker.save(parkingLot.saveState());

        String spotNumber = "S-" + (int)(Math.random() * 100);
        boolean parked = parkingLot.parkVehicle(cloned);

        if (parked) {
            dao.insertTransaction(vehicleNumber, vehicleType, spotNumber, "general");

            response.put("success", true);
            response.put("message", "Prototype cloned and parked: " + cloned.getType() + " " + vehicleNumber);
            response.put("spotNumber", spotNumber);
            response.put("availableSpots", parkingLot.getAvailableSpots());
        } else {
            response.put("success", false);
            response.put("message", "Could not park cloned vehicle.");
        }
        return response;
    }

    // ═══════════════════════════════════════════════════════════════════
    //  BUILDER PATTERN — Get ticket info
    // ═══════════════════════════════════════════════════════════════════

    // GET /api/ticket/{vehicleNumber} — Get a parking ticket built via Builder
    @GetMapping("/ticket/{vehicleNumber}")
    public Map<String, Object> getTicket(@PathVariable String vehicleNumber) {
        Map<String, Object> response = new HashMap<>();
        ParkingTicket ticket = activeTickets.get(vehicleNumber);

        if (ticket == null) {
            response.put("success", false);
            response.put("message", "No ticket found for " + vehicleNumber);
            return response;
        }

        response.put("success", true);
        response.put("ticketId", ticket.getTicketId());
        response.put("vehicleNumber", ticket.getVehicleNumber());
        response.put("vehicleType", ticket.getVehicleType());
        response.put("ownerName", ticket.getOwnerName());
        response.put("spotNumber", ticket.getSpotNumber());
        response.put("zone", ticket.getZone());
        response.put("entryTime", ticket.getEntryTime());
        response.put("exitTime", ticket.getExitTime());
        response.put("fee", ticket.getFee());
        response.put("paid", ticket.isPaid());
        return response;
    }

    // ═══════════════════════════════════════════════════════════════════
    //  MEDIATOR PATTERN — Coordinate subsystems via mediator
    // ═══════════════════════════════════════════════════════════════════

    // POST /api/mediator/entry — Vehicle entry coordinated via Mediator
    @PostMapping("/mediator/entry")
    public Map<String, Object> mediatorEntry(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        String vehicleNumber = request.get("vehicleNumber");
        String vehicleType = request.get("vehicleType");
        String ownerName = request.get("ownerName");

        mediator.getEntryGate().vehicleArrives(vehicleNumber, vehicleType, ownerName);

        response.put("success", true);
        response.put("message", "Mediator coordinated entry for " + vehicleNumber);
        response.put("availableSpots", parkingLot.getAvailableSpots());
        return response;
    }

    // POST /api/mediator/exit — Vehicle exit coordinated via Mediator
    @PostMapping("/mediator/exit")
    public Map<String, Object> mediatorExit(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        String vehicleNumber = request.get("vehicleNumber");

        mediator.getExitGate().vehicleLeaves(vehicleNumber);

        response.put("success", true);
        response.put("message", "Mediator coordinated exit for " + vehicleNumber);
        response.put("availableSpots", parkingLot.getAvailableSpots());
        return response;
    }

    // ═══════════════════════════════════════════════════════════════════
    //  TEMPLATE METHOD PATTERN — Fee calculation with template steps
    // ═══════════════════════════════════════════════════════════════════

    // GET /api/template/fee?vehicleType=car&durationMinutes=180
    @GetMapping("/template/fee")
    public Map<String, Object> calculateFeeTemplate(
            @RequestParam String vehicleType,
            @RequestParam long durationMinutes) {

        Map<String, Object> response = new HashMap<>();

        ParkingFeeCalculator calculator;
        switch (vehicleType.toLowerCase()) {
            case "car":
                calculator = new CarFeeCalculator();
                break;
            case "bike":
                calculator = new BikeFeeCalculator();
                break;
            case "truck":
                calculator = new TruckFeeCalculator();
                break;
            default:
                response.put("success", false);
                response.put("message", "Unknown vehicle type: " + vehicleType);
                return response;
        }

        double fee = calculator.calculateTotalFee(durationMinutes);
        response.put("success", true);
        response.put("vehicleType", vehicleType);
        response.put("durationMinutes", durationMinutes);
        response.put("fee", fee);
        return response;
    }

    // ═══════════════════════════════════════════════════════════════════
    //  STRATEGY PATTERN — Switch pricing strategies at runtime
    // ═══════════════════════════════════════════════════════════════════

    // POST /api/strategy/set — Change the pricing strategy
    @PostMapping("/strategy/set")
    public Map<String, Object> setStrategy(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String strategyName = request.get("strategy");

        switch (strategyName.toLowerCase()) {
            case "hourly":
                pricingContext.setStrategy(new HourlyPricing());
                break;
            case "flat":
                pricingContext.setStrategy(new FlatRatePricing());
                break;
            case "dynamic":
                double occupancy = ((parkingLot.getTotalSpots() - parkingLot.getAvailableSpots()) * 100.0) / parkingLot.getTotalSpots();
                pricingContext.setStrategy(new DynamicPricing(occupancy));
                break;
            default:
                response.put("success", false);
                response.put("message", "Unknown strategy: " + strategyName + ". Use: hourly, flat, or dynamic.");
                return response;
        }

        response.put("success", true);
        response.put("message", "Pricing strategy set to: " + pricingContext.getStrategy().getStrategyName());
        return response;
    }

    // GET /api/strategy/calculate?vehicleType=car&durationMinutes=180
    @GetMapping("/strategy/calculate")
    public Map<String, Object> calculateWithStrategy(
            @RequestParam String vehicleType,
            @RequestParam long durationMinutes) {

        Map<String, Object> response = new HashMap<>();
        double price = pricingContext.executeStrategy(vehicleType, durationMinutes);

        response.put("success", true);
        response.put("strategy", pricingContext.getStrategy().getStrategyName());
        response.put("vehicleType", vehicleType);
        response.put("durationMinutes", durationMinutes);
        response.put("price", price);
        return response;
    }

    // ═══════════════════════════════════════════════════════════════════
    //  MEMENTO PATTERN — Save / Undo / History of parking lot state
    // ═══════════════════════════════════════════════════════════════════

    // POST /api/memento/save — Manually save a snapshot
    @PostMapping("/memento/save")
    public Map<String, Object> saveSnapshot() {
        Map<String, Object> response = new HashMap<>();
        ParkingLotMemento memento = parkingLot.saveState();
        caretaker.save(memento);

        response.put("success", true);
        response.put("message", "Snapshot saved.");
        response.put("historySize", caretaker.getHistorySize());
        response.put("snapshot", memento.toString());
        return response;
    }

    // POST /api/memento/undo — Restore the last saved state
    @PostMapping("/memento/undo")
    public Map<String, Object> undoLastAction() {
        Map<String, Object> response = new HashMap<>();
        ParkingLotMemento memento = caretaker.undo();

        if (memento == null) {
            response.put("success", false);
            response.put("message", "No snapshots to restore.");
            return response;
        }

        parkingLot.restoreState(memento);
        response.put("success", true);
        response.put("message", "State restored to: " + memento.getTimestamp());
        response.put("availableSpots", parkingLot.getAvailableSpots());
        response.put("parkedVehicles", parkingLot.getParkedVehicles().size());
        response.put("historySize", caretaker.getHistorySize());
        return response;
    }

    // GET /api/memento/history — View all saved snapshots
    @GetMapping("/memento/history")
    public Map<String, Object> getSnapshotHistory() {
        Map<String, Object> response = new HashMap<>();
        List<String> snapshots = new ArrayList<>();

        for (ParkingLotMemento m : caretaker.getHistory()) {
            snapshots.add(m.toString());
        }

        response.put("success", true);
        response.put("historySize", caretaker.getHistorySize());
        response.put("snapshots", snapshots);
        return response;
    }
}
