package com.vengat.bitcoin_service.api;

import java.util.List;

import org.json.JSONException;

import com.vengat.bitcoin_service.model.BitcoinPriceResponse;

public interface BitcoinService {

    BitcoinPriceResponse getHistoricalPrices(String startDate, String endDate, String currency);

    List<String> getSupportedCurrencies();

    double convertCurrency(double amount, String fromCurrency, String toCurrency);

    boolean isCurrencySupported(String currency) throws JSONException, Exception;

    boolean isDateValid(String date) throws Exception;

    boolean isAmountValid(double amount) throws Exception;

    boolean isCurrencyCodeValid(String currency) throws Exception;

    boolean isCurrencyCodeValid(String currency, List<String> supportedCurrencies) throws Exception;

    boolean isStartDateBeforeEndDate(String startDate, String endDate) throws Exception;

    boolean isStartDateBeforeEndDate(String startDate, String endDate, String dateFormat) throws Exception;

    boolean isStartDateBeforeEndDate(String startDate, String endDate, String dateFormat, String timeZone)
            throws Exception;

    boolean isStartDateBeforeEndDate(String startDate, String endDate, String dateFormat, String timeZone,
            String locale) throws Exception;

    double getHistoricalHigh(String startDate, String endDate, String currency) throws Exception;

    double getHistoricalLow(String startDate, String endDate, String currency) throws Exception;

    double getHistoricalAverage(String startDate, String endDate, String currency) throws Exception;

    double usdToCurrency(double amount, String currency) throws Exception;
}
