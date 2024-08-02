// package com.vengat.bitcoin_service.service;

// import static org.mockito.Mockito.*;
// import static org.junit.jupiter.api.Assertions.*;

// import java.io.*;
// import java.util.*;
// import java.util.concurrent.ConcurrentHashMap;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.*;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.ResponseEntity;
// import org.springframework.test.util.ReflectionTestUtils;
// import org.springframework.web.client.RestTemplate;

// import com.vengat.bitcoin_service.model.CurrencyApiResponse;
// import com.vengat.bitcoin_service.model.CurrencyExchangeRateApiResponse;

// @ExtendWith(MockitoExtension.class)
// public class CurrencyServiceTest {

//     @InjectMocks
//     private CurrencyService currencyService;

//     @Mock
//     private RestTemplate restTemplate;

//     @Value("${currency.api.key}")
//     private String apiKey = "testApiKey";

//     @Value("${currency.api.url}")
//     private String apiUrl = "http://testapi.com";

//     @Value("${supported.currency.url}")
//     private String supportedCurrencies = "http://testapi.com/supported";

//     @BeforeEach
//     public void setUp() {
//         ReflectionTestUtils.setField(currencyService, "apiKey", apiKey);
//         ReflectionTestUtils.setField(currencyService, "apiUrl", apiUrl);
//         ReflectionTestUtils.setField(currencyService, "supportedCurrencies", supportedCurrencies);
//     }

//     @Test
//     public void testInit() {
//         currencyService.init();
//         assertFalse(currencyService.getExchangeRates().isEmpty());
//         assertFalse(currencyService.getSupportedCurrenciesCache().isEmpty());
//     }

//     @Test
//     public void testGetLatestExchangeRates_Success() {
//         CurrencyExchangeRateApiResponse response = new CurrencyExchangeRateApiResponse();
//         response.setData(Map.of("USD", 1.0, "EUR", 0.85));
//         when(restTemplate.getForEntity(anyString(), eq(CurrencyExchangeRateApiResponse.class)))
//                 .thenReturn(ResponseEntity.ok(response));

//         Map<String, Double> rates = currencyService.getLatestExchangeRates("USD", "EUR");
//         assertEquals(2, rates.size());
//         assertEquals(1.0, rates.get("USD"));
//         assertEquals(0.85, rates.get("EUR"));
//     }

//     @Test
//     public void testGetLatestExchangeRates_Failure() {
//         when(restTemplate.getForEntity(anyString(), eq(CurrencyExchangeRateApiResponse.class)))
//                 .thenReturn(ResponseEntity.status(500).build());

//         assertThrows(RuntimeException.class, () -> currencyService.getLatestExchangeRates("USD", "EUR"));
//     }

//     @Test
//     public void testLoadExchangeRatesFromFile() throws IOException, ClassNotFoundException {
//         ConcurrentHashMap<String, Double> mockRates = new ConcurrentHashMap<>();
//         mockRates.put("USD", 1.0);
//         mockRates.put("EUR", 0.85);

//         try (FileOutputStream fileOut = new FileOutputStream("exchangeRates.ser");
//              ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
//             out.writeObject(mockRates);
//         }

//         currencyService.loadExchangeRatesFromFile();
//         assertEquals(2, currencyService.getExchangeRates().size());
//         assertEquals(1.0, currencyService.getExchangeRates().get("USD"));
//         assertEquals(0.85, currencyService.getExchangeRates().get("EUR"));
//     }

//     @Test
//     public void testSaveExchangeRatesToFile() throws IOException, ClassNotFoundException {
//         currencyService.getExchangeRates().put("USD", 1.0);
//         currencyService.getExchangeRates().put("EUR", 0.85);

//         currencyService.saveExchangeRatesToFile();

//         try (FileInputStream fileIn = new FileInputStream("exchangeRates.ser");
//              ObjectInputStream in = new ObjectInputStream(fileIn)) {
//             ConcurrentHashMap<String, Double> loadedRates = (ConcurrentHashMap<String, Double>) in.readObject();
//             assertEquals(2, loadedRates.size());
//             assertEquals(1.0, loadedRates.get("USD"));
//             assertEquals(0.85, loadedRates.get("EUR"));
//         }
//     }

//     @Test
//     public void testGetSupportedCurrencies_Success() {
//         CurrencyApiResponse response = new CurrencyApiResponse();
//         response.setData(Map.of("USD", "United States Dollar", "EUR", "Euro"));
//         when(restTemplate.getForEntity(anyString(), eq(CurrencyApiResponse.class)))
//                 .thenReturn(ResponseEntity.ok(response));

//         Set<Currency> currencies = currencyService.getSupportedCurrencies();
//         assertEquals(2, currencies.size());
//         assertTrue(currencies.contains(Currency.getInstance("USD")));
//         assertTrue(currencies.contains(Currency.getInstance("EUR")));
//     }

//     @Test
//     public void testGetSupportedCurrencies_Failure() {
//         when(restTemplate.getForEntity(anyString(), eq(CurrencyApiResponse.class)))
//                 .thenReturn(ResponseEntity.status(500).build());

//         assertThrows(RuntimeException.class, () -> currencyService.getSupportedCurrencies());
//     }

//     @Test
//     public void testLoadSupportedCurrenciesFromFile() throws IOException, ClassNotFoundException {
//         Set<String> mockCurrencies = new HashSet<>();
//         mockCurrencies.add("USD");
//         mockCurrencies.add("EUR");

//         try (FileOutputStream fileOut = new FileOutputStream("supportedCurrenciesCache.ser");
//              ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
//             out.writeObject(mockCurrencies);
//         }

//         currencyService.loadSupportedCurrenciesFromFile();
//         assertEquals(2, currencyService.getSupportedCurrenciesCache().size());
//         assertTrue(currencyService.getSupportedCurrenciesCache().contains(Currency.getInstance("USD")));
//         assertTrue(currencyService.getSupportedCurrenciesCache().contains(Currency.getInstance("EUR")));
//     }

//     @Test
//     public void testSaveSupportedCurrenciesToFile() throws IOException, ClassNotFoundException {
//         currencyService.getSupportedCurrenciesCache().add(Currency.getInstance("USD"));
//         currencyService.getSupportedCurrenciesCache().add(Currency.getInstance("EUR"));

//         currencyService.saveSupportedCurrenciesToFile();

//         try (FileInputStream fileIn = new FileInputStream("supportedCurrenciesCache.ser");
//              ObjectInputStream in = new ObjectInputStream(fileIn)) {
//             Set<String> loadedCurrencies = (Set<String>) in.readObject();
//             assertEquals(2, loadedCurrencies.size());
//             assertTrue(loadedCurrencies.contains("USD"));
//             assertTrue(loadedCurrencies.contains("EUR"));
//         }
//     }

//     @Test
//     public void testIsCurrencySupported() {
//         currencyService.getSupportedCurrenciesCache().add(Currency.getInstance("USD"));
//         assertTrue(currencyService.isCurrencySupported("USD"));
//         assertFalse(currencyService.isCurrencySupported("EUR"));
//     }

//     @Test
//     public void testGetUSDExchangeRate() {
//         currencyService.getExchangeRates().put("USD", 1.0);
//         assertEquals(1.0, currencyService.getUSDExchangeRate("USD"));
//     }
// }