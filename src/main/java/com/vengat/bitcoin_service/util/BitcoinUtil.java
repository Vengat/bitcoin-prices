package com.vengat.bitcoin_service.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.vengat.bitcoin_service.model.BitcoinPrice;

public class BitcoinUtil {

    public static boolean isStartDateAndEndDateBeforeCurrentDate(String startDate, String endDate) {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date startDateObj = dateFormat.parse(startDate);
            Date endDateObj = dateFormat.parse(endDate);
            return startDateObj.before(currentDate) && endDateObj.before(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<BitcoinPrice> convertToBitcoinPriceList(Map<Date, Double> prices) {
        List<BitcoinPrice> bitcoinPrices = new ArrayList<>();
        for (Map.Entry<Date, Double> entry : prices.entrySet()) {
            bitcoinPrices.add(new BitcoinPrice(entry.getKey(), entry.getValue()));
        }
        return bitcoinPrices;
    }

}
