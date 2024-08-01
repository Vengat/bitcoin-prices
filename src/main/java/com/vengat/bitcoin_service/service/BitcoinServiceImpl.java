package com.vengat.bitcoin_service.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vengat.bitcoin_service.api.BitcoinService;
import com.vengat.bitcoin_service.cache.BitcoinBtree;
import com.vengat.bitcoin_service.model.BitcoinPrice;
import com.vengat.bitcoin_service.model.BitcoinPriceResponse;
import com.vengat.bitcoin_service.util.RestUtil;

import jakarta.annotation.PostConstruct;

@Service
public class BitcoinServiceImpl implements BitcoinService {

    // private static final String HISTORICAL_PRICE_URL = "https://api.coindesk.com/v1/bpi/historical/close.json?";
    private static final String SUPPORTED_CURRENCIES_URL = "https://api.coindesk.com/v1/bpi/supported-currencies.json";

    @Value("${historical.price.url}")
    private String historicalPriceURL;

    @Value("${supported.currencies.url}")
    private String supportedCurrenciesURL;

    private BitcoinBtree bTree;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private CurrencyService currencyService;

    @Value("${daily.price.update.cron}")
    private String dailyPriceFetchCron;

    public BitcoinServiceImpl() {
        bTree = new BitcoinBtree(6);
    }

    @PostConstruct
    public void init() {
        // Deserialize the BTree from the file
        bTree.deserializeFromFile();
        fetchDailyBitcoinPrices();
    }

    @Scheduled(cron = "${daily.price.update.cron}")
    public void fetchDailyBitcoinPrices() {
        // Fetch daily bitcoin prices and update the BTree
        JSONArray jsonResponse;
        try {
            jsonResponse = new JSONArray(RestUtil.sendGetRequest(historicalPriceURL));
            BitcoinPriceResponse response = objectMapper.readValue(jsonResponse.toString(),
                    BitcoinPriceResponse.class);
            List<BitcoinPrice> bitcoinPrices = response.toBitcoinPriceList();
            bTree.insertList(bitcoinPrices);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public BitcoinPriceResponse getHistoricalPrices(String startDate, String endDate, String currency) {
        // Fetch historical bitcoin prices from the BTree
        throw new UnsupportedOperationException("Unimplemented method 'getHistoricalPrices'");
    }

    @Override
    public List<String> getSupportedCurrencies() {
        try {
            String response = RestUtil.sendGetRequest(SUPPORTED_CURRENCIES_URL);
            JSONArray jsonResponse = new JSONArray(response);
            List<String> currencies = new ArrayList<>();
            for (int i = 0; i < jsonResponse.length(); i++) {
                JSONObject currencyObj = jsonResponse.getJSONObject(i);
                currencies.add(currencyObj.getString("currency"));
            }
            return currencies;
        } catch (Exception e) {
            e.printStackTrace();
            return null; // In production, consider a more robust error handling strategy.
        }
    }

    @Override
    public double convertCurrency(double amount, String fromCurrency, String toCurrency) {
        // Simulate currency conversion. Implement actual conversion logic based on your
        // requirements.
        return amount; // Placeholder implementation
    }

    @Override
    public boolean isCurrencySupported(String currency) throws JSONException, Exception {
        String urlString = String.format(SUPPORTED_CURRENCIES_URL, currency);
        JSONArray jsonResponse = new JSONArray(RestUtil.sendGetRequest(urlString));

        for (int i = 0; i < jsonResponse.length(); i++) {
            JSONObject currencyObj = jsonResponse.getJSONObject(i);
            if (currencyObj.getString("currency").equals(currency)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isDateValid(String date) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isDateValid'");
    }

    @Override
    public boolean isAmountValid(double amount) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isAmountValid'");
    }

    @Override
    public boolean isCurrencyCodeValid(String currency) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isCurrencyCodeValid'");
    }

    @Override
    public boolean isCurrencyCodeValid(String currency, List<String> supportedCurrencies) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isCurrencyCodeValid'");
    }

    @Override
    public boolean isStartDateBeforeEndDate(String startDate, String endDate) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isStartDateBeforeEndDate'");
    }

    @Override
    public boolean isStartDateBeforeEndDate(String startDate, String endDate, String dateFormat) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isStartDateBeforeEndDate'");
    }

    @Override
    public boolean isStartDateBeforeEndDate(String startDate, String endDate, String dateFormat, String timeZone)
            throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isStartDateBeforeEndDate'");
    }

    @Override
    public boolean isStartDateBeforeEndDate(String startDate, String endDate, String dateFormat, String timeZone,
            String locale) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isStartDateBeforeEndDate'");
    }

    @Override
    public double getHistoricalHigh(String startDate, String endDate, String currency) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getHistoricalHigh'");
    }

    @Override
    public double getHistoricalLow(String startDate, String endDate, String currency) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getHistoricalLow'");
    }

    @Override
    public double getHistoricalAverage(String startDate, String endDate, String currency) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getHistoricalAverage'");
    }

    @Override
    public double usdToCurrency(double amount, String currency) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'usdToCurrency'");
    }

}
