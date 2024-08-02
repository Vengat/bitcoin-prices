package com.vengat.bitcoin_service.api;

import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.vengat.bitcoin_service.model.BitcoinPrice;

public interface BitcoinService {

    List<BitcoinPrice> getHistoricalPrices(Date startDate, Date endDate, String currency);

    Set<Currency> getSupportedCurrencies();

    double convertCurrency(double amount, String toCurrency);

    boolean isCurrencySupported(String currency);

    boolean isDateValid(String date) throws Exception;

    boolean isAmountValid(double amount) throws Exception;

    boolean isCurrencyCodeValid(String currency, List<String> supportedCurrencies) throws Exception;

    boolean isStartDateBeforeEndDate(String startDate, String endDate, String dateFormat, String timeZone, String locale) throws Exception;

    double getHistoricalHigh(String startDate, String endDate, String currency) throws Exception;

    double getHistoricalLow(String startDate, String endDate, String currency) throws Exception;

    double getHistoricalAverage(String startDate, String endDate, String currency) throws Exception;

    double usdToCurrency(double amount, String currency) throws Exception;
}