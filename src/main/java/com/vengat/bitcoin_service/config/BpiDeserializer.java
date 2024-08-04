package com.vengat.bitcoin_service.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.vengat.bitcoin_service.model.BitcoinPriceResponse;

public class BpiDeserializer extends JsonDeserializer<BitcoinPriceResponse.Bpi> {

    @Override
    public BitcoinPriceResponse.Bpi deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        Map<String, Double> prices = new HashMap<>();

        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            prices.put(field.getKey(), field.getValue().asDouble());
        }

        BitcoinPriceResponse.Bpi bpi = new BitcoinPriceResponse.Bpi();
        bpi.setPrices(prices);
        return bpi;
    }
}
