package com.vengat.bitcoin_service.model;

import java.util.Map;

public class CurrencyApiResponse {
    private Map<String, String> data;

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}