package org.galatea.starter.service;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import net.sf.aspect4log.Log.Level;
import org.galatea.starter.domain.AlphaVantage;
import org.galatea.starter.domain.price.OutputSize;
import org.galatea.starter.domain.price.PriceHistory;
import org.galatea.starter.domain.rpsy.PricesRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@RequiredArgsConstructor
@Slf4j
@Log(enterLevel = Level.INFO, exitLevel = Level.INFO)
public class PricesService{

  @Autowired
  private PricesRepository repository;
  @Autowired
  private AlphaVantage alphaVantage;
  @NonNull
  private String apiKey;
  @NonNull
  private String apiFunction;

  /**
   * Return the price history of the stock for the number of days asked for
   * Check if we have it already in the database first
   * If not pull from Alpha Vantage API and store in database
   */
  public PriceHistory getPrices(String symbol, double days) {
    PriceHistory history = new PriceHistory();
    if (validateSymbol(symbol) && validateDays(days)) {
      history = getPricesFromMongo(symbol);

      if (history == null || history.getDailyPrices().size() < days) {
        PriceHistory avHistory = getPricesFromAlphaVantage(symbol,
            days > 100 ? OutputSize.Full : OutputSize.Compact);
        if (avHistory.isNull()) {
          log.error("Alpha Vantage did not return data.");
          if (history == null) {
            history = avHistory;
          } else {
            history.setDebugMessage("Gotten from MongoDB");
          }
        } else {
          if (history != null) {
            deleteFromMongo(history);          // Delete copy from database to store new information
            storeInMongo(avHistory);
          }
          history = avHistory;
          history.setDebugMessage("Gotten from AlphaVantage");
        }
      } else {
        history.setDebugMessage("Gotten from MongoDB");
      }

      if (history.isNull()) {
        history.setMessage("Request unsucessful, error retrieving data.");
      } else if (history.getDailyPrices().size() < days) {
        history.setMessage("Error retrieving full data, please find partial data below.");
      } else {
        history.setMessage("Request successful, please find data below.");
        history.keepRelevantData(days);
      }

    } else {
      log.error("Symbol or Days input invalid.");
      history.setMessage("Error in stock symbol or days asked for, please check and try again.");
    }
    return history;
  }

  private PriceHistory getPricesFromMongo(String symbol) {
    log.info("Retrieving data from Mongo instance.");
    return repository.findByMetadata_Symbol(symbol);
  }

  private void storeInMongo(PriceHistory history) {
    log.info("Storing data in Mongo instance.");
    repository.save(history);
  }

  private void deleteFromMongo(PriceHistory history) {
    log.info("Deleting data from Mongo instance.");
    repository.delete(history.getId());
  }

  private PriceHistory getPricesFromAlphaVantage(String symbol, OutputSize size) {
    try {
      // AlphaVantage returns an error with capital Full/Compact for size
      log.info("Getting data from Alpha Vantage API");
      return new PriceHistory(alphaVantage
          .json(apiFunction, symbol, size.toString().toLowerCase(), apiKey));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  // If the symbol has a number, is too long, or is empty, return false
  private boolean validateSymbol(String symbol) {
    return !(symbol.matches(".*\\d+.*") || symbol.length() > 6 || symbol.isEmpty());
  }

  // If the days are negative, greater than 20 years, or has a decimal, return false
  private boolean validateDays(double days) {
    return days > 0 && days < 15000 && days == (int)days;
  }
}

