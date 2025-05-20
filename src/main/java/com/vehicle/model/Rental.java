package com.vehicle.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Rental {
    private int id;
    private int userId;
    private int vehicleId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private RentalPeriod rentalPeriod;
    private BigDecimal totalPrice;
    private BigDecimal depositAmount;
    private String status;

    // Constructors, getters, setters
}