package com.vengat.bitcoin_service.cache;

import java.time.LocalDate;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;

public class ConcurrentSkipListIndex {

    private static class DatePricePair {
        private LocalDate date;
        private double price;

        public DatePricePair(LocalDate date, double price) {
            this.date = date;
            this.price = price;
        }

        public LocalDate getDate() {
            return date;
        }

        public double getPrice() {
            return price;
        }
    }

    private final NavigableMap<LocalDate, DatePricePair> datePriceMap = new ConcurrentSkipListMap<>();

    public void add(DatePricePair pair) {
        datePriceMap.put(pair.getDate(), pair);
    }

    public NavigableMap<LocalDate, DatePricePair> getRange(LocalDate startDate, LocalDate endDate) {
        endDate = endDate.plusDays(1);
        return datePriceMap.subMap(startDate, true, endDate, false);
    }

    public double getPriceAtDate(LocalDate date) {
        DatePricePair pair = datePriceMap.get(date);
        if (pair != null) {
            return pair.getPrice();
        } else {
            throw new IllegalArgumentException("No price found for date: " + date);
        }
    }

    public NavigableMap<LocalDate, DatePricePair> getPricesBeforeDate(LocalDate endDate) {
        return datePriceMap.headMap(endDate, false);
    }

    public NavigableMap<LocalDate, DatePricePair> getPricesOnOrAfterDate(LocalDate startDate) {
        return datePriceMap.tailMap(startDate, true);
    }

    public NavigableMap<LocalDate, DatePricePair> getFullRange() {
        return datePriceMap;
    }

    public void clear() {
        datePriceMap.clear();
    }

    public int size() {
        return datePriceMap.size();
    }

    public boolean isEmpty() {
        return datePriceMap.isEmpty();
    }

    public boolean containsDate(LocalDate date) {
        return datePriceMap.containsKey(date);
    }

    public boolean containsPrice(double price) {
        return datePriceMap.values().stream().anyMatch(pair -> pair.getPrice() == price);
    }

    public boolean remove(LocalDate date) {
        return datePriceMap.remove(date) != null;
    }

    public boolean remove(DatePricePair pair) {
        return datePriceMap.remove(pair.getDate(), pair);
    }

    public boolean remove(LocalDate date, double price) {
        return datePriceMap.remove(date, new DatePricePair(date, price));
    }

}
