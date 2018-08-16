package org.galatea.starter;

import org.galatea.starter.service.PricesService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PricesConfig {

  @Value("${prices.apiKey}")
  private String apiKey;
  @Value("${prices.apiFunction}")
  private String apiFunction;

  @Bean
  public PricesService pricesService() {
    return new PricesService(apiKey, apiFunction);
  }
}
