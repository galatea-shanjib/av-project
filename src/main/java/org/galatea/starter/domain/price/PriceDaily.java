package org.galatea.starter.domain.price;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PriceDaily {

  public String date;

  @SerializedName("1. open")
  public String open;

  @SerializedName("2. high")
  public String high;

  @SerializedName("3. low")
  public String low;

  @SerializedName("4. close")
  public String close;

  @SerializedName("5. volume")
  public String volume;
}
