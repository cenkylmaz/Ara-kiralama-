package com.vehicle;

import com.vehicle.dao.DBConnection;
import com.vehicle.model.Rental;
import com.vehicle.model.enums.RentalPeriod;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RentalDAO {
    public boolean createRental(Rental rental) throws SQLException {
        String sql = "INSERT INTO rentals (user_id, vehicle_id, start_date, end_date, rental_period, total_price, deposit_amount) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, rental.getUserId());
            stmt.setInt(2, rental.getVehicleId());
            stmt.setTimestamp(3, Timestamp.valueOf(rental.getStartDate()));
            stmt.setTimestamp(4, Timestamp.valueOf(rental.getEndDate()));
            stmt.setString(5, rental.getRentalPeriod().toString());
            stmt.setBigDecimal(6, rental.getTotalPrice());
            stmt.setBigDecimal(7, rental.getDepositAmount());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    rental.setId(generatedKeys.getInt(1));
                }
            }
            return true;
        }
    }

    public List<Rental> getRentalsByUser(int userId) throws SQLException {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT r.*, v.brand, v.model FROM rentals r " +
                "JOIN vehicles v ON r.vehicle_id = v.id " +
                "WHERE r.user_id = ? ORDER BY r.start_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Rental rental = new Rental();
                rental.setId(rs.getInt("id"));
                rental.setUserId(rs.getInt("user_id"));
                rental.setVehicleId(rs.getInt("vehicle_id"));
                rental.setStartDate(String.valueOf(rs.getTimestamp("start_date").toLocalDateTime()));
                rental.setEndDate(String.valueOf(rs.getTimestamp("end_date").toLocalDateTime()));
                rental.setRentalPeriod(RentalPeriod.valueOf(rs.getString("rental_period")));
                rental.setTotalPrice(rs.getBigDecimal("total_price"));
                rental.setDepositAmount(rs.getBigDecimal("deposit_amount"));
                rental.setStatus(rs.getString("status"));
                rental.setVehicleInfo(rs.getString("brand") + " " + rs.getString("model"));
                rentals.add(rental);
            }
        }
        return rentals;
    }
}
