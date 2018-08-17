package org.galatea.starter.entrypoint;

import static org.mockito.Mockito.when;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.galatea.starter.domain.price.PriceHistory;
import org.galatea.starter.service.PricesService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PriceControllerTest {

  @Mock
  private PricesService pricesService;
  private PricesController priceController;
  private JsonObject json;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    priceController = new PricesController(pricesService);
    String jstring = "{\"Meta Data\": {\"1. Information\": \"Daily Prices (open, high, low, close) and Volumes\",\"2. Symbol\": \"aapl\",\"3. Last Refreshed\": \"2018-08-14 11:50:49\",\"4. Output Size\": \"Compact\",\"5. Time Zone\": \"US/Eastern\"},\"Time Series (Daily)\": {\"2018-08-14\": {\"1. open\": \"210.1550\",\"2. high\": \"210.5600\",\"3. low\": \"208.2600\", \"4. close\": \"209.4895\",\"5. volume\": \"9596306\"}}}";
    JsonParser parser = new JsonParser();
    json = (JsonObject) parser.parse(jstring);

  }

  @Test
  public void testPriceController_happy() {
    when(pricesService.getPrices("aapl", 1)).thenReturn(new PriceHistory(json));
    PriceHistory history = priceController.processPriceRequest("aapl", 1);
    Assert.assertNotNull(history);
  }

  @Test
  public void testPriceController_unhappySymbol() {
    String errorMessage = "Error in stock symbol or days asked for, please check and try again.";
    PriceHistory test = new PriceHistory();
    test.setMessage(errorMessage);
    when(pricesService.getPrices("123", 1)).thenReturn(test);
    PriceHistory history = priceController.processPriceRequest("123", 1);
    Assert.assertNotNull(history);
    Assert.assertArrayEquals(errorMessage.toCharArray(), history.getMessage().toCharArray());
  }
}
