// package com.vengat.bitcoin_service.controller;

// import java.text.SimpleDateFormat;
// import java.util.Arrays;
// import java.util.Currency;
// import java.util.Date;
// import java.util.List;
// import java.util.Set;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import static org.mockito.ArgumentMatchers.any;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import static org.mockito.Mockito.when;
// import org.mockito.MockitoAnnotations;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
// import org.springframework.test.web.servlet.setup.MockMvcBuilders;

// import com.vengat.bitcoin_service.api.BitcoinService;
// import com.vengat.bitcoin_service.dto.BitcoinPriceDTO;
// import com.vengat.bitcoin_service.model.BitcoinPrice;
// import com.vengat.bitcoin_service.service.CurrencyService;
// import com.vengat.bitcoin_service.util.BitcoinPriceConverter;

// public class BitcoinControllerTest {

//     private MockMvc mockMvc;

//     @Mock
//     private BitcoinService bitcoinService;

//     @Mock
//     private CurrencyService currencyService;

//     @InjectMocks
//     private BitcoinController bitcoinController;

//     @BeforeEach
//     public void setUp() {
//         MockitoAnnotations.openMocks(this);
//         mockMvc = MockMvcBuilders.standaloneSetup(bitcoinController).build();
//     }

//     @Test
//     public void testGetHistoricalPrices() throws Exception {
//         // Arrange
//         SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//         Date startDate = dateFormat.parse("2023-10-01");
//         Date endDate = dateFormat.parse("2023-10-02");
//         String currency = "USD";

//         BitcoinPrice price1 = new BitcoinPrice(startDate, 100.0);
//         BitcoinPrice price2 = new BitcoinPrice(endDate, 200.0);
//         List<BitcoinPrice> prices = Arrays.asList(price1, price2);
//         List<BitcoinPriceDTO> priceDTOs = BitcoinPriceConverter.toDTOList(prices, currency);

//         when(bitcoinService.getHistoricalPrices(any(Date.class), any(Date.class), any(String.class)))
//             .thenReturn(prices);

//         // Act & Assert
//         mockMvc.perform(get("/historical-prices")
//             .param("startDate", dateFormat.format(startDate))
//             .param("endDate", dateFormat.format(endDate))
//             .param("currency", currency)
//             .contentType(MediaType.APPLICATION_JSON))
//             .andExpect(status().isOk());
//             // .andExpect(jsonPath("$[0].currency").value(currency));
//     }

//     @Test
//     public void testGetSupportedCurrencies() throws Exception {

//         Set<Currency> supportedCurrencies = Set.of(Currency.getInstance("USD"), Currency.getInstance("EUR"),
//                 Currency.getInstance("GBP"), Currency.getInstance("INR"));

//         when(currencyService.getSupportedCurrencies()).thenReturn(supportedCurrencies);

//         mockMvc.perform(get("/api/bitcoin/supported-currencies")
//                 .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(content().json("[USD, EUR, GBP, INR]"));
//     }
// }