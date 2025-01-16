package com.example.project.relay;

import dev.streamx.quasar.reactive.messaging.metadata.Action;
import dev.streamx.quasar.reactive.messaging.metadata.EventTime;
import dev.streamx.quasar.reactive.messaging.metadata.Key;
import io.smallrye.reactive.messaging.GenericPayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.example.project.model.Product;
import org.jboss.logging.Logger;

@ApplicationScoped
public class Relay {

  public static final String CHANNEL_PRODUCTS = "products";
  public static final String CHANNEL_RELAYED_PRODUCTS = "relayed-products";

  @Inject
  Logger log;

  @Incoming(CHANNEL_PRODUCTS)
  @Outgoing(CHANNEL_RELAYED_PRODUCTS)
  public GenericPayload<Product> process(Product product, Key key, Action action,
      EventTime eventTime) {
    log.tracef("Relaying: key %s, action %s, event time %s", key, action, eventTime);
    // metadata from incoming message are included by default
    return GenericPayload.of(product);
  }
}
