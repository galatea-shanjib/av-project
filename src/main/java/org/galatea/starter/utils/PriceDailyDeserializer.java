package org.galatea.starter.utils;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.galatea.starter.domain.price.PriceDaily;

public class PriceDailyDeserializer implements JsonDeserializer<List<PriceDaily>> {

  @Override
  public List<PriceDaily> deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) {
    List<PriceDaily> list = new ArrayList<>();
    JsonObject price = je.getAsJsonObject().get("Time Series (Daily)").getAsJsonObject();

    for (Map.Entry<String, JsonElement> entry : price.entrySet()) {
      PriceDaily daily = new Gson().fromJson(entry.getValue(), PriceDaily.class);
      daily.setDate(entry.getKey());
      list.add(daily);
    }
    return list;
  }
}
