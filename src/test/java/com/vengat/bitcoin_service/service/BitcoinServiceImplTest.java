// package com.vengat.bitcoin_service.service;

// import java.util.Arrays;
// import java.util.Date;
// import java.util.List;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import static org.mockito.Mockito.when;
// import org.springframework.test.web.servlet.MockMvc;

// import com.vengat.bitcoin_service.cache.BitcoinBtree;
// import com.vengat.bitcoin_service.model.BitcoinPrice;

// public class BitcoinServiceImplTest {

//     private MockMvc mockMvc;

//     @Mock
//     private CurrencyService currencyService;

//     @Mock
//     private BitcoinBtree bTree;

//     @InjectMocks
//     private BitcoinServiceImpl bitcoinService;

//     private Date startDate;
//     private Date endDate;
//     private String currency;

//     @BeforeEach
//     public void setUp() {
//         startDate = new Date();
//         endDate = new Date();
//         currency = "USD";
//     }

//     @Test
//     public void testGetHistoricalPrices_NullStartDateOrEndDate() {
//         IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//             bitcoinService.getHistoricalPrices(null, endDate, currency);
//         });
//         assertEquals("Start date and end date cannot be null", exception.getMessage());

//         exception = assertThrows(IllegalArgumentException.class, () -> {
//             bitcoinService.getHistoricalPrices(startDate, null, currency);
//         });
//         assertEquals("Start date and end date cannot be null", exception.getMessage());
//     }

//     @Test
//     public void testGetHistoricalPrices_NullOrEmptyCurrency() {
//         IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//             bitcoinService.getHistoricalPrices(startDate, endDate, null);
//         });
//         assertEquals("Currency cannot be null or empty", exception.getMessage());

//         exception = assertThrows(IllegalArgumentException.class, () -> {
//             bitcoinService.getHistoricalPrices(startDate, endDate, "");
//         });
//         assertEquals("Currency cannot be null or empty", exception.getMessage());
//     }

//     @Test
//     public void testGetHistoricalPrices_UnsupportedCurrency() {
//         when(bitcoinService.isCurrencySupported(currency)).thenReturn(false);

//         IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//             bitcoinService.getHistoricalPrices(startDate, endDate, currency);
//         });
//         assertEquals("Currency not supported", exception.getMessage());
//     }

//     @Test
//     public void testGetHistoricalPrices_NoDataFound() {
//         when(bitcoinService.isCurrencySupported(currency)).thenReturn(true);
//         when(bTree.search_range(startDate, endDate)).thenReturn(null);

//         IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//             bitcoinService.getHistoricalPrices(startDate, endDate, currency);
//         });
//         assertEquals("No data found for the given date range", exception.getMessage());
//     }

//     @Test
//     public void testGetHistoricalPrices_Success() {
//         when(bitcoinService.isCurrencySupported(currency)).thenReturn(true);
//         List<BitcoinPrice> prices = Arrays.asList(new BitcoinPrice(new Date(), 100.0));
//         when(bTree.search_range(startDate, endDate)).thenReturn(prices);
//         when(currencyService.getUSDExchangeRate(currency)).thenReturn(1.5);

//         List<BitcoinPrice> result = bitcoinService.getHistoricalPrices(startDate, endDate, currency);

//         assertNotNull(result);
//         assertEquals(1, result.size());
//         assertEquals(150.0, result.get(0).getPrice());
//     }

    
// }