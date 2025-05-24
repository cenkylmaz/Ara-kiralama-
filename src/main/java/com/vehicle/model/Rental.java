package com.vehicle.model;

import com.vehicle.model.enums.RentalPeriod;

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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Object getRentalPeriod() {
        return rentalPeriod;
    }

    public void setRentalPeriod(Object rentalPeriod) {
        this.rentalPeriod = rentalPeriod;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setStatus(String status) {
    }

    public void setVehicleInfo(String s) {
    }

    // Constructors, getters, setters
}