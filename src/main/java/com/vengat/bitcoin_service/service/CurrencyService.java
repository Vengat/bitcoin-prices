package com.vengat.bitcoin_service.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Currency;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.vengat.bitcoin_service.model.CurrencyApiResponse;
import com.vengat.bitcoin_service.model.CurrencyExchangeRateApiResponse;

import jakarta.annotation.PostConstruct;

@Service
public class CurrencyService {

    @Value("${currency.api.key}")
    private String apiKey;

    @Value("${currency.api.url}")
    private String apiUrl;

    @Value("${supported.currency.url}")
    private String supportedCurrencies;

    @Value("${exchange.rate.update.cron}")
    private String exchangeRateUpdateCron;

    @Value("${supported.currencies.update.cron}")
    private String supportedCurrenciesUpdateCron;

    private final ConcurrentHashMap<String, Double> exchangeRates = new ConcurrentHashMap<>();
    private Set<Currency> supportedCurrenciesCache = new HashSet<>();

    @PostConstruct
    public void init() {
        initializeExchangeRates();
        initializeSupportedCurrencies();
    }
    
    private void initializeExchangeRates() {
        try {
            getLatestExchangeRates("USD", null);
        } catch (Exception e) {
            e.printStackTrace();
            loadExchangeRatesFromFile();
        }
    }
    
    private void initializeSupportedCurrencies() {
        try {
            updateSupportedCurrencies();
        } catch (Exception e) {
            e.printStackTrace();
            loadSupportedCurrenciesFromFile();
        }
    }

    private void loadExchangeRatesFromFile() {
        File file = new File("exchangeRates.ser");
        if (file.exists()) {
            try (FileInputStream fileIn = new FileInputStream(file);
                 ObjectInputStream in = new ObjectInputStream(fileIn)) {
                ConcurrentHashMap<String, Double> loadedExchangeRates = (ConcurrentHashMap<String, Double>) in.readObject();
                exchangeRates.putAll(loadedExchangeRates);
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void loadSupportedCurrenciesFromFile() {
        File file = new File("supportedCurrenciesCache.ser");
        if (file.exists()) {
            try (FileInputStream fileIn = new FileInputStream(file);
                 ObjectInputStream in = new ObjectInputStream(fileIn)) {
                Set<String> loadedCurrencies = (Set<String>) in.readObject();
                supportedCurrenciesCache = loadedCurrencies.stream()
                    .map(Currency::getInstance)
                    .collect(Collectors.toSet());
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

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
            exchangeRates.putAll(data);
            saveExchangeRatesToFile();
            return data;
        } else {
            throw new RuntimeException("Failed to fetch exchange rates");
        }
    }

    private void saveExchangeRatesToFile() {
        try (FileOutputStream fileOut = new FileOutputStream("exchangeRates.ser");
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(exchangeRates);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "${exchange.rate.update.cron}")
    public void scheduleExchangeRateUpdate() {
        getLatestExchangeRates("USD", null);
    }

    public Double getExchangeRate(String currency) {
        return exchangeRates.get(currency);
    }

    public Map<String, Double> getExchangeRates() {
        return exchangeRates;
    }

    @Scheduled(cron = "${supported.currencies.update.cron}")
    public void updateSupportedCurrencies() {
        Set<Currency> newCurrencies = getSupportedCurrencies();
        synchronized (supportedCurrenciesCache) {
            supportedCurrenciesCache.clear();
            supportedCurrenciesCache.addAll(newCurrencies);
            saveSupportedCurrenciesToFile();
        }
    }

    private void saveSupportedCurrenciesToFile() {
        try (FileOutputStream fileOut = new FileOutputStream("supportedCurrenciesCache.ser");
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(supportedCurrenciesCache);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<Currency> getSupportedCurrenciesCache() {
        return supportedCurrenciesCache;
    }

    public Set<Currency> getSupportedCurrencies() {
        ResponseEntity<CurrencyApiResponse> response = new RestTemplate().getForEntity(supportedCurrencies,
                CurrencyApiResponse.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, String> data = response.getBody().getData();
            Set<Currency> currencySet = data.keySet().stream()
                    .map(Currency::getInstance)
                    .collect(Collectors.toSet());
            return currencySet;
        } else {
            throw new RuntimeException("Failed to fetch supported currencies");
        }
    }

    public boolean isCurrencySupported(String currencySymbol) {
        return supportedCurrenciesCache.stream()
                .anyMatch(currency -> currency.getCurrencyCode().equals(currencySymbol));
    }
}