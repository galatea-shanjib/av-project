package org.galatea.starter.utils.deserializers;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import java.lang.reflect.Type;
import org.galatea.starter.domain.price.PriceMetadata;

public class MetadataDeserializer implements JsonDeserializer<PriceMetadata> {
  @Override
  public PriceMetadata deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) {
    JsonElement meta = je.getAsJsonObject().get("Meta Data");
    return new Gson().fromJson(meta, PriceMetadata.class);
  }
}
