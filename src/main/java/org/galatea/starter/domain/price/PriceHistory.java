package org.galatea.starter.domain.price;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.galatea.starter.utils.deserializers.MetadataDeserializer;
import org.galatea.starter.utils.deserializers.PriceDailyDeserializer;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PriceHistory {

  private String id;
  private String message;
  private String debugMessage;
  private PriceMetadata metadata;
  private List<PriceDaily> dailyPrices;

  // Constructor using custom deserializers
  public PriceHistory(JsonObject json) {
    Type pricesListType = new TypeToken<List<PriceDaily>>(){}.getType();
    Gson gson = new GsonBuilder()
        .registerTypeAdapter(PriceMetadata.class, new MetadataDeserializer())
        .registerTypeAdapter(pricesListType, new PriceDailyDeserializer())
        .create();

    metadata = gson.fromJson(json, PriceMetadata.class);
    dailyPrices = gson.fromJson(json, pricesListType);
  }

  public boolean isNull() {
    return metadata == null || dailyPrices == null;
  }

  public void keepRelevantData(double days) {
    dailyPrices = dailyPrices.subList(0, (int)days);
  }
}
