package org.galatea.starter.service;

import static org.mockito.Mockito.when;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import org.galatea.starter.domain.AlphaVantage;
import org.galatea.starter.domain.price.OutputSize;
import org.galatea.starter.domain.price.PriceDaily;
import org.galatea.starter.domain.price.PriceHistory;
import org.galatea.starter.domain.rpsy.PricesRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PricesServiceTest {
  @MockBean
  private PricesRepository testPricesRepository;
  @MockBean
  private AlphaVantage testAlphaVantage;
  @Autowired
  private PricesService testPricesService;

  private PriceHistory compactTestPriceHistory, fullTestPriceHistory;
  private JsonObject compactTestJson;
  private String errorMessage, mongoDebugMessage, alphaDebugMessage, testSymbol;
  private double testCompactDays, testFullDays;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    String jString = "{\"Meta Data\": "
        + "{\"1. Information\": \"Daily Prices (open, high, low, close) and Volumes\","
        + "\"2. Symbol\": \"aapl\","
        + "\"3. Last Refreshed\": \"2018-08-14 11:50:49\","
        + "\"4. Output Size\": \"Compact\","
        + "\"5. Time Zone\": \"US/Eastern\"},"
        + "\"Time Series (Daily)\": {\"2018-08-14\": "
        + "{\"1. open\": \"210.1550\","
        + "\"2. high\": \"210.5600\","
        + "\"3. low\": \"208.2600\", "
        + "\"4. close\": \"209.4895\","
        + "\"5. volume\": \"9596306\"}}}";
    JsonParser parser = new JsonParser();
    compactTestJson = (JsonObject) parser.parse(jString);
    compactTestPriceHistory = new PriceHistory(compactTestJson);

    List<PriceDaily> priceDailies = new ArrayList<>();
    for(int i = 0; i < 200; i++) {
      priceDailies.add(new PriceDaily());
    }
    fullTestPriceHistory = new PriceHistory();
    fullTestPriceHistory.setDailyPrices(priceDailies);

    errorMessage = "Error in stock symbol or days asked for, please check and try again.";
    mongoDebugMessage = "Gotten from MongoDB";
    alphaDebugMessage = "Gotten from AlphaVantage";
    testSymbol = "aapl";
    testCompactDays = 1;
    testFullDays = 105;
  }

  @Test
  public void testPricesService_invalidSymbol() {
    PriceHistory history = testPricesService.getPrices("123", 54);
    Assert.assertNotNull(history);
    Assert.assertArrayEquals(errorMessage.toCharArray(), history.getMessage().toCharArray());
  }

  @Test
  public void testPricesService_invalidDays() {
    PriceHistory history = testPricesService.getPrices("aapl", -5);
    Assert.assertNotNull(history);
    Assert.assertArrayEquals(errorMessage.toCharArray(), history.getMessage().toCharArray());
  }

  @Test
  public void testPricesService_happyCompactMongo() {
    when(testPricesRepository.findByMetadata_Symbol(testSymbol))
        .thenReturn(compactTestPriceHistory);
    PriceHistory history = testPricesService.getPrices(testSymbol, testCompactDays);

    Assert.assertNotNull(history);
    Assert.assertEquals(testCompactDays, history.getDailyPrices().size(), 0);
    Assert.assertEquals(mongoDebugMessage, history.getDebugMessage());
  }

  @Test
  public void testPricesService_happyFullMongo() {
    when(testPricesRepository.findByMetadata_Symbol(testSymbol))
        .thenReturn(fullTestPriceHistory);
    PriceHistory history = testPricesService.getPrices(testSymbol, testFullDays);

    Assert.assertNotNull(history);
    Assert.assertEquals(testFullDays, history.getDailyPrices().size(), 0);
    Assert.assertEquals(mongoDebugMessage, history.getDebugMessage());
  }

  @Test
  public void testPricesService_happyCompactAlpha() {
    OutputSize testSize = OutputSize.Compact;
    when(testAlphaVantage.json(testPricesService.getApiFunction(), testSymbol,
        testSize.toString().toLowerCase(), testPricesService.getApiKey()))
        .thenReturn(compactTestJson);
    PriceHistory history = testPricesService.getPrices(testSymbol, testCompactDays);

    Assert.assertNotNull(history);
    Assert.assertEquals(testCompactDays, history.getDailyPrices().size(), 0);
    Assert.assertEquals(alphaDebugMessage, history.getDebugMessage());
  }
}
