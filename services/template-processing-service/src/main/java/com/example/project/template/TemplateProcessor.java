package com.example.project.template;

import dev.streamx.quasar.reactive.messaging.metadata.Action;
import dev.streamx.quasar.reactive.messaging.metadata.EventTime;
import dev.streamx.quasar.reactive.messaging.metadata.Key;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.example.project.model.Product;
import org.example.project.model.Page;
import org.jboss.logging.Logger;

@ApplicationScoped
public class TemplateProcessor {

  public static final String CHANNEL_PRODUCTS = "products";
  public static final String CHANNEL_PAGES = "pages";

  @Inject
  Logger log;

  @Inject
  PageCreator pageCreator;

  @Inject
  ListingCreator listingCreator;

  @Incoming(CHANNEL_PRODUCTS)
  @Outgoing(CHANNEL_PAGES)
  @Acknowledgment(Acknowledgment.Strategy.POST_PROCESSING)
  public Multi<Message<Page>> process(Message<Product> message) {
    Product product = message.getPayload();
    Metadata metadata = message.getMetadata();
    Key key = getMetadataField(metadata, Key.class);
    Action action = getMetadataField(metadata, Action.class);
    EventTime eventTime = getMetadataField(metadata, EventTime.class);

    log.tracef("Processing product: key %s, action %s, event time %s",
        key, action, eventTime);

    Message<Page> productPage = pageCreator.createProductPage(key, product, action, eventTime);
    // listing is regenerated for every product update to avoid complicated business logic
    Message<Page> listingPage = listingCreator.createListingPage(eventTime);

    return Multi.createFrom().items(
        productPage,
        listingPage
    );
  }

  private static <T> T getMetadataField(Metadata metadata, Class<T> metadataFieldClass) {
    return metadata.get(metadataFieldClass).orElse(null);
  }
}
