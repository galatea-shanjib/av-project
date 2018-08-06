package org.galatea.starter.domain.price;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.galatea.starter.utils.MetadataDeserializer;
import org.galatea.starter.utils.PriceDailyDeserializer;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PriceHistory {

  public PriceMetadata metadata;
  public List<PriceDaily> dailyPrices;

  public PriceHistory(String json) {
    Type listType = new TypeToken<List<PriceDaily>>(){}.getType();
    Gson metaGson = new GsonBuilder()
        .registerTypeAdapter(PriceMetadata.class, new MetadataDeserializer())
        .create();
    Gson priceGson = new GsonBuilder()
        .registerTypeAdapter(listType, new PriceDailyDeserializer())
        .create();

    metadata = metaGson.fromJson(json, PriceMetadata.class);
    dailyPrices = priceGson.fromJson(json, listType);
  }
}
