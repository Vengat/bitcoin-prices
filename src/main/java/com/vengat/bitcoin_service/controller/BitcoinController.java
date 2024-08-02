package com.vengat.bitcoin_service.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vengat.bitcoin_service.api.BitcoinService;
import com.vengat.bitcoin_service.dto.BitcoinPriceDTO;
import com.vengat.bitcoin_service.model.BitcoinPrice;
import com.vengat.bitcoin_service.util.BitcoinPriceConverter;

@RestController
@RequestMapping("/api/bitcoin")
public class BitcoinController {

    @Autowired
    private BitcoinService bitcoinService;

    @GetMapping("/historical-prices")
    public List<BitcoinPriceDTO> getHistoricalPrices(@RequestParam Date startDate, 
                                                     @RequestParam Date endDate, 
                                                     @RequestParam String currency) {
        List<BitcoinPrice> prices = bitcoinService.getHistoricalPrices(startDate, endDate, currency);
        return BitcoinPriceConverter.toDTOList(prices, currency);
    }
}