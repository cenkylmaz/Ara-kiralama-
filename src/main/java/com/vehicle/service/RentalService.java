package com.vehicle.service;

import com.vehicle.dao.RentalDAO;
import com.vehicle.dao.VehicleDAO;
import com.vehicle.model.Rental;
import com.vehicle.model.User;
import com.vehicle.model.Vehicle;
import com.vehicle.exception.RentalException;
import com.vehicle.model.enums.RentalPeriod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class RentalService {
    private com.vehicle.service.RentalDAO rentalDAO;
    private VehicleDAO vehicleDAO;
    private VehicleService vehicleService;

    public RentalService() {
        this.rentalDAO = new RentalDAO();
        this.vehicleDAO = new VehicleDAO();
        this.vehicleService = new VehicleService();
    }

    public Rental createRental(User user, Vehicle vehicle, RentalPeriod period, LocalDateTime startDate)
            throws RentalException {
        try {
            // Validate rental rules
            validateRentalRules(user, vehicle, period);

            // Calculate end date based on period
            LocalDateTime endDate = calculateEndDate(startDate, period);

            // Calculate total price
            BigDecimal totalPrice = vehicleService.calculateRentalPrice(vehicle, period);

            // Calculate deposit if needed
            BigDecimal deposit = calculateDeposit(user, vehicle);

            // Create rental object
            Rental rental = new Rental();
            rental.setUserId(user.getId());
            rental.setVehicleId(vehicle.getId());
            rental.setStartDate(startDate);
            rental.setEndDate(endDate);
            rental.setRentalPeriod(period);
            rental.setTotalPrice(totalPrice);
            rental.setDepositAmount(deposit);

            // Save to database
            if (rentalDAO.createRental(rental)) {
                return rental;
            } else {
                throw new RentalException("Failed to create rental");
            }
        } catch (Exception e) {
            throw new RentalException("Rental creation failed: " + e.getMessage());
        }
    }

    public List<Rental> getUserRentals(int userId) {
        try {
            return rentalDAO.getRentalsByUser(userId);
        } catch (Exception e) {
            throw new RentalException("Error retrieving rentals: " + e.getMessage());
        }
    }

    private void validateRentalRules(User user, Vehicle vehicle, RentalPeriod period) throws RentalException {
        // Corporate users can only rent monthly
        if (user.getUserType() == UserType.CORPORATE && period != RentalPeriod.MONTHLY) {
            throw new RentalException("Corporate users must rent for at least one month");
        }

        // Check vehicle availability
        if (!vehicle.isAvailable()) {
            throw new RentalException("Vehicle is not available for rental");
        }
    }

    private LocalDateTime calculateEndDate(LocalDateTime startDate, RentalPeriod period) {
        switch (period) {
            case HOURLY:
                return startDate.plus(1, ChronoUnit.HOURS);
            case DAILY:
                return startDate.plus(1, ChronoUnit.DAYS);
            case WEEKLY:
                return startDate.plus(1, ChronoUnit.WEEKS);
            case MONTHLY:
                return startDate.plus(1, ChronoUnit.MONTHS);
            default:
                return startDate.plus(1, ChronoUnit.DAYS);
        }
    }

    private BigDecimal calculateDeposit(User user, Vehicle vehicle) {
        UserService userService = new UserService();
        int userAge = userService.calculateUserAge(user);

        // If vehicle price > 2M and user age > 30, require 10% deposit
        if (vehicle.getPrice().compareTo(new BigDecimal("2000000")) > 0 && userAge > 30) {
            return vehicle.getPrice().multiply(new BigDecimal("0.1"));
        }
        return BigDecimal.ZERO;
    }

    private class RentalDAO extends com.vehicle.service.RentalDAO {
    }
}
