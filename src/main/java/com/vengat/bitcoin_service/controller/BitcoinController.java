package com.vengat.bitcoin_service.controller;

import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vengat.bitcoin_service.api.BitcoinService;
import com.vengat.bitcoin_service.dto.BitcoinPriceDTO;
import com.vengat.bitcoin_service.model.BitcoinPrice;
import com.vengat.bitcoin_service.service.CurrencyService;
import com.vengat.bitcoin_service.util.BitcoinPriceConverter;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/bitcoin")
@Api(value = "Bitcoin Controller", description = "REST APIs related to Bitcoin Entity")
public class BitcoinController {

    @Autowired
    private BitcoinService bitcoinService;

    @Autowired
    private CurrencyService currencyService;

    @ApiOperation(value = "Get Historical Prices", notes = "Fetch historical prices of Bitcoin")
    @GetMapping("/historical-prices")
    public EntityModel<List<BitcoinPriceDTO>> getHistoricalPrices(
            @ApiParam(value = "Start Date", required = true) @RequestParam Date startDate,
            @ApiParam(value = "End Date", required = true) @RequestParam Date endDate,
            @ApiParam(value = "Currency", required = true) @RequestParam String currency) {
        List<BitcoinPrice> prices = bitcoinService.getHistoricalPrices(startDate, endDate, currency);
        List<BitcoinPriceDTO> priceDTOs = BitcoinPriceConverter.toDTOList(prices, currency);

        EntityModel<List<BitcoinPriceDTO>> resource = EntityModel.of(priceDTOs);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(BitcoinController.class)
                .getHistoricalPrices(startDate, endDate, currency)).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(BitcoinController.class)
                .getSupportedCurrencies()).withRel("supported-currencies"));

        return resource;
    }

    @ApiOperation(value = "Get Supported Currencies", notes = "Fetch supported currencies")
    @GetMapping("/supported-currencies")
    public Set<Currency> getSupportedCurrencies() {
        return currencyService.getSupportedCurrencies();
    }
}