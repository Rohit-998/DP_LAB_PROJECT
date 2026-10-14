import { NextResponse } from "next/server";

const BACKEND_URL = process.env.BACKEND_URL || "http://localhost:8080";

export async function POST(request: Request) {
  try {
    const body = await request.json();
    const response = await fetch(`${BACKEND_URL}/api/entry`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });
    const data = await response.json();

    // Map backend response to frontend format
    const validation = [
      { label: "Registration check", passed: data.success !== false || data.message?.includes("parked"), detail: data.success ? "Vehicle identity is valid" : data.message || "Validation failed" },
      { label: "Parking availability", passed: data.success !== false, detail: data.success ? `Spots available: ${data.availableSpots}` : "No spots available" },
      { label: "Vehicle type", passed: data.success !== false || !data.message?.includes("Unknown"), detail: data.success ? `${body.vehicleType} is accepted` : data.message || "Invalid type" },
      { label: "Access permit", passed: data.success === true, detail: data.success ? "Permit approved for entry" : data.message || "Entry denied" },
    ];

    return NextResponse.json({
      ok: data.success,
      message: data.message,
      spotNumber: data.spotNumber,
      validation,
    }, { status: data.success ? 201 : 422 });
  } catch (error) {
    return NextResponse.json({
      ok: false,
      message: "Could not reach the backend server. Is it running on port 8080?",
      validation: [],
    }, { status: 503 });
  }
}
