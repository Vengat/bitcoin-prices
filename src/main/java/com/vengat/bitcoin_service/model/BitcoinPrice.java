package com.vengat.bitcoin_service.model;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class BitcoinPrice implements Comparable<BitcoinPrice>, java.io.Serializable {

    private static final long serialVersionUID = 1L;
    
    private Date date;
    private Double price;
    private boolean isMax;
    private boolean isMin;

    public BitcoinPrice(Date date, Double price) {
        this.date = date;
        this.price = price;
        this.isMax = false;
        this.isMin = false;
    }

    public int compareTo(BitcoinPrice other) {
        return this.date.compareTo(other.date);
    }

    public void sort(BitcoinPrice[] prices) {
        Arrays.sort(prices);
    }

    public Date getDate() {
        return date;
    }

    public Double getPrice() {
        return price;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setPrice(Double price) {
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

    public String toString() {
        return "Date: " + date + ", Price: " + price + ", isMax: " + isMax + ", isMin: " + isMin;
    }

    public static void markMaxMinInRange(List<BitcoinPrice> prices, Date startDate, Date endDate) {
        BitcoinPrice maxPrice = null;
        BitcoinPrice minPrice = null;

        for (BitcoinPrice price : prices) {
            if (!price.getDate().before(startDate) && !price.getDate().after(endDate)) {
                if (maxPrice == null || price.getPrice() > maxPrice.getPrice()) {
                    maxPrice = price;
                }
                if (minPrice == null || price.getPrice() < minPrice.getPrice()) {
                    minPrice = price;
                }
            }
        }

        if (maxPrice != null) {
            maxPrice.setMax(true);
        }
        if (minPrice != null) {
            minPrice.setMin(true);
        }
    }
}