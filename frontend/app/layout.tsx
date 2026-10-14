import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "Parkdesk · Parking Operations",
  description: "Smart parking management dashboard",
};

export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return <html lang="en"><body>{children}</body></html>;
}
