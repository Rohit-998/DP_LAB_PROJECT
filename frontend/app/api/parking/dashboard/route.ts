import { NextResponse } from "next/server";

const BACKEND_URL = process.env.BACKEND_URL || "http://localhost:8080";

export async function GET() {
  try {
    // Fetch spots and transactions from backend in parallel
    const [spotsRes, transactionsRes, activeRes] = await Promise.all([
      fetch(`${BACKEND_URL}/api/spots`, { cache: "no-store" }),
      fetch(`${BACKEND_URL}/api/transactions`, { cache: "no-store" }),
      fetch(`${BACKEND_URL}/api/transactions/active`, { cache: "no-store" }),
    ]);

    const spots = await spotsRes.json();
    const transactions = await transactionsRes.json();
    const activeTransactions = await activeRes.json();

    // Map backend transactions to frontend format
    const mappedTransactions = transactions.map((tx: any, index: number) => ({
      id: tx.id || `tx-${index}`,
      vehicleNumber: tx.vehicleNumber || "",
      ownerName: tx.ownerName || "—",
      vehicleType: tx.vehicleType || "Car",
      spotNumber: tx.spotNumber || "—",
      entryTime: tx.entryTime || new Date().toISOString(),
      exitTime: tx.exitTime || null,
      amount: tx.amount ? parseFloat(tx.amount) : null,
      status: tx.status === "Active" ? "Active" : "Completed",
    }));

    const totalSpots = spots.totalSpots || 100;
    const availableSpots = spots.availableSpots || 100;
    const occupiedSpots = totalSpots - availableSpots;
    const occupancy = totalSpots > 0 ? Math.round((occupiedSpots / totalSpots) * 100) : 0;

    // Generate notifications from recent activity
    const notifications = activeTransactions.slice(0, 5).map((tx: any, i: number) => ({
      id: `n-${i}`,
      source: "LED · ENTRY GATE" as const,
      message: `${tx.vehicleNumber} parked at bay ${tx.spotNumber}`,
      timestamp: tx.entryTime || new Date().toISOString(),
      tone: "signal" as const,
    }));

    return NextResponse.json({
      stats: { totalSpots, availableSpots, occupiedSpots, occupancy },
      transactions: mappedTransactions,
      notifications,
    });
  } catch (error) {
    // Fallback: return empty dashboard if backend is unreachable
    return NextResponse.json({
      stats: { totalSpots: 100, availableSpots: 100, occupiedSpots: 0, occupancy: 0 },
      transactions: [],
      notifications: [{ id: "err-1", source: "MOBILE · ADMIN APP", message: "Backend offline — check if Spring Boot is running on port 8080", timestamp: new Date().toISOString(), tone: "ember" }],
    });
  }
}
