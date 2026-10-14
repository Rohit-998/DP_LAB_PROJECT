# Smart Parking System — PRD

## 1. Overview

A Smart Parking System that manages vehicle entry, validation, spot allocation, fee calculation, and real-time notifications using **11 design patterns**.

### System Flow

```
Vehicle arrives at gate
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
┌──────────────────┐     ┌──────────────────────┐
│  Vehicle Factory  │  OR │ Prototype Registry    │  ← Factory + Prototype
│  (Car/Bike/Truck) │     │ (Clone from template) │
└────────┬─────────┘     └────────┬──────────────┘
         │                        │
         ▼                        ▼
┌──────────────────┐
│  ParkingLot       │  ← Singleton
│  (Single instance)│
└────────┬─────────┘
         │
    ┌────┴────────────────────────────────────┐
    │                                         │
    ▼                                         ▼
┌──────────────────┐              ┌───────────────────────┐
│  Mediator         │  ← Mediator │  Observer + Bridge     │
│  (Coordinates     │             │  (Display Boards via   │
│   Gate/Payment/   │             │   LED/App/SMS channels)│
│   Display)        │             └───────────────────────┘
└──────────────────┘
         │
    ┌────┴─────────────────────────┐
    │                              │
    ▼                              ▼
┌──────────────────┐     ┌──────────────────────┐
│  Builder          │     │  Memento              │  ← Memento
│  (ParkingTicket)  │     │  (Save/Undo state)    │
└──────────────────┘     └──────────────────────┘
         │
         ▼
┌──────────────────────────────────────────────┐
│  Fee Calculation                              │
│  ├── Template Method (base + duration +       │  ← Template Method
│  │   surcharge - discount per vehicle type)   │
│  └── Strategy (Hourly / Flat / Dynamic)       │  ← Strategy
└──────────────────────────────────────────────┘
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
├── getInstance()           ← static method, returns single instance
├── saveState()             ← Memento support
└── restoreState(memento)   ← Memento support
```

**Why Singleton?** If multiple instances existed, each would track different spot counts — leading to overbooking or ghost spots.

---

### 2.2 Factory — `VehicleFactory`

Creates the correct `Vehicle` object based on the type string. The client code doesn't need to know which class to instantiate.

```
VehicleFactory
└── createVehicle(vehicleNumber, vehicleType, ownerName)
    ├── "car"   → new Car(vehicleNumber, ownerName)
    ├── "bike"  → new Bike(vehicleNumber, ownerName)
    └── "truck" → new Truck(vehicleNumber, ownerName)

Vehicle (abstract class, implements Cloneable)
├── vehicleNumber
├── ownerName
├── getType()          ← abstract
├── getSpaceRequired() ← abstract (bike=1, car=2, truck=4)
├── clone()            ← Prototype support
```

**Why Factory?** Tomorrow if you add "EV" or "Bus" type, you only change the factory — no other code changes.

---

### 2.3 Chain of Responsibility — Validation Chain

The vehicle goes through a **chain of validators**. Each validator either approves and passes to the next, or rejects and stops.

```
RegistrationCheck → ParkingAvailabilityCheck → VehicleTypeCheck → AccessPermitCheck
```

| Validator | What it checks | Rejects if... |
|-----------|---------------|---------------|
| `RegistrationCheck` | Is the vehicle number valid? | Vehicle number is empty/null |
| `ParkingAvailabilityCheck` | Are there free spots? | No spots available |
| `VehicleTypeCheck` | Is the vehicle type allowed? | Unknown or restricted type |
| `AccessPermitCheck` | Does the owner have a valid permit? | Permit invalid |

**Analogy:** Like your leave approval chain (HOD → Principal → Director), but here each link checks a different thing.

---

### 2.4 Observer — Display Board Updates

When a vehicle parks or leaves, the `ParkingLot` notifies all registered **observers** (display boards, admin panels).

```
ParkingLot (Subject / Observable)
├── List<ParkingObserver> observers
├── addObserver(observer)
├── removeObserver(observer)
├── notifyObservers()         ← called after every park/remove

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

### 2.5 Bridge — `NotificationChannel` for Display Board

The **notification mechanism** (LED Screen, Mobile App, SMS) is decoupled from the **display board** using Bridge pattern.

```
NotificationChannel (interface / Implementor)
├── send(message)

LEDScreenChannel implements NotificationChannel
├── send(message) → "LED Screen: Available spots = 45"

MobileAppChannel implements NotificationChannel
├── send(message) → "App Push Notification: Available spots = 45"

SMSChannel implements NotificationChannel
├── send(message) → "SMS Alert: Available spots = 45"
```

**Why Bridge?** You can add new display boards AND new channels independently without modifying each other.

---

### 2.6 Prototype — `VehiclePrototypeRegistry`

Pre-registers prototype `Vehicle` objects (Car, Bike, Truck). Instead of constructing from scratch, vehicles are **cloned** from prototypes and customized with new details.

```
VehiclePrototypeRegistry
├── prototypes: Map<String, Vehicle>      ← pre-registered prototypes
├── getClone(type, vehicleNumber, owner)  ← clones + customizes
├── addPrototype(key, vehicle)            ← register new prototype at runtime
```

**Why Prototype?** Faster than constructing complex objects from scratch. Useful when many similar vehicles need to be created.

---

### 2.7 Builder — `ParkingTicket`

Constructs a complex `ParkingTicket` object step-by-step via a fluent builder. The ticket has many fields (ID, vehicle info, spot, zone, time, fee, payment status) — builder avoids telescoping constructors.

```
ParkingTicket (private constructor)
├── ticketId, vehicleNumber, vehicleType, ownerName
├── spotNumber, zone, entryTime, exitTime
├── fee, isPaid

ParkingTicket.Builder (inner class)
├── ticketId("TKT-123")
├── vehicleNumber("MH12AB1234")
├── vehicleType("car")
├── ownerName("Rohit")
├── spotNumber("S-42")
├── zone("general")
├── entryTime(...)
├── build() → ParkingTicket
```

**Why Builder?** With 10+ fields, constructors become unreadable. Builder gives named, readable construction.

---

### 2.8 Mediator — `ParkingSystemMediator`

Coordinates `EntryGate`, `ExitGate`, `PaymentSystem`, and `DisplaySystem` through a **central mediator**. Components never talk to each other directly.

```
ParkingMediator (interface)
├── vehicleArrived(vehicleNumber, type, owner)
├── vehicleDeparted(vehicleNumber)
├── notify(sender, event)

ParkingSystemMediator implements ParkingMediator
├── entryGate, exitGate, paymentSystem, displaySystem
├── Coordinates: validation → factory → parking → display → payment

Components (all extend ParkingComponent):
├── EntryGate    → detects vehicle, delegates to mediator
├── ExitGate     → detects exit, opens barrier
├── PaymentSystem → calculates & processes payment
├── DisplaySystem → updates availability displays
```

**Why Mediator?** Without it, each component would need references to every other component — creating a tangled web. The mediator centralizes communication.

---

### 2.9 Template Method — `ParkingFeeCalculator`

Defines the **skeleton** of the fee calculation algorithm. Subclasses override specific steps without changing the overall structure.

```
ParkingFeeCalculator (abstract)
├── calculateTotalFee(duration)  ← FINAL template method
│   ├── getBaseFee()             ← abstract (Car=₹30, Bike=₹10, Truck=₹60)
│   ├── calculateDurationFee()   ← abstract (different rates per type)
│   ├── applySurcharge()         ← hook (default: ₹0)
│   └── applyDiscount()         ← hook (default: ₹0)
│   → Total = base + duration + surcharge - discount

CarFeeCalculator   → ₹30 base, ₹10/hr, overnight surcharge ₹20
BikeFeeCalculator  → ₹10 base, ₹5/hr, short-stay discount ₹5
TruckFeeCalculator → ₹60 base, ₹20/hr, heavy vehicle surcharge ₹50
```

**Why Template Method?** The algorithm structure (base + duration + surcharge - discount) is fixed. Only the values change per vehicle type.

---

### 2.10 Strategy — `PricingStrategy`

Allows **swapping pricing algorithms at runtime**. The system can switch between Hourly, Flat Rate, or Dynamic pricing without modifying calling code.

```
PricingStrategy (interface)
├── calculatePrice(vehicleType, durationMinutes)
├── getStrategyName()

HourlyPricing   → Car: ₹20/hr, Bike: ₹10/hr, Truck: ₹40/hr
FlatRatePricing → Car: ₹100, Bike: ₹50, Truck: ₹200 (fixed)
DynamicPricing  → Base rate × surge multiplier (1.0x-3.0x based on occupancy %)

PricingContext (holds current strategy)
├── setStrategy(strategy)       ← swap at runtime
├── executeStrategy(type, duration) → delegates to active strategy
```

**Why Strategy?** Different times of day or events may need different pricing. Weekday → Hourly, Weekend → Flat, Festival → Dynamic. No if-else chains needed.

---

### 2.11 Memento — `ParkingLotMemento`

Saves **snapshots** of ParkingLot state. Supports **undo** to rollback the last parking operation.

```
ParkingLotMemento (immutable snapshot)
├── availableSpots, totalSpots
├── parkedVehicles (deep copy)
├── timestamp

ParkingLotCaretaker (manages history)
├── history: List<ParkingLotMemento>
├── save(memento)     ← push snapshot
├── undo()            ← pop & return last snapshot
├── getHistory()      ← view all snapshots

ParkingLot
├── saveState()    → creates ParkingLotMemento
├── restoreState() → restores from ParkingLotMemento
```

**Why Memento?** If a vehicle was parked by mistake, the operator can undo. Also useful for audit trails and state recovery.

---

## 3. Project Structure

```
smart-parking/backend/src/main/java/com/parking/smart_parking/
│
├── core/                              ← DESIGN PATTERN CODE
│   │
│   ├── singleton/
│   │   └── ParkingLot.java
│   │
│   ├── factory/
│   │   ├── Vehicle.java               (abstract, Cloneable)
│   │   ├── Car.java
│   │   ├── Bike.java
│   │   ├── Truck.java
│   │   └── VehicleFactory.java
│   │
│   ├── chain/
│   │   ├── VehicleValidator.java      (interface)
│   │   ├── RegistrationCheck.java
│   │   ├── ParkingAvailabilityCheck.java
│   │   ├── VehicleTypeCheck.java
│   │   ├── AccessPermitCheck.java
│   │   └── ValidationChainFactory.java
│   │
│   ├── observer/
│   │   ├── ParkingObserver.java       (interface)
│   │   ├── EntryDisplayBoard.java
│   │   ├── ExitDisplayBoard.java
│   │   └── AdminDashboard.java
│   │
│   ├── bridge/
│   │   ├── NotificationChannel.java   (interface)
│   │   ├── LEDScreenChannel.java
│   │   ├── MobileAppChannel.java
│   │   └── SMSChannel.java
│   │
│   ├── prototype/
│   │   └── VehiclePrototypeRegistry.java
│   │
│   ├── builder/
│   │   └── ParkingTicket.java         (with inner Builder class)
│   │
│   ├── mediator/
│   │   ├── ParkingMediator.java       (interface)
│   │   ├── ParkingComponent.java      (abstract)
│   │   ├── ParkingSystemMediator.java
│   │   ├── EntryGate.java
│   │   ├── ExitGate.java
│   │   ├── PaymentSystem.java
│   │   └── DisplaySystem.java
│   │
│   ├── template/
│   │   ├── ParkingFeeCalculator.java  (abstract, template method)
│   │   ├── CarFeeCalculator.java
│   │   ├── BikeFeeCalculator.java
│   │   └── TruckFeeCalculator.java
│   │
│   ├── strategy/
│   │   ├── PricingStrategy.java       (interface)
│   │   ├── HourlyPricing.java
│   │   ├── FlatRatePricing.java
│   │   ├── DynamicPricing.java
│   │   └── PricingContext.java
│   │
│   ├── memento/
│   │   ├── ParkingLotMemento.java
│   │   └── ParkingLotCaretaker.java
│   │
│   └── db/
│       ├── DBConnection.java
│       └── ParkingDAO.java
│
├── api/
│   └── controller/
│       └── ParkingController.java
│
└── SmartParkingApplication.java
```

---

## 4. Complete Flow — Vehicle Entry

```
User enters: vehicleNumber = "MH12AB1234", vehicleType = "car", ownerName = "Rohit"

1. [CHAIN] RegistrationCheck:
   ✓ "MH12AB1234" is not empty
   → Pass to next

2. [CHAIN] ParkingAvailabilityCheck:
   ✓ 45 spots available
   → Pass to next

3. [CHAIN] VehicleTypeCheck:
   ✓ "car" is an allowed type
   → Pass to next

4. [CHAIN] AccessPermitCheck:
   ✓ Permit is valid
   → All checks passed!

5. [MEMENTO] ParkingLot saves state snapshot before parking

6. [FACTORY] VehicleFactory.createVehicle("MH12AB1234", "car", "Rohit")
   → Creates Car object (spaceRequired = 2)

7. [SINGLETON] ParkingLot.getInstance().parkVehicle(car)
   → availableSpots -= 2

8. [BUILDER] ParkingTicket built:
   → TKT-123 | MH12AB1234 | Car | Spot S-42 | Zone general | Entry: now

9. [OBSERVER] ParkingLot notifies all observers:
   → EntryDisplayBoard: "44/100 spots available"
   → ExitDisplayBoard: "44/100 spots available"
   → AdminDashboard: "Occupancy: 56%"

10. [BRIDGE] Each DisplayBoard uses its bridged channel:
    → LED Screen shows the count
    → Mobile App sends push notification
```

---

## 5. Vehicle Exit Flow

```
User exits: vehicleNumber = "MH12AB1234"

1. [MEMENTO] ParkingLot saves state snapshot before removal

2. [STRATEGY] Current pricing strategy calculates fee:
   → Hourly: Car × 2h × ₹20/hr = ₹40
   (or Flat: ₹100 | or Dynamic: ₹40 × 1.5x surge = ₹60)

3. [SINGLETON] ParkingLot.removeVehicle("MH12AB1234")
   → availableSpots += 2

4. [BUILDER] Ticket updated: exitTime, fee, isPaid = true

5. [OBSERVER] ParkingLot notifies all observers

6. [BRIDGE] Notifications sent via bridged channels

7. [DB] Transaction updated with exit time and amount
```

---

## 6. API Endpoints

| Method | Endpoint | Patterns Used |
|--------|----------|---------------|
| POST | `/api/entry` | Chain + Factory + Singleton + Observer + Bridge + Builder + Memento |
| POST | `/api/exit` | Singleton + Observer + Strategy + Builder + Memento |
| GET | `/api/spots` | Singleton |
| GET | `/api/transactions` | DB |
| GET | `/api/transactions/active` | DB |
| POST | `/api/prototype/clone` | Prototype + Memento |
| GET | `/api/ticket/{vehicleNumber}` | Builder |
| POST | `/api/mediator/entry` | Mediator |
| POST | `/api/mediator/exit` | Mediator |
| GET | `/api/template/fee` | Template Method |
| POST | `/api/strategy/set` | Strategy |
| GET | `/api/strategy/calculate` | Strategy |
| POST | `/api/memento/save` | Memento |
| POST | `/api/memento/undo` | Memento |
| GET | `/api/memento/history` | Memento |

---

## 7. Summary — Pattern to Class Mapping

| # | Pattern | Category | Class(es) | Purpose |
|---|---------|----------|-----------|---------|
| 1 | **Singleton** | Creational | `ParkingLot` | One parking lot instance shared everywhere |
| 2 | **Factory** | Creational | `VehicleFactory` → `Car, Bike, Truck` | Creates correct vehicle from type string |
| 3 | **Prototype** | Creational | `VehiclePrototypeRegistry` | Clones pre-built vehicle prototypes |
| 4 | **Builder** | Creational | `ParkingTicket.Builder` | Step-by-step construction of complex ticket |
| 5 | **Chain of Responsibility** | Behavioral | `RegistrationCheck → AvailabilityCheck → TypeCheck → PermitCheck` | Sequential validation chain |
| 6 | **Observer** | Behavioral | `ParkingLot` notifies `EntryBoard, ExitBoard, AdminDashboard` | Auto-updates all boards on state change |
| 7 | **Mediator** | Behavioral | `ParkingSystemMediator` coordinates `EntryGate, ExitGate, Payment, Display` | Centralizes component communication |
| 8 | **Template Method** | Behavioral | `ParkingFeeCalculator` → `CarFee, BikeFee, TruckFee` | Fixed algorithm skeleton, variable steps |
| 9 | **Strategy** | Behavioral | `HourlyPricing, FlatRatePricing, DynamicPricing` | Swappable pricing algorithms at runtime |
| 10 | **Memento** | Behavioral | `ParkingLotMemento + ParkingLotCaretaker` | Save/restore/undo parking lot state |
| 11 | **Bridge** | Structural | `NotificationChannel` (LED/App/SMS) | Decouples display boards from notification channels |
