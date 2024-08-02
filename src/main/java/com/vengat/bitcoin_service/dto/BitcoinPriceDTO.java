package com.vengat.bitcoin_service.dto;

import java.util.Date;

public class BitcoinPriceDTO {
    
    private Date date;
    private double price;
    private boolean isMax;
    private boolean isMin;
    private String currency;

    public BitcoinPriceDTO() {
    }

    public BitcoinPriceDTO(Date date, double price, String currency) {
        this.date = date;
        this.price = price;
        this.isMax = false;
        this.isMin = false;
        this.currency = currency;
    }

    // Getters and Setters
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isMax() {
        return isMax;
    }

    public void setMax(boolean isMax) {
        this.isMax = isMax;
    }

    public boolean isMin() {
        return isMin;
    }

    public void setMin(boolean isMin) {
        this.isMin = isMin;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
