package org.galatea.starter.entrypoint;

import org.galatea.starter.domain.price.OutputSize;
import org.galatea.starter.domain.price.PriceHistory;
import org.galatea.starter.service.AlphaVantageService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PriceController {

  @RequestMapping("${mvc.getPricesPath}")
  public PriceHistory processPriceRequest (@RequestParam(value="stock", defaultValue="ZZZZ") String symbol,
                                           @RequestParam(value="days", defaultValue = "0") int days) {
    AlphaVantageService avs = new AlphaVantageService();
    PriceHistory history = avs.getPrices(symbol, days > 100 ? OutputSize.Full : OutputSize.Compact);
    history.keepRelevantData(days);
    return history;
  }
}
