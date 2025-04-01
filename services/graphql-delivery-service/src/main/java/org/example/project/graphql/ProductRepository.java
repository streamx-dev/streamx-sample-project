package org.example.project.graphql;

import static java.util.Objects.nonNull;

import dev.streamx.quasar.reactive.messaging.Store;
import dev.streamx.quasar.reactive.messaging.Store.Entry;
import dev.streamx.quasar.reactive.messaging.annotations.FromChannel;
import dev.streamx.quasar.reactive.messaging.metadata.Action;
import dev.streamx.quasar.reactive.messaging.metadata.EventTime;
import dev.streamx.quasar.reactive.messaging.metadata.Key;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Comparator;
import java.util.List;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.example.project.model.Product;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ProductRepository {

  public static final String CHANNEL_PRODUCTS = "products";

  // Store below contains messages converted to StoreProduct using ProductMessageConverter
  @FromChannel(CHANNEL_PRODUCTS)
  Store<StoreProduct> store;

  @Inject
  Logger log;

  // Method consuming messages from channel is required to use Store of incoming messages
  @Incoming(CHANNEL_PRODUCTS)
  public void consume(Product product, Key key, Action action, EventTime eventTime) {
    log.tracef("Storing resource: key %s, action %s, event time %s", key, action, eventTime);
  }

  public List<StoreProduct> getAllProducts() {
    return store.entries()
        .filter(entry -> nonNull(entry.value()))
        .sorted(Comparator.comparing(Entry::key))
        .map(Entry::value)
        .toList();
  }

  public StoreProduct getProduct(String key) {
    return store.get(key);
  }
}
