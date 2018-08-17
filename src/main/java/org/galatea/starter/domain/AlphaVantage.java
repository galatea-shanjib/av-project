package org.galatea.starter.domain;

import com.google.gson.JsonObject;
import feign.Param;
import feign.RequestLine;

public interface AlphaVantage {
  @RequestLine("GET /query?function={func}&symbol={sym}&outputsize={size}&apikey={key}")
  JsonObject json(@Param("func") String func,
      @Param("sym") String symbol,
      @Param("size") String size,
      @Param("key") String key);
}
