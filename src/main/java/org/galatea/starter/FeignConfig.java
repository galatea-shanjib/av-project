package org.galatea.starter;

import feign.Feign;
import feign.gson.GsonDecoder;
import org.galatea.starter.domain.AlphaVantage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

  @Value("${prices.apiUrl}")
  private String apiUrl;

  @Bean
  public AlphaVantage alphaVantage() {
    return Feign.builder()
        .decoder(new GsonDecoder())
        .target(AlphaVantage.class, apiUrl);
  }
}
