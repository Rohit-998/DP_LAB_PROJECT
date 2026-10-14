import { NextResponse } from "next/server";

const BACKEND_URL = process.env.BACKEND_URL || "http://localhost:8080";

export async function POST(request: Request) {
  try {
    const body = await request.json();
    const response = await fetch(`${BACKEND_URL}/api/exit`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });
    const data = await response.json();

    return NextResponse.json({
      ok: data.success,
      message: data.message,
      amount: data.amount,
      strategy: data.strategy,
    }, { status: data.success ? 200 : 404 });
  } catch (error) {
    return NextResponse.json({
      ok: false,
      message: "Could not reach the backend server. Is it running on port 8080?",
    }, { status: 503 });
  }
}
