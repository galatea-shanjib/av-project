package org.galatea.starter.domain.price;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PriceMetadata {

  @SerializedName("1. Information")
  public String information;

  @SerializedName("2. Symbol")
  public String symbol;

  @SerializedName("3. Last Refreshed")
  public String lastRefreshed;

  @SerializedName("4. Output Size")
  public String outputSize;

  @SerializedName("5. Time Zone")
  public String timeZone;
}
