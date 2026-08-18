package com.parking.smart_parking.core.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParkingDAO {

    // Insert a new parking transaction
    public void insertTransaction(String vehicleNumber, String vehicleType, String spotNumber, String zone) {
        String sql = "INSERT INTO ParkingTransaction (vehicleNumber, vehicleType, spotNumber, zone, status) VALUES (?, ?, ?, ?, 'Active')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vehicleNumber);
            pstmt.setString(2, vehicleType);
            pstmt.setString(3, spotNumber);
            pstmt.setString(4, zone);
            pstmt.executeUpdate();
            System.out.println("Transaction saved for: " + vehicleNumber);
        } catch (Exception e) {
            System.out.println("Insert failed: " + e.getMessage());
        }
    }

    // Update transaction on exit
    public void exitTransaction(String vehicleNumber, double amount) {
        String sql = "UPDATE ParkingTransaction SET exitTime = CURRENT_TIMESTAMP, amount = ?, status = 'Completed' WHERE vehicleNumber = ? AND status = 'Active'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setString(2, vehicleNumber);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Exit recorded for: " + vehicleNumber + " | Amount: ₹" + amount);
            }
        } catch (Exception e) {
            System.out.println("Exit update failed: " + e.getMessage());
        }
    }

    // Get all active (currently parked) vehicles
    public List<Map<String, String>> getActiveTransactions() {
        String sql = "SELECT * FROM ParkingTransaction WHERE status = 'Active'";
        List<Map<String, String>> results = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("vehicleNumber", rs.getString("vehicleNumber"));
                row.put("vehicleType", rs.getString("vehicleType"));
                row.put("spotNumber", rs.getString("spotNumber"));
                row.put("zone", rs.getString("zone"));
                row.put("entryTime", rs.getString("entryTime"));
                results.add(row);
            }
        } catch (Exception e) {
            System.out.println("Query failed: " + e.getMessage());
        }
        return results;
    }

    // Get all transactions
    public List<Map<String, String>> getAllTransactions() {
        String sql = "SELECT * FROM ParkingTransaction ORDER BY id DESC";
        List<Map<String, String>> results = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("id", rs.getString("id"));
                row.put("vehicleNumber", rs.getString("vehicleNumber"));
                row.put("vehicleType", rs.getString("vehicleType"));
                row.put("spotNumber", rs.getString("spotNumber"));
                row.put("zone", rs.getString("zone"));
                row.put("entryTime", rs.getString("entryTime"));
                row.put("exitTime", rs.getString("exitTime"));
                row.put("amount", rs.getString("amount"));
                row.put("status", rs.getString("status"));
                results.add(row);
            }
        } catch (Exception e) {
            System.out.println("Query failed: " + e.getMessage());
        }
        return results;
    }
}
