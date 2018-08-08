package org.galatea.starter.service;

import com.google.gson.JsonObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import feign.Feign;
import feign.Param;
import feign.RequestLine;
import feign.gson.GsonDecoder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import net.sf.aspect4log.Log.Level;
import org.galatea.starter.domain.price.OutputSize;
import org.galatea.starter.domain.price.PriceHistory;

@Data
@RequiredArgsConstructor
@Slf4j
@Log(enterLevel = Level.INFO, exitLevel = Level.INFO)
public class PricesService{

  @NonNull
  private String apiUrl;
  @NonNull
  private String apiKey;
  @NonNull
  private String apiFunction;
  private String mongoURL = "mongodb://localhost:27017";

  public PriceHistory getPrices(String symbol, double days) {
    PriceHistory history = new PriceHistory();
    if (validateSymbol(symbol) && validateDays(days)) {
      history = getPricesFromAlphaVantage(symbol,
          days > 100 ? OutputSize.Full : OutputSize.Compact);
      storeInMongo(history);
      history.keepRelevantData(days);
      history.setMessage("Request successful, please find data below.");
    } else {
      history.setMessage("Error in stock symbol or days ask for, please check and try again.");
    }
    return history;
  }

  private PriceHistory getPricesFromMongo(String symbol, double days) {
    // check out Spring Data for Mongo stuff
    return new PriceHistory();
  }

  private void storeInMongo(PriceHistory history) {
    try {
      MongoClient client = new MongoClient();
      DB db = client.getDB("Prices");
      DBCollection collection = db.getCollection(history.getMetadata().getSymbol());
      collection.insert(history.toDBObject());
      client.close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private PriceHistory getPricesFromAlphaVantage(String symbol, OutputSize size) {
    try {
      AlphaVantage av = Feign.builder()
          .decoder(new GsonDecoder())
          .target(AlphaVantage.class, apiUrl);
      JsonObject json = av.json(apiFunction, symbol, size.toString(), apiKey);
      return new PriceHistory(json);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private boolean validateSymbol(String symbol) {
    return !(symbol.matches(".*\\d+.*") || symbol.length() > 6 || symbol.isEmpty());
  }

  private boolean validateDays(double days) {
    return days > 0 && days < 15000 && days == (int)days;
  }
}

interface AlphaVantage {
  @RequestLine("GET /query?function={func}&symbol={sym}&outputsize={size}&apikey={key}")
  JsonObject json(@Param("func") String func,
      @Param("sym") String symbol,
      @Param("size") String size,
      @Param("key") String key);
}
