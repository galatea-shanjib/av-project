package org.galatea.starter.entrypoint;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.galatea.starter.domain.price.PriceHistory;
import org.galatea.starter.service.PricesService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PriceController extends BaseRestController {

  @NonNull
  PricesService pricesService;

  @RequestMapping("${webservice.getPricesPath}")
  public PriceHistory processPriceRequest (@RequestParam(value="stock", defaultValue="ZZZZ") String symbol,
                                           @RequestParam(value="days", defaultValue = "0") double days) {
    return pricesService.getPrices(symbol, days);
  }
}
