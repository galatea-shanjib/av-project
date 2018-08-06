package org.galatea.starter.service;

import org.galatea.starter.domain.price.PriceHistory;

public class MongoService {

  private String connectionString;
  private MongoService client;

  public PriceHistory getPrices(String symbol, int days) {

    return new PriceHistory();
  }

  public void storePrices(PriceHistory history) {

  }

}
