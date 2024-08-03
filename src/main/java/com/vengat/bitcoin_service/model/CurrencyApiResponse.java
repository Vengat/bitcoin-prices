package com.vengat.bitcoin_service.model;

import java.util.Currency;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vengat.bitcoin_service.config.CurrencyDeserializer;
import com.vengat.bitcoin_service.config.CurrencySerializer;

public class CurrencyApiResponse {

    @JsonSerialize(using = CurrencySerializer.class)
    @JsonDeserialize(using = CurrencyDeserializer.class)
    private Currency currency;
    private String country;

    public CurrencyApiResponse() {
    }

    public CurrencyApiResponse(Currency currency, String country) {
        this.currency = currency;
        this.country = country;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}