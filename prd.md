# Smart Parking System — PRD

## 1. Overview

A Smart Parking System that manages vehicle entry, validation, spot allocation, and real-time notifications using 7 design patterns learned across Practical 1–4.

### System Flow

```
Vehicle arrives at gate
       │
       ▼
┌──────────────────┐
│  Gate Sensor      │  ← Proxy Pattern
│  (Pre-validation) │
└────────┬─────────┘
         │
         ▼
┌──────────────────────────────┐
│  Validation Chain             │  ← Chain of Responsibility
│  ├── Registration Check       │
│  ├── Parking Availability     │
│  ├── Vehicle Type/Size Check  │
│  └── Access/Permit Check      │
└────────┬─────────────────────┘
         │
         ▼
┌──────────────────┐
│  Parking Zone     │  ← Abstract Factory
│  Factory          │     (Covered vs Open zone)
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│  Vehicle Factory  │  ← Factory Pattern
│  (Car/Bike/Truck) │
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│  ParkingLot       │  ← Singleton
│  (Single instance)│
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│  Display Board    │  ← Observer + Bridge
│  (LED/App/SMS)    │
└──────────────────┘
```

---

## 2. Design Patterns Mapping

### 2.1 Singleton — `ParkingLot`

There is only **one** parking lot. No matter how many gates or entries, they all share the same parking lot instance.

```
ParkingLot (private constructor)
├── totalSpots
├── availableSpots
├── parkVehicle(vehicle)
├── removeVehicle(vehicleNumber)
└── getParkingLotInstance()   ← static method, returns single instance
```

**Why Singleton?** If multiple instances existed, each would track different spot counts — leading to overbooking or ghost spots.

---

### 2.2 Proxy — `GateSensorProxy`

The gate sensor acts as a **proxy** before the actual validation chain runs. It performs quick pre-checks:
- Is the gate operational (not under maintenance)?
- Is the vehicle number plate readable / non-empty?
- Is the parking lot not already full?

If any pre-check fails, the request is **blocked immediately** without wasting time on the full chain.

```
GateSensorProxy
├── implements: VehicleValidator (same interface as chain)
├── realValidator: VehicleValidator (the actual chain)
├── validateEntry(vehicleNumber, vehicleType)
│   ├── Check: Is vehicle number valid?
│   ├── Check: Is parking lot open?
│   ├── If all pass → forward to realValidator (the chain)
│   └── If any fail → block entry, return rejection
```

**Analogy:** Like a security guard at the gate — checks your ID before letting you inside the building where further checks happen.

---

### 2.3 Chain of Responsibility — Validation Chain

Once the proxy passes, the vehicle goes through a **chain of validators**. Each validator either approves and passes to the next, or rejects and stops.

```
RegistrationCheck → ParkingAvailabilityCheck → VehicleTypeCheck → AccessPermitCheck
```

| Validator | What it checks | Rejects if... |
|-----------|---------------|---------------|
| `RegistrationCheck` | Is the vehicle registered in the system? | Vehicle number not found in DB |
| `ParkingAvailabilityCheck` | Are there free spots for this vehicle type? | No spots available for bikes/cars/trucks |
| `VehicleTypeCheck` | Is the vehicle type allowed? (e.g., no trucks in basement) | Vehicle too large for available zone |
| `AccessPermitCheck` | Does the owner have a valid permit/pass? | Permit expired or not found |

```
interface VehicleValidator {
    String validate(String vehicleNumber, String vehicleType);
    // returns approver name or null if rejected
}

Each validator has:
├── nextValidator: VehicleValidator
├── validate(vehicleNumber, vehicleType)
│   ├── Run own check
│   ├── If pass → forward to nextValidator
│   └── If fail → print rejection, return null
```

**Analogy:** Like your leave approval chain (HOD → Principal → Director), but here each link checks a different thing.

---

### 2.4 Factory — `VehicleFactory`

Creates the correct `Vehicle` object based on the type string. The client code doesn't need to know which class to instantiate.

```
VehicleFactory
└── createVehicle(vehicleNumber, vehicleType, ownerName)
    ├── "car"   → new Car(vehicleNumber, ownerName)
    ├── "bike"  → new Bike(vehicleNumber, ownerName)
    └── "truck" → new Truck(vehicleNumber, ownerName)

Vehicle (abstract class)
├── vehicleNumber
├── ownerName
├── getType()          ← abstract
├── getSpaceRequired() ← abstract (bike=1, car=2, truck=4)
```

**Why Factory?** Tomorrow if you add "EV" or "Bus" type, you only change the factory — no other code changes.

---

### 2.5 Abstract Factory — `ParkingZoneFactory`

The parking lot has **two zones**: Covered (basement) and Open (rooftop). Each zone creates a **family** of related objects:
- A `ParkingSpot` type (covered spot vs open spot)
- A `ParkingTicket` type (covered ticket with higher rate vs open ticket)

```
ParkingZoneFactory (interface)
├── createSpot(spotNumber)    → ParkingSpot
└── createTicket(vehicle)     → ParkingTicket

CoveredZoneFactory implements ParkingZoneFactory
├── createSpot(spotNumber)    → CoveredSpot (basement, sheltered)
└── createTicket(vehicle)     → CoveredTicket (₹50/hr)

OpenZoneFactory implements ParkingZoneFactory
├── createSpot(spotNumber)    → OpenSpot (rooftop, no shelter)
└── createTicket(vehicle)     → OpenTicket (₹30/hr)
```

**Why Abstract Factory?** A covered spot and open spot have different properties (rate, shelter, size). The factory ensures you always get a **matching pair** (covered spot + covered ticket), never a mismatch.

**Analogy:** Like PermanentFacultyLeaveFactory vs ContractFacultyLeaveFactory — each creates a consistent family.

---

### 2.6 Bridge — `NotificationChannel` for Display Board

The Display Board (Observer) needs to send notifications. The **notification mechanism** (LED Screen, Mobile App, SMS) is decoupled from the **display board** using Bridge pattern.

```
DisplayBoard (Abstraction)
├── has-a: NotificationChannel (Implementor)
├── updateDisplay(message)
│   └── calls channel.send(message)

NotificationChannel (interface / Implementor)
├── send(message)

LEDScreenChannel implements NotificationChannel
├── send(message) → "LED Screen: Available spots = 45"

MobileAppChannel implements NotificationChannel
├── send(message) → "App Push Notification: Available spots = 45"

SMSChannel implements NotificationChannel
├── send(message) → "SMS Alert: Available spots = 45"
```

**Why Bridge?** You can add new display boards (EntryBoard, ExitBoard, AdminBoard) AND new channels (Email, Webhook) independently without modifying each other.

---

### 2.7 Observer — Display Board Updates

When a vehicle parks or leaves, the `ParkingLot` notifies all registered **observers** (display boards, admin panels, etc.).

```
ParkingLot (Subject / Observable)
├── List<ParkingObserver> observers
├── addObserver(observer)
├── removeObserver(observer)
├── notifyObservers()         ← called after every park/remove
│   └── for each observer → observer.update(availableSpots, totalSpots)

ParkingObserver (interface)
├── update(availableSpots, totalSpots)

EntryDisplayBoard implements ParkingObserver
├── has-a: NotificationChannel (Bridge)
├── update() → "Entry Gate: 45/100 spots available"

ExitDisplayBoard implements ParkingObserver
├── has-a: NotificationChannel (Bridge)  
├── update() → "Exit Gate: Thank you! 46/100 spots now available"

AdminDashboard implements ParkingObserver
├── update() → "Admin: Parking occupancy at 54%"
```

---

### 2.8 Database Connectivity

Store and retrieve data from PostgreSQL (Supabase):

#### Tables

```sql
-- Registered vehicles
CREATE TABLE Vehicle (
    id SERIAL PRIMARY KEY,
    vehicleNumber VARCHAR(15) UNIQUE,
    vehicleType VARCHAR(10),
    ownerName VARCHAR(50),
    permitExpiry DATE
);

-- Parking spots
CREATE TABLE ParkingSpot (
    id SERIAL PRIMARY KEY,
    spotNumber VARCHAR(10),
    zone VARCHAR(10),         -- 'Covered' or 'Open'
    isOccupied BOOLEAN DEFAULT false,
    vehicleNumber VARCHAR(15)
);

-- Parking transactions (entry/exit log)
CREATE TABLE ParkingTransaction (
    id SERIAL PRIMARY KEY,
    vehicleNumber VARCHAR(15),
    spotNumber VARCHAR(10),
    entryTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    exitTime TIMESTAMP,
    amount DECIMAL(10,2),
    status VARCHAR(10) DEFAULT 'Active'
);
```

---

## 3. Project Structure — Code Separation

> [!IMPORTANT]
> The `core/` package contains ALL design pattern code. This is what the teacher evaluates.
> The `api/` and `frontend/` layers are just wrappers — teacher will NOT ask about them.

```
smart-parking/
│
├── src/main/java/com/parking/
│   │
│   ├── core/                          ← DESIGN PATTERN CODE (teacher evaluates)
│   │   │
│   │   ├── singleton/
│   │   │   └── ParkingLot.java
│   │   │
│   │   ├── proxy/
│   │   │   └── GateSensorProxy.java
│   │   │
│   │   ├── chain/
│   │   │   ├── VehicleValidator.java           (interface)
│   │   │   ├── RegistrationCheck.java
│   │   │   ├── ParkingAvailabilityCheck.java
│   │   │   ├── VehicleTypeCheck.java
│   │   │   ├── AccessPermitCheck.java
│   │   │   └── ValidationChainFactory.java     (builds the chain)
│   │   │
│   │   ├── factory/
│   │   │   ├── Vehicle.java                    (abstract)
│   │   │   ├── Car.java
│   │   │   ├── Bike.java
│   │   │   ├── Truck.java
│   │   │   └── VehicleFactory.java
│   │   │
│   │   ├── abstractfactory/
│   │   │   ├── ParkingZoneFactory.java         (interface)
│   │   │   ├── CoveredZoneFactory.java
│   │   │   ├── OpenZoneFactory.java
│   │   │   ├── ParkingSpot.java                (abstract)
│   │   │   ├── CoveredSpot.java
│   │   │   ├── OpenSpot.java
│   │   │   ├── ParkingTicket.java              (abstract)
│   │   │   ├── CoveredTicket.java
│   │   │   └── OpenTicket.java
│   │   │
│   │   ├── bridge/
│   │   │   ├── NotificationChannel.java        (interface)
│   │   │   ├── LEDScreenChannel.java
│   │   │   ├── MobileAppChannel.java
│   │   │   └── SMSChannel.java
│   │   │
│   │   ├── observer/
│   │   │   ├── ParkingObserver.java             (interface)
│   │   │   ├── EntryDisplayBoard.java
│   │   │   ├── ExitDisplayBoard.java
│   │   │   └── AdminDashboard.java
│   │   │
│   │   └── db/
│   │       ├── DBConnection.java
│   │       ├── VehicleDAO.java
│   │       ├── ParkingSpotDAO.java
│   │       └── TransactionDAO.java
│   │
│   └── api/                           ← SPRING BOOT LAYER (teacher ignores)
│       ├── controller/
│       │   ├── ParkingController.java
│       │   └── VehicleController.java
│       ├── dto/
│       │   ├── EntryRequest.java
│       │   └── ParkingResponse.java
│       └── ParkingApplication.java
│
├── frontend/                          ← UI (teacher ignores)
│   ├── index.html
│   ├── style.css
│   └── script.js
│
└── pom.xml
```

---

## 4. Complete Flow — Vehicle Entry Example

```
User enters: vehicleNumber = "MH12AB1234", vehicleType = "car"

1. [PROXY] GateSensorProxy checks:
   ✓ Vehicle number is not empty
   ✓ Parking lot is open
   → Forwards to validation chain

2. [CHAIN] RegistrationCheck:
   ✓ "MH12AB1234" found in Vehicle table
   → Pass to next

3. [CHAIN] ParkingAvailabilityCheck:
   ✓ 45 car spots available
   → Pass to next

4. [CHAIN] VehicleTypeCheck:
   ✓ "car" is allowed in both zones
   → Pass to next

5. [CHAIN] AccessPermitCheck:
   ✓ Permit valid until 2026-12-31
   → All checks passed! Returns "AccessPermitCheck"

6. [FACTORY] VehicleFactory.createVehicle("MH12AB1234", "car", "Rohit")
   → Creates Car object (spaceRequired = 2)

7. [ABSTRACT FACTORY] User picks "Covered" zone
   → CoveredZoneFactory creates CoveredSpot + CoveredTicket (₹50/hr)

8. [SINGLETON] ParkingLot.getInstance().parkVehicle(car, spot)
   → Marks spot as occupied, availableSpots--

9. [OBSERVER] ParkingLot notifies all observers:
   → EntryDisplayBoard: "44/100 spots available"
   → ExitDisplayBoard: "44/100 spots available"  
   → AdminDashboard: "Occupancy: 56%"

10. [BRIDGE] Each DisplayBoard uses its bridged channel:
    → LED Screen shows the count
    → Mobile App sends push notification
    → SMS sent to admin

11. [DB] Transaction saved:
    → INSERT INTO ParkingTransaction (vehicleNumber, spotNumber, entryTime)
```

---

## 5. Vehicle Exit Flow

```
User exits: vehicleNumber = "MH12AB1234"

1. [SINGLETON] ParkingLot.getInstance().removeVehicle("MH12AB1234")
   → Frees the spot, availableSpots++

2. [DB] Update transaction:
   → UPDATE ParkingTransaction SET exitTime = NOW(), amount = calculated

3. [OBSERVER] ParkingLot notifies all observers:
   → EntryDisplayBoard: "45/100 spots available"
   → AdminDashboard: "Occupancy: 55%"

4. [BRIDGE] Notifications sent via bridged channels
```

---

## 6. API Endpoints (Spring Boot Layer)

> [!NOTE]
> These are just thin wrappers around the `core/` classes. The controller calls the design pattern code — it doesn't contain any business logic itself.

| Method | Endpoint | What it does |
|--------|----------|-------------|
| POST | `/api/entry` | Process vehicle entry (runs proxy → chain → factory → park) |
| POST | `/api/exit` | Process vehicle exit (remove → notify → calculate amount) |
| GET | `/api/spots` | Get all parking spots and their status |
| GET | `/api/spots/available` | Get count of available spots |
| GET | `/api/vehicles` | Get all registered vehicles |
| POST | `/api/vehicles/register` | Register a new vehicle |
| GET | `/api/transactions` | Get parking transaction history |

---

## 7. Summary — Pattern to Class Mapping

| # | Pattern | Class(es) | Purpose |
|---|---------|-----------|---------|
| 1 | **Singleton** | `ParkingLot` | One parking lot instance shared everywhere |
| 2 | **Proxy** | `GateSensorProxy` | Pre-validates before running expensive chain |
| 3 | **Chain of Responsibility** | `RegistrationCheck → AvailabilityCheck → TypeCheck → PermitCheck` | Sequential validation, each can approve or reject |
| 4 | **Factory** | `VehicleFactory` → `Car, Bike, Truck` | Creates correct vehicle object from type string |
| 5 | **Abstract Factory** | `CoveredZoneFactory, OpenZoneFactory` → `Spot + Ticket` families | Creates matching zone-specific object families |
| 6 | **Bridge** | `DisplayBoard` + `NotificationChannel` (LED/App/SMS) | Decouples what to display from how to notify |
| 7 | **Observer** | `ParkingLot` notifies `EntryBoard, ExitBoard, AdminDashboard` | Auto-updates all boards when spots change |
| 8 | **DB Connectivity** | `DBConnection, VehicleDAO, SpotDAO, TransactionDAO` | CRUD operations on PostgreSQL |

---

## 8. Implementation Order

| Step | What to build | Patterns involved |
|------|--------------|-------------------|
| 1 | `Vehicle` abstract + `Car`, `Bike`, `Truck` + `VehicleFactory` | Factory |
| 2 | `ParkingLot` singleton | Singleton |
| 3 | `VehicleValidator` interface + 4 chain validators + `ValidationChainFactory` | Chain of Responsibility |
| 4 | `GateSensorProxy` | Proxy |
| 5 | `ParkingSpot`, `ParkingTicket` + zone factories | Abstract Factory |
| 6 | `NotificationChannel` + LED/App/SMS channels | Bridge |
| 7 | `ParkingObserver` + display boards (using bridge) | Observer + Bridge |
| 8 | `DBConnection` + DAOs | DB Connectivity |
| 9 | Spring Boot controllers (thin wrappers) | API layer |
| 10 | Frontend UI | UI layer |
