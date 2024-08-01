package com.vengat.bitcoin_service.model;

import java.util.Arrays;
import java.util.Date;

public class BitcoinPrice implements Comparable<BitcoinPrice> {

    private Date date;
    private Double price;

    public BitcoinPrice(Date date, Double price) {
        this.date = date;
        this.price = price;
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

    public String toString() {
        return "Date: " + date + ", Price: " + price;
    }
}
