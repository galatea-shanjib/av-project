package org.galatea.starter.domain;

import org.galatea.starter.domain.price.PriceHistory;
import org.galatea.starter.domain.price.PriceMetadata;
import org.galatea.starter.domain.rpsy.PricesRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PricesRepositoryTest {
  @Autowired
  private PricesRepository pricesRepository;

  @Test
  public void whenFindByMetadataSymbol_thenReturnPriceHistory() {
    pricesRepository.deleteAll();
    PriceMetadata metadata = new PriceMetadata();
    metadata.setSymbol("test");
    PriceHistory test = new PriceHistory();
    test.setMetadata(metadata);

    pricesRepository.save(test);
    PriceHistory fromDb = pricesRepository.findByMetadata_Symbol("test");
    Assert.assertEquals("test", fromDb.getMetadata().getSymbol());
  }

}
