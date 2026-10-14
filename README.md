# 🅿️ Smart Parking System

A Java Spring Boot application demonstrating **11 Design Patterns** in a real-world Smart Parking Management System.

## 📋 Design Patterns Implemented

### Creational Patterns
| # | Pattern | Package | Key Classes |
|---|---------|---------|-------------|
| 1 | **Singleton** | `core.singleton` | `ParkingLot` — single shared parking lot instance |
| 2 | **Factory Method** | `core.factory` | `VehicleFactory` → `Car`, `Bike`, `Truck` |
| 3 | **Prototype** | `core.prototype` | `VehiclePrototypeRegistry` — clones vehicle templates |
| 4 | **Builder** | `core.builder` | `ParkingTicket.Builder` — step-by-step ticket construction |

### Behavioral Patterns
| # | Pattern | Package | Key Classes |
|---|---------|---------|-------------|
| 5 | **Chain of Responsibility** | `core.chain` | `RegistrationCheck → AvailabilityCheck → TypeCheck → PermitCheck` |
| 6 | **Observer** | `core.observer` | `ParkingObserver`, `EntryDisplayBoard`, `ExitDisplayBoard`, `AdminDashboard` |
| 7 | **Mediator** | `core.mediator` | `ParkingSystemMediator` coordinates `EntryGate`, `ExitGate`, `PaymentSystem`, `DisplaySystem` |
| 8 | **Template Method** | `core.template` | `ParkingFeeCalculator` → `CarFeeCalculator`, `BikeFeeCalculator`, `TruckFeeCalculator` |
| 9 | **Strategy** | `core.strategy` | `HourlyPricing`, `FlatRatePricing`, `DynamicPricing` via `PricingContext` |
| 10 | **Memento** | `core.memento` | `ParkingLotMemento` + `ParkingLotCaretaker` — save/undo state |

### Structural Patterns
| # | Pattern | Package | Key Classes |
|---|---------|---------|-------------|
| 11 | **Bridge** | `core.bridge` | `NotificationChannel` → `LEDScreenChannel`, `MobileAppChannel`, `SMSChannel` |

## 🏗️ Project Structure

```
backend/src/main/java/com/parking/smart_parking/
├── core/
│   ├── singleton/        → ParkingLot
│   ├── factory/          → Vehicle, Car, Bike, Truck, VehicleFactory
│   ├── prototype/        → VehiclePrototypeRegistry
│   ├── builder/          → ParkingTicket (with Builder)
│   ├── chain/            → VehicleValidator, RegistrationCheck, etc.
│   ├── observer/         → ParkingObserver, EntryDisplayBoard, etc.
│   ├── bridge/           → NotificationChannel, LEDScreen, MobileApp, SMS
│   ├── mediator/         → ParkingMediator, ParkingSystemMediator, Gates, etc.
│   ├── template/         → ParkingFeeCalculator, Car/Bike/TruckFeeCalculator
│   ├── strategy/         → PricingStrategy, Hourly/Flat/Dynamic, PricingContext
│   ├── memento/          → ParkingLotMemento, ParkingLotCaretaker
│   └── db/               → DBConnection, ParkingDAO
├── api/controller/       → ParkingController (REST endpoints)
└── SmartParkingApplication.java
```

## 🔌 API Endpoints

### Core Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/entry` | Vehicle entry (Chain + Factory + Singleton + Observer + Builder + Memento) |
| `POST` | `/api/exit` | Vehicle exit (Singleton + Strategy + Builder + Memento) |
| `GET` | `/api/spots` | Get parking spot availability |
| `GET` | `/api/transactions` | Get all transactions |
| `GET` | `/api/transactions/active` | Get currently parked vehicles |

### Prototype Pattern
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/prototype/clone` | Create vehicle by cloning a prototype |

### Builder Pattern
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/ticket/{vehicleNumber}` | Get parking ticket details |

### Mediator Pattern
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/mediator/entry` | Vehicle entry via mediator coordination |
| `POST` | `/api/mediator/exit` | Vehicle exit via mediator coordination |

### Template Method Pattern
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/template/fee?vehicleType=car&durationMinutes=180` | Calculate fee using template method |

### Strategy Pattern
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/strategy/set` | Switch pricing strategy (hourly/flat/dynamic) |
| `GET` | `/api/strategy/calculate?vehicleType=car&durationMinutes=180` | Calculate price with current strategy |

### Memento Pattern
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/memento/save` | Save parking lot state snapshot |
| `POST` | `/api/memento/undo` | Undo last action (restore previous state) |
| `GET` | `/api/memento/history` | View all saved snapshots |

## 🚀 How to Run

### Prerequisites
- Java 17+
- Maven

### Build & Run
```bash
cd backend
./mvnw spring-boot:run
```

The server starts at `http://localhost:8080`.

### Example API Calls

**Vehicle Entry:**
```bash
curl -X POST http://localhost:8080/api/entry \
  -H "Content-Type: application/json" \
  -d '{"vehicleNumber":"MH12AB1234","vehicleType":"car","ownerName":"Rohit"}'
```

**Vehicle Exit:**
```bash
curl -X POST http://localhost:8080/api/exit \
  -H "Content-Type: application/json" \
  -d '{"vehicleNumber":"MH12AB1234"}'
```

**Switch Pricing Strategy:**
```bash
curl -X POST http://localhost:8080/api/strategy/set \
  -H "Content-Type: application/json" \
  -d '{"strategy":"dynamic"}'
```

**Calculate Fee (Template Method):**
```bash
curl "http://localhost:8080/api/template/fee?vehicleType=truck&durationMinutes=420"
```

**Undo Last Action:**
```bash
curl -X POST http://localhost:8080/api/memento/undo
```

## 📐 UML Diagram

The complete UML class diagram is available as a PlantUML file:
- `SmartParkingSystem_UML.puml` — open with any PlantUML viewer or paste at [plantuml.com](https://www.plantuml.com/plantuml/uml)
- `SmartParkingSystem_UML.svg` — pre-rendered SVG diagram

## 📄 License

This project is for educational / lab purposes.
