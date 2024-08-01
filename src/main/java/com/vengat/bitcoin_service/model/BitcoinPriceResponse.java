package com.vengat.bitcoin_service.model;

import java.util.Map;

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