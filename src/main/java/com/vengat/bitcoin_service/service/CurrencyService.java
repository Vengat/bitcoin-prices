package com.vengat.bitcoin_service.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.vengat.bitcoin_service.model.Currency;
import com.vengat.bitcoin_service.model.CurrencyApiResponse;
import com.vengat.bitcoin_service.model.CurrencyExchangeRateApiResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class CurrencyService {

    @Value("${currency.api.key}")
    private String apiKey;

    @Value("${currency.api.url}")
    private String apiUrl;

    @Value("${supported.currency.url}")
    private String supportedCurrencies;

    private final ConcurrentHashMap<String, Double> exchangeRates = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> supportedCurrenciesMap = new ConcurrentHashMap<>();

    public Map<String, Double> getLatestExchangeRates(String baseCurrency, String currencies) {
        StringBuilder uriBuilder = new StringBuilder(apiUrl);
        uriBuilder.append("?apikey=").append(apiKey);

        if (baseCurrency != null && !baseCurrency.isEmpty()) {
            uriBuilder.append("&base_currency=").append(baseCurrency);
        }
        if (currencies != null && !currencies.isEmpty()) {
            uriBuilder.append("&currencies=").append(currencies);
        }

        String uri = uriBuilder.toString();
        ResponseEntity<CurrencyExchangeRateApiResponse> response = new RestTemplate().getForEntity(uri,
                CurrencyExchangeRateApiResponse.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Double> data = response.getBody().getData();
            exchangeRates.putAll(data); // Store the response in the ConcurrentHashMap
            return data;
        } else {
            // Handle error
            throw new RuntimeException("Failed to fetch exchange rates");
        }
    }

    @Scheduled(cron = "0 0 8,20 * * *")
    public void scheduleExchangeRateUpdate() {
        // Call the method with appropriate parameters
        getLatestExchangeRates("USD", null);
    }

    public Double getExchangeRate(String currency) {
        return exchangeRates.get(currency);
    }

    public Map<String, Double> getExchangeRates() {
        return exchangeRates;
    }

    public Map<String, String> getSupportedCurrencies_Old() {
        ResponseEntity<CurrencyApiResponse> response = new RestTemplate().getForEntity(supportedCurrencies,
                CurrencyApiResponse.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, String> data = response.getBody().getData();
            supportedCurrenciesMap.putAll(data); // Store the response in the ConcurrentHashMap
            return data;
        } else {
            // Handle error
            throw new RuntimeException("Failed to fetch supported currencies");
        }
    }

    public Set<Currency> getSupportedCurrencies() {
        ResponseEntity<CurrencyApiResponse> response = new RestTemplate().getForEntity(supportedCurrencies,
                CurrencyApiResponse.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, String> data = response.getBody().getData();
            Set<Currency> currencySet = data.entrySet().stream()
                    .map(entry -> new Currency(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toSet());
            return currencySet;
        } else {
            // Handle error
            throw new RuntimeException("Failed to fetch supported currencies");
        }
    }

}