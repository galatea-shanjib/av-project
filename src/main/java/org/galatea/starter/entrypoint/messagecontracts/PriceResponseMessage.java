package org.galatea.starter.entrypoint.messagecontracts;

import lombok.Data;
import org.galatea.starter.domain.price.PriceHistory;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PriceResponseMessage {

  private PriceHistory history;

  // TODO: audit info stuff
}
