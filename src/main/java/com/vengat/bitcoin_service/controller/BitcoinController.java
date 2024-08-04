package com.vengat.bitcoin_service.controller;

import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vengat.bitcoin_service.api.BitcoinService;
import com.vengat.bitcoin_service.dto.BitcoinPriceDTO;
import com.vengat.bitcoin_service.exceptions.InvalidDateException;
import com.vengat.bitcoin_service.exceptions.NoDataFoundException;
import com.vengat.bitcoin_service.exceptions.UnsupportedCurrencyException;
import com.vengat.bitcoin_service.model.BitcoinPrice;
import com.vengat.bitcoin_service.service.CurrencyService;
import com.vengat.bitcoin_service.util.BitcoinPriceConverter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/bitcoin")
@Tag(name = "Bitcoin", description = "API for Bitcoin operations")
public class BitcoinController {

    @Autowired
    private BitcoinService bitcoinService;

    @Autowired
    private CurrencyService currencyService;

    @Operation(summary = "Get Historical Prices", description = "Get historical prices for Bitcoin")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved prices"),
        @ApiResponse(responseCode = "400", description = "Invalid input parameters"),
        @ApiResponse(responseCode = "404", description = "Prices not found")
    })
    @GetMapping("/historical-prices")
    public EntityModel<List<BitcoinPriceDTO>> getHistoricalPrices(
            @Parameter(description = "Start Date", required = true, schema = @Schema(type = "string", format = "date"))
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @Parameter(description = "End Date", required = true, schema = @Schema(type = "string", format = "date"))
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @Parameter(description = "Currency", required = true) @RequestParam String currency) {

        List<BitcoinPrice> prices = bitcoinService.getHistoricalPrices(startDate, endDate, currency);
        List<BitcoinPriceDTO> priceDTOs = BitcoinPriceConverter.toDTOList(prices, currency);

        EntityModel<List<BitcoinPriceDTO>> resource = EntityModel.of(priceDTOs);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(BitcoinController.class)
                .getHistoricalPrices(startDate, endDate, currency)).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(BitcoinController.class)
                .getSupportedCurrencies()).withRel("supported-currencies"));

        return resource;
    }

    @ExceptionHandler(InvalidDateException.class)
    public ResponseEntity<String> handleInvalidDateException(InvalidDateException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(UnsupportedCurrencyException.class)
    public ResponseEntity<String> handleUnsupportedCurrencyException(UnsupportedCurrencyException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(NoDataFoundException.class)
    public ResponseEntity<String> handleNoDataFoundException(NoDataFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @Operation(summary = "Get Supported Currencies", description = "Get the list of supported currencies")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved supported currencies")
    })
    @GetMapping("/supported-currencies")
    public Set<Currency> getSupportedCurrencies() {
        return currencyService.getSupportedCurrencies();
    }
}