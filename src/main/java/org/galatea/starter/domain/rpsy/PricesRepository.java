package org.galatea.starter.domain.rpsy;

import org.galatea.starter.domain.price.PriceHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PricesRepository extends MongoRepository<PriceHistory, String> {
  PriceHistory findByMetadata_Symbol(String symbol);
}
