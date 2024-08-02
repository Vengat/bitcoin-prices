package com.vengat.bitcoin_service.service;

import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


    private static final Logger logger = LoggerFactory.getLogger(BitcoinServiceImpl.class);
    
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
            logger.error("Error fetching daily bitcoin prices", e);
            e.printStackTrace();
        }

    }

    @Override
    public List<BitcoinPrice> getHistoricalPrices(Date startDate, Date endDate, String currency) {
        if (startDate == null || endDate == null) {
            logger.error("Start date and end date cannot be null");
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }
        if (currency == null || currency.isEmpty()) {
            logger.error("Currency cannot be null or empty");
            throw new IllegalArgumentException("Currency cannot be null or empty");
        }
        if (!isCurrencySupported(currency)) {
            logger.error("Currency not supported");
            throw new IllegalArgumentException("Currency not supported");
        }
        List<BitcoinPrice> prices = bTree.search_range(startDate, endDate);
        if (prices == null || prices.isEmpty()) {
            logger.error("No data found for the given date range");
            throw new IllegalArgumentException("No data found for the given date range");
        }
        double exchangeRate = currencyService.getUSDExchangeRate(currency);
        for (BitcoinPrice price : prices) {
            price.setPrice(price.getPrice() * exchangeRate);
        }
        logger.info("Returning historical prices for the given date range");
        return prices;
    }

    @Override
    public Set<Currency> getSupportedCurrencies() {
        try {
            return currencyService.getSupportedCurrencies();
        } catch (Exception e) {
            logger.error("Failed to fetch supported currencies", e);
            e.printStackTrace();
            return null; // In production, consider a more robust error handling strategy.
        }
    }

    @Override
    public double convertCurrency(double amount, String toCurrency) {
        double exchangeRate = currencyService.getUSDExchangeRate(toCurrency);
        logger.info("Converting {} USD to {}", amount, toCurrency);
        return amount * exchangeRate; 
    }

    @Override
    public boolean isCurrencySupported(String currency) {
        logger.info("Checking if currency {} is supported", currency);
        return currencyService.isCurrencySupported(currency);
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
        double exchangeRate = currencyService.getUSDExchangeRate(currency);
        return amount * exchangeRate; 
    }

    @Override
    public boolean isCurrencyCodeValid(String currency, List<String> supportedCurrencies) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
