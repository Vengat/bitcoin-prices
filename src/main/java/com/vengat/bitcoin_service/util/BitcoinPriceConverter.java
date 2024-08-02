package com.vengat.bitcoin_service.util;

import java.util.List;
import java.util.stream.Collectors;

import com.vengat.bitcoin_service.dto.BitcoinPriceDTO;
import com.vengat.bitcoin_service.model.BitcoinPrice;

public class BitcoinPriceConverter {

    public static BitcoinPriceDTO toDTO(BitcoinPrice bitcoinPrice, String currency) {
        BitcoinPriceDTO dto = new BitcoinPriceDTO();
        dto.setDate(bitcoinPrice.getDate());
        dto.setPrice(bitcoinPrice.getPrice());
        dto.setMax(bitcoinPrice.isMax());
        dto.setMin(bitcoinPrice.isMin());
        dto.setCurrency(currency);
        return dto;
    }

    public static List<BitcoinPriceDTO> toDTOList(List<BitcoinPrice> bitcoinPrices, String currency) {
        return bitcoinPrices.stream()
                            .map(price -> toDTO(price, currency))
                            .collect(Collectors.toList());
    }
    
}
