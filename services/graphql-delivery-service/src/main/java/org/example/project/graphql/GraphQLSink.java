package org.example.project.graphql;

import dev.streamx.quasar.reactive.messaging.Store;
import dev.streamx.quasar.reactive.messaging.annotations.FromChannel;
import dev.streamx.quasar.reactive.messaging.metadata.Action;
import dev.streamx.quasar.reactive.messaging.metadata.EventTime;
import dev.streamx.quasar.reactive.messaging.metadata.Key;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.example.project.model.Product;
import org.jboss.logging.Logger;

@ApplicationScoped
public class GraphQLSink {

  public static final String CHANNEL_PRODUCTS = "products";

  @Inject
  Logger log;

  @Inject
  InMemoryProductRepository productRepository;

  // Store is needed to filter out outdated versions.
  // As productsStore remains unused, we can store only data required for filtering (EventTime).
  // Long type stands for EventTime extracted by EventTimeMessageConverter
  @FromChannel(CHANNEL_PRODUCTS)
  Store<Long> productsStore;

  @Incoming(CHANNEL_PRODUCTS)
  public void consume(Product product, Key key, Action action, EventTime eventTime) {
    if (key == null || action == null || eventTime == null) {
      log.trace("Skipping storing of resource without required metadata");
    } else {
      log.tracef("Storing resource: key %s, action %s, event time %s", key, action, eventTime);
      updateStorage(product, key.getValue(), action);
    }
  }

  private void updateStorage(Product product, String key, Action action) {
    if (Action.PUBLISH.equals(action)) {
      InMemoryProductRepository.Product graphQLProduct = mapProduct(product);

      productRepository.putProduct(key, graphQLProduct);
    } else if (Action.UNPUBLISH.equals(action)) {
      productRepository.removeProduct(key);
    } else {
      log.warnf("Skipping unknown action '%s'...", action.getValue());
    }
  }

  private static InMemoryProductRepository.Product mapProduct(Product product) {
    return new InMemoryProductRepository.Product(
        product.getName(),
        product.getDescription(),
        product.getImageUrl()
    );
  }

}
