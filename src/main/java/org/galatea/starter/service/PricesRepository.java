package org.galatea.starter.service;

import org.galatea.starter.domain.price.PriceHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PricesRepository extends MongoRepository<PriceHistory, String> {

  PriceHistory findByMetadata_Symbol(String symbol);
}
