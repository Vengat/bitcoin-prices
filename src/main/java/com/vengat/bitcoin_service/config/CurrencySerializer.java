package com.vengat.bitcoin_service.config;

import java.io.IOException;
import java.util.Currency;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class CurrencySerializer extends StdSerializer<Currency> {

    public CurrencySerializer() {
        super(Currency.class);
    }

    @Override
    public void serialize(Currency currency, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(currency.getCurrencyCode());
    }
}