package com.vengat.bitcoin_service.config;

import java.io.IOException;
import java.util.Currency;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

public class CurrencyDeserializer extends JsonDeserializer<Currency> {

    private static final Set<String> CUSTOM_CURRENCY_CODES = new HashSet<>();

    static {
        CUSTOM_CURRENCY_CODES.add("BTC");
        CUSTOM_CURRENCY_CODES.add("GGP");
        CUSTOM_CURRENCY_CODES.add("IMP");
        CUSTOM_CURRENCY_CODES.add("JEP");
        CUSTOM_CURRENCY_CODES.add("XBT");
        // Add other custom currency codes here if needed
    }

    @Override
    public Currency deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String currencyCode = p.getText();
        try {
            if (CUSTOM_CURRENCY_CODES.contains(currencyCode)) {
                // Handle custom currency codes
                return Currency.getInstance("USD"); // Default to USD or any other logic
            } else {
                // Handle standard ISO 4217 currency codes
                return Currency.getInstance(currencyCode);
            }
        } catch (IllegalArgumentException e) {
            // Handle invalid currency code
            throw new JsonMappingException(p, "Invalid currency code: " + currencyCode, e);
        }
    }
}