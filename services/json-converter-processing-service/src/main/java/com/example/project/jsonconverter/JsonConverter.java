package com.example.project.jsonconverter;

import static dev.streamx.quasar.reactive.messaging.metadata.Action.PUBLISH;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.streamx.quasar.reactive.messaging.metadata.Action;
import dev.streamx.quasar.reactive.messaging.metadata.EventTime;
import dev.streamx.quasar.reactive.messaging.metadata.Key;
import io.smallrye.reactive.messaging.GenericPayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Metadata;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.example.project.model.Json;
import org.example.project.model.Product;
import org.jboss.logging.Logger;

@ApplicationScoped
public class JsonConverter {

  public static final String CHANNEL_PRODUCTS = "products";
  public static final String CHANNEL_JSONS = "jsons";

  @Inject
  Logger log;

  @Inject
  ObjectMapper objectMapper;

  @Incoming(CHANNEL_PRODUCTS)
  @Outgoing(CHANNEL_JSONS)
  public GenericPayload<Json> process(Product product, Key key, Action action,
      EventTime eventTime) throws JsonProcessingException {
    log.tracef("Converting product to JSON: key %s, action %s, event time %s", key, action, eventTime);

    Json json = null;
    if (PUBLISH.equals(action)) {
      String content = objectMapper.writeValueAsString(product);
      json = new Json(content);
    }
    String newKey = "jsons/%s.json".formatted(key.getValue());

    return GenericPayload.of(json)
        .withMetadata(Metadata.of(Key.of(newKey)));
  }
}
