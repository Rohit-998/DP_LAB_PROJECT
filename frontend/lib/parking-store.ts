export type VehicleType = "Car" | "Bike" | "Truck";
export type TransactionStatus = "Active" | "Completed";

export type ParkingTransaction = {
  id: string;
  vehicleNumber: string;
  ownerName: string;
  vehicleType: VehicleType;
  spotNumber: string;
  entryTime: string;
  exitTime: string | null;
  amount: number | null;
  status: TransactionStatus;
};

export type Notification = {
  id: string;
  source: "LED · ENTRY GATE" | "LED · EXIT GATE" | "MOBILE · ADMIN APP";
  message: string;
  timestamp: string;
  tone: "signal" | "ember" | "muted";
};

export type ValidationStep = { label: string; passed: boolean; detail: string };
type Store = { transactions: ParkingTransaction[]; notifications: Notification[] };

const capacity = 24;
const minutesAgo = (minutes: number) => new Date(Date.now() - minutes * 60_000).toISOString();

const initialTransactions: ParkingTransaction[] = [
  { id: "a1", vehicleNumber: "KA 01 AP 2248", ownerName: "Aarav Mehta", vehicleType: "Car", spotNumber: "A-01", entryTime: minutesAgo(142), exitTime: null, amount: null, status: "Active" },
  { id: "a2", vehicleNumber: "MH 12 QN 8031", ownerName: "Ishita Rao", vehicleType: "Bike", spotNumber: "A-02", entryTime: minutesAgo(106), exitTime: null, amount: null, status: "Active" },
  { id: "a3", vehicleNumber: "KA 03 MK 7759", ownerName: "Rohan Nair", vehicleType: "Car", spotNumber: "A-03", entryTime: minutesAgo(87), exitTime: null, amount: null, status: "Active" },
  { id: "a4", vehicleNumber: "GJ 05 DT 1934", ownerName: "Dev Shah", vehicleType: "Truck", spotNumber: "A-04", entryTime: minutesAgo(72), exitTime: null, amount: null, status: "Active" },
  { id: "a5", vehicleNumber: "TN 09 PD 4260", ownerName: "Kavya Iyer", vehicleType: "Car", spotNumber: "A-05", entryTime: minutesAgo(63), exitTime: null, amount: null, status: "Active" },
  { id: "a6", vehicleNumber: "UP 32 BL 1178", ownerName: "Zoya Khan", vehicleType: "Bike", spotNumber: "A-06", entryTime: minutesAgo(45), exitTime: null, amount: null, status: "Active" },
  { id: "a7", vehicleNumber: "HR 26 CV 9017", ownerName: "Neil Verma", vehicleType: "Car", spotNumber: "A-07", entryTime: minutesAgo(31), exitTime: null, amount: null, status: "Active" },
  { id: "a8", vehicleNumber: "WB 20 AX 4831", ownerName: "Mira Bose", vehicleType: "Car", spotNumber: "A-08", entryTime: minutesAgo(22), exitTime: null, amount: null, status: "Active" },
  { id: "a9", vehicleNumber: "RJ 14 EA 4201", ownerName: "Ansh Garg", vehicleType: "Bike", spotNumber: "B-01", entryTime: minutesAgo(17), exitTime: null, amount: null, status: "Active" },
  { id: "a10", vehicleNumber: "KL 07 FA 2984", ownerName: "Sana Joseph", vehicleType: "Car", spotNumber: "B-02", entryTime: minutesAgo(12), exitTime: null, amount: null, status: "Active" },
  { id: "a11", vehicleNumber: "DL 08 HM 6224", ownerName: "Tanya Singh", vehicleType: "Car", spotNumber: "B-03", entryTime: minutesAgo(7), exitTime: null, amount: null, status: "Active" },
  { id: "a12", vehicleNumber: "MH 14 JC 9982", ownerName: "Ritesh Patel", vehicleType: "Bike", spotNumber: "B-04", entryTime: minutesAgo(3), exitTime: null, amount: null, status: "Active" },
  { id: "h1", vehicleNumber: "DL 03 LQ 7004", ownerName: "Maya Kapoor", vehicleType: "Car", spotNumber: "B-06", entryTime: minutesAgo(226), exitTime: minutesAgo(82), amount: 160, status: "Completed" },
  { id: "h2", vehicleNumber: "PB 10 VE 4130", ownerName: "Kabir Bhatia", vehicleType: "Truck", spotNumber: "C-02", entryTime: minutesAgo(303), exitTime: minutesAgo(128), amount: 450, status: "Completed" },
  { id: "h3", vehicleNumber: "AP 16 KR 8215", ownerName: "Noor Ali", vehicleType: "Bike", spotNumber: "B-05", entryTime: minutesAgo(152), exitTime: minutesAgo(72), amount: 45, status: "Completed" },
];

const initialNotifications: Notification[] = [
  { id: "n1", source: "LED · ENTRY GATE", message: "MH 14 JC 9982 cleared — bay B-04 assigned", timestamp: minutesAgo(3), tone: "signal" },
  { id: "n2", source: "MOBILE · ADMIN APP", message: "Occupancy stable at 50% · 12 bays available", timestamp: minutesAgo(8), tone: "muted" },
  { id: "n3", source: "LED · EXIT GATE", message: "DL 03 LQ 7004 exit settled · ₹160 collected", timestamp: minutesAgo(82), tone: "ember" },
  { id: "n4", source: "LED · ENTRY GATE", message: "Gate 02 online · all access checks nominal", timestamp: minutesAgo(117), tone: "signal" },
];

const shared = globalThis as typeof globalThis & { __parkdeskStore?: Store };
if (!shared.__parkdeskStore) shared.__parkdeskStore = { transactions: initialTransactions, notifications: initialNotifications };
const store = shared.__parkdeskStore;
const active = () => store.transactions.filter((item) => item.status === "Active");

function addNotification(notification: Omit<Notification, "id" | "timestamp">) {
  store.notifications = [{ id: `n-${Date.now()}`, timestamp: new Date().toISOString(), ...notification }, ...store.notifications].slice(0, 20);
}

function nextSpot() {
  const used = new Set(active().map((item) => item.spotNumber));
  const slots = ["A", "B", "C"].flatMap((level) => Array.from({ length: 8 }, (_, index) => `${level}-${String(index + 1).padStart(2, "0")}`));
  return slots.find((slot) => !used.has(slot)) || null;
}

export function getDashboard() {
  const parked = active();
  return {
    stats: { totalSpots: capacity, availableSpots: capacity - parked.length, occupiedSpots: parked.length, occupancy: Math.round((parked.length / capacity) * 100) },
    transactions: [...store.transactions].sort((first, second) => new Date(second.entryTime).getTime() - new Date(first.entryTime).getTime()),
    notifications: store.notifications,
  };
}

export function parkVehicle(input: { vehicleNumber?: string; ownerName?: string; vehicleType?: string }) {
  const vehicleNumber = (input.vehicleNumber || "").trim().toUpperCase().replace(/\s+/g, " ");
  const ownerName = (input.ownerName || "").trim();
  const vehicleType = input.vehicleType as VehicleType;
  const registrationPassed = /^[A-Z0-9 -]{5,14}$/.test(vehicleNumber) && ownerName.length >= 2;
  const hasAvailability = active().length < capacity;
  const validType = ["Car", "Bike", "Truck"].includes(vehicleType);
  const permitPassed = registrationPassed && !active().some((item) => item.vehicleNumber === vehicleNumber);
  const validation: ValidationStep[] = [
    { label: "Registration check", passed: registrationPassed, detail: registrationPassed ? "Vehicle identity is valid" : "Enter a valid number and owner name" },
    { label: "Parking availability", passed: hasAvailability, detail: hasAvailability ? `${capacity - active().length} bays are available` : "The garage is currently full" },
    { label: "Vehicle type", passed: validType, detail: validType ? `${vehicleType} is supported in this garage` : "Choose Car, Bike, or Truck" },
    { label: "Access permit", passed: permitPassed, detail: permitPassed ? "Permit approved for entry" : "An active session already exists for this vehicle" },
  ];
  const failure = validation.find((item) => !item.passed);
  if (failure) {
    addNotification({ source: "LED · ENTRY GATE", message: `${vehicleNumber || "UNKNOWN"} held — ${failure.label}`, tone: "ember" });
    return { ok: false, message: failure.detail, validation };
  }
  const spotNumber = nextSpot();
  if (!spotNumber) return { ok: false, message: "No parking bay is currently available.", validation };
  store.transactions = [{ id: `tx-${Date.now()}`, vehicleNumber, ownerName, vehicleType, spotNumber, entryTime: new Date().toISOString(), exitTime: null, amount: null, status: "Active" }, ...store.transactions];
  addNotification({ source: "LED · ENTRY GATE", message: `${vehicleNumber} cleared — bay ${spotNumber} assigned`, tone: "signal" });
  addNotification({ source: "MOBILE · ADMIN APP", message: `Occupancy updated · ${active().length}/${capacity} bays in use`, tone: "muted" });
  return { ok: true, message: `Access confirmed. Spot ${spotNumber} assigned.`, spotNumber, validation };
}

export function exitVehicle(input: { vehicleNumber?: string }) {
  const vehicleNumber = (input.vehicleNumber || "").trim().toUpperCase().replace(/\s+/g, " ");
  const transaction = active().find((item) => item.vehicleNumber === vehicleNumber);
  if (!transaction) {
    addNotification({ source: "LED · EXIT GATE", message: `${vehicleNumber || "UNKNOWN"} was not found in the active registry`, tone: "ember" });
    return { ok: false, message: "No active parking session was found for that vehicle." };
  }
  const minutes = Math.max(1, Math.ceil((Date.now() - new Date(transaction.entryTime).getTime()) / 60_000));
  const rates: Record<VehicleType, number> = { Car: 80, Bike: 35, Truck: 150 };
  const amount = Math.max(rates[transaction.vehicleType], Math.ceil(minutes / 60) * rates[transaction.vehicleType]);
  transaction.status = "Completed";
  transaction.exitTime = new Date().toISOString();
  transaction.amount = amount;
  addNotification({ source: "LED · EXIT GATE", message: `${vehicleNumber} exit settled · ₹${amount} collected`, tone: "ember" });
  addNotification({ source: "MOBILE · ADMIN APP", message: `Bay ${transaction.spotNumber} is now available`, tone: "muted" });
  return { ok: true, message: `Exit confirmed. ₹${amount} collected.`, amount };
}
