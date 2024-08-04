package com.vengat.bitcoin_service.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.vengat.bitcoin_service.config.BpiDeserializer;

public class BitcoinPriceResponse {

    private Bpi bpi;
    private String disclaimer;
    private Time time;

    public BitcoinPriceResponse() {
    }

    public Bpi getBpi() {
        return bpi;
    }

    public void setBpi(Bpi bpi) {
        this.bpi = bpi;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public List<BitcoinPrice> toBitcoinPriceList() {
        List<BitcoinPrice> bitcoinPrices = new ArrayList<>();
        if (bpi != null && bpi.getPrices() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            for (Map.Entry<String, Double> entry : bpi.getPrices().entrySet()) {
                try {
                    Date date = dateFormat.parse(entry.getKey());
                    BitcoinPrice bitcoinPrice = new BitcoinPrice(date, entry.getValue());
                    bitcoinPrices.add(bitcoinPrice);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitcoinPrices;
    }

    @JsonDeserialize(using = BpiDeserializer.class)
    public static class Bpi {
        private Map<String, Double> prices;

        public Bpi() {
        }

        public Map<String, Double> getPrices() {
            return prices;
        }

        public void setPrices(Map<String, Double> prices) {
            this.prices = prices;
        }
    }

    public static class Time {
        private String updated;
        private String updatedISO;

        public Time() {
        }

        public String getUpdated() {
            return updated;
        }

        public void setUpdated(String updated) {
            this.updated = updated;
        }

        public String getUpdatedISO() {
            return updatedISO;
        }

        public void setUpdatedISO(String updatedISO) {
            this.updatedISO = updatedISO;
        }
    }
}