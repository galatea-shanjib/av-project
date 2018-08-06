package org.galatea.starter.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import net.sf.aspect4log.Log.Level;
import org.galatea.starter.domain.price.OutputSize;
import org.galatea.starter.domain.price.PriceHistory;
import org.springframework.beans.factory.annotation.Value;

@Data
@Slf4j
@Log(enterLevel = Level.INFO, exitLevel = Level.INFO)
public class AlphaVantageService {

  @NonNull
  @Value("${prices.apiUrl}")
  private String apiUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY";

  @NonNull
  @Value("${prices.apiKey}")
  private String apiKey = "XBLEFGBKUO1U4EYG";

  public PriceHistory getPrices(String symbol, OutputSize size) {
    try {
      String apiParams = "&symbol=" + URLEncoder.encode(symbol, "UTF-8")
         + "&outputsize=" + URLEncoder.encode(size.toString(), "UTF-8")
         + "&apikey=" + URLEncoder.encode(apiKey, "UTF-8");

      URL url = new URL(apiUrl + apiParams);
      InputStream is = url.openStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
      StringBuilder sb = new StringBuilder();
      int cp;
      while ((cp = rd.read()) != -1) {
        sb.append((char) cp);
      }
      is.close();
      String json = sb.toString();
      return new PriceHistory(json);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
