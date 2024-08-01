package com.vengat.bitcoin_service.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Set;

public class Currency {
    private String currency;
    private String country;

    // Getters and Setters
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    // toString method
    @Override
    public String toString() {
        return "Currency{" +
                "currency='" + currency + '\'' +
                ", country='" + country + '\'' +
                '}';
    }

    // equals method
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Currency currency1 = (Currency) o;

        if (currency != null ? !currency.equals(currency1.currency) : currency1.currency != null)
            return false;
        return country != null ? country.equals(currency1.country) : currency1.country == null;
    }

    // hashCode method
    @Override
    public int hashCode() {
        int result = currency != null ? currency.hashCode() : 0;
        result = 31 * result + (country != null ? country.hashCode() : 0);
        return result;
    }

    public Set<Currency> deserializeCurrencyJson(String jsonArray) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonArray, new TypeReference<Set<Currency>>() {
        });
    }
}