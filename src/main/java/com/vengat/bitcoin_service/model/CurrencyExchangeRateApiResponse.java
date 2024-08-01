package com.vengat.bitcoin_service.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CurrencyExchangeRateApiResponse {
    @JsonProperty("data")
    private Map<String, Double> data;

    public Map<String, Double> getData() {
        return data;
    }

    public void setData(Map<String, Double> data) {
        this.data = data;
    }
}
