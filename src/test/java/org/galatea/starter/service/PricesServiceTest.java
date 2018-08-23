package org.galatea.starter.service;

import static org.mockito.Mockito.when;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.galatea.starter.domain.AlphaVantage;
import org.galatea.starter.domain.price.OutputSize;
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
  private JsonObject compactTestJson, fullTestJson;
  private String errorMessage, mongoDebugMessage, alphaDebugMessage, unsuccessfulMessage, testSymbol;
  private double testCompactDays, testFullDays;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    String compactJsonString = "{\"Meta Data\": "
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
    String fullJsonString = "{\"Meta Data\": "
            + "{\"1. Information\": \"Daily Prices (open, high, low, close) and Volumes\","
            + "\"2. Symbol\": \"aapl\","
            + "\"3. Last Refreshed\": \"2018-08-14 11:50:49\","
            + "\"4. Output Size\": \"Compact\","
            + "\"5. Time Zone\": \"US/Eastern\"},"
            + "\"Time Series (Daily)\": {";
    for(int i = 0; i < 200; i++) {
      fullJsonString += "\"2018-08-" + String.valueOf(i) + "\": {"
              + "\"1. open\": \"210.1550\","
              + "\"2. high\": \"210.5600\","
              + "\"3. low\": \"208.2600\", "
              + "\"4. close\": \"209.4895\","
              + "\"5. volume\": \"\"},";
    }
    fullJsonString = fullJsonString.substring(0, fullJsonString.length() - 1);
    fullJsonString += "}}";
    JsonParser parser = new JsonParser();
    compactTestJson = (JsonObject) parser.parse(compactJsonString);
    fullTestJson = (JsonObject) parser.parse(fullJsonString);
    compactTestPriceHistory = new PriceHistory(compactTestJson);
    fullTestPriceHistory = new PriceHistory(fullTestJson);

    errorMessage = "Error in stock symbol or days asked for, please check and try again.";
    mongoDebugMessage = "Gotten from MongoDB";
    alphaDebugMessage = "Gotten from AlphaVantage";
    unsuccessfulMessage = "Request unsucessful, error retrieving data.";
    testSymbol = "aapl";
    testCompactDays = 1;
    testFullDays = 105;
  }

  @Test
  public void unhappy_invalidSymbol() {
    PriceHistory history = testPricesService.getPrices("123", 54);
    Assert.assertNotNull(history);
    Assert.assertArrayEquals(errorMessage.toCharArray(), history.getMessage().toCharArray());
  }

  @Test
  public void unhappy_invalidDays() {
    PriceHistory history = testPricesService.getPrices("aapl", -5);
    Assert.assertNotNull(history);
    Assert.assertArrayEquals(errorMessage.toCharArray(), history.getMessage().toCharArray());
  }

  @Test
  public void happy_CompactMongo() {
    when(testPricesRepository.findByMetadata_Symbol(testSymbol))
        .thenReturn(compactTestPriceHistory);
    PriceHistory history = testPricesService.getPrices(testSymbol, testCompactDays);

    Assert.assertNotNull(history);
    Assert.assertEquals(testCompactDays, history.getDailyPrices().size(), 0);
    Assert.assertEquals(mongoDebugMessage, history.getDebugMessage());
  }

  @Test
  public void happy_FullMongo() {
    when(testPricesRepository.findByMetadata_Symbol(testSymbol))
        .thenReturn(fullTestPriceHistory);
    PriceHistory history = testPricesService.getPrices(testSymbol, testFullDays);

    Assert.assertNotNull(history);
    Assert.assertEquals(testFullDays, history.getDailyPrices().size(), 0);
    Assert.assertEquals(mongoDebugMessage, history.getDebugMessage());
  }

  @Test
  public void happy_CompactAlpha() {
    OutputSize testSize = OutputSize.Compact;
    when(testAlphaVantage.json(testPricesService.getApiFunction(), testSymbol,
        testSize.toString().toLowerCase(), testPricesService.getApiKey()))
        .thenReturn(compactTestJson);
    PriceHistory history = testPricesService.getPrices(testSymbol, testCompactDays);

    Assert.assertNotNull(history);
    Assert.assertEquals(testCompactDays, history.getDailyPrices().size(), 0);
    Assert.assertEquals(alphaDebugMessage, history.getDebugMessage());
  }

  @Test
  public void happy_FullAlpha() {
    OutputSize testSize = OutputSize.Full;
    when(testAlphaVantage.json(testPricesService.getApiFunction(), testSymbol,
            testSize.toString().toLowerCase(), testPricesService.getApiKey()))
            .thenReturn(fullTestJson);
    PriceHistory history = testPricesService.getPrices(testSymbol, testFullDays);

    Assert.assertNotNull(history);
    Assert.assertEquals(testFullDays, history.getDailyPrices().size(), 0);
    Assert.assertEquals(alphaDebugMessage, history.getDebugMessage());
  }

  @Test
  public void happy_CompactMongoFullAlpha() {
    when(testPricesRepository.findByMetadata_Symbol(testSymbol))
            .thenReturn(compactTestPriceHistory);
    when(testAlphaVantage.json(testPricesService.getApiFunction(), testSymbol,
            OutputSize.Full.toString().toLowerCase(), testPricesService.getApiKey()))
            .thenReturn(fullTestJson);
    PriceHistory history = testPricesService.getPrices(testSymbol, testFullDays);

    Assert.assertNotNull(history);
    Assert.assertEquals(testFullDays, history.getDailyPrices().size(), 0);
    Assert.assertEquals(alphaDebugMessage, history.getDebugMessage());
  }

  @Test
  public void unhappy_AvDown() {
      PriceHistory history = testPricesService.getPrices(testSymbol, testFullDays);
      Assert.assertEquals(unsuccessfulMessage, history.getMessage());
  }

  @Test
  public void unhappy_AvDownCompactMongo() {
    when(testPricesRepository.findByMetadata_Symbol(testSymbol))
            .thenReturn(compactTestPriceHistory);
    PriceHistory history = testPricesService.getPrices(testSymbol, testFullDays);

    Assert.assertNotNull(history);
    Assert.assertEquals(testCompactDays, history.getDailyPrices().size(), 0);
    Assert.assertEquals(mongoDebugMessage, history.getDebugMessage());
  }
}
