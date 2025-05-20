package com.vehicle.service;

import com.vehicle.dao.VehicleDAO;
import com.vehicle.model.Vehicle;
import com.vehicle.exception.VehicleNotFoundException;
import java.math.BigDecimal;
import java.util.List;

public class VehicleService {
    private VehicleDAO vehicleDAO;

    public VehicleService() {
        this.vehicleDAO = new VehicleDAO();
    }

    public List<Vehicle> getAllVehicles(int page, int pageSize) {
        try {
            int offset = (page - 1) * pageSize;
            return vehicleDAO.getAllVehicles(pageSize, offset);
        } catch (Exception e) {
            throw new VehicleNotFoundException("Error retrieving vehicles: " + e.getMessage());
        }
    }

    public List<Vehicle> getVehiclesByType(VehicleType type, int page, int pageSize) {
        try {
            int offset = (page - 1) * pageSize;
            return vehicleDAO.getVehiclesByType(type, pageSize, offset);
        } catch (Exception e) {
            throw new VehicleNotFoundException("Error retrieving vehicles by type: " + e.getMessage());
        }
    }

    public Vehicle getVehicleById(int id) {
        try {
            Vehicle vehicle = vehicleDAO.getVehicleById(id);
            if (vehicle == null) {
                throw new VehicleNotFoundException("Vehicle not found with ID: " + id);
            }
            return vehicle;
        } catch (Exception e) {
            throw new VehicleNotFoundException("Error retrieving vehicle: " + e.getMessage());
        }
    }

    public boolean addVehicle(Vehicle vehicle) {
        try {
            return vehicleDAO.addVehicle(vehicle);
        } catch (Exception e) {
            throw new VehicleNotFoundException("Error adding vehicle: " + e.getMessage());
        }
    }

    public BigDecimal calculateRentalPrice(Vehicle vehicle, RentalPeriod period) {
        BigDecimal basePrice = vehicle.getPrice();
        BigDecimal multiplier;

        switch (period) {
            case HOURLY:
                multiplier = new BigDecimal("0.05");
                break;
            case DAILY:
                multiplier = new BigDecimal("0.2");
                break;
            case WEEKLY:
                multiplier = new BigDecimal("1.0");
                break;
            case MONTHLY:
                multiplier = new BigDecimal("3.5");
                break;
            default:
                multiplier = BigDecimal.ONE;
        }

        return basePrice.multiply(multiplier);
    }
}
