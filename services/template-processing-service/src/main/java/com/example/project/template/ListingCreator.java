package com.example.project.template;

import static com.example.project.template.TemplateConfig.LISTING_TEMPLATE_NAME;
import static dev.streamx.quasar.reactive.messaging.metadata.Action.PUBLISH;
import static dev.streamx.quasar.reactive.messaging.metadata.Action.UNPUBLISH;

import com.github.mustachejava.Mustache;
import dev.streamx.quasar.reactive.messaging.Store;
import dev.streamx.quasar.reactive.messaging.Store.Entry;
import dev.streamx.quasar.reactive.messaging.annotations.FromChannel;
import dev.streamx.quasar.reactive.messaging.metadata.EventTime;
import dev.streamx.quasar.reactive.messaging.metadata.Key;
import dev.streamx.quasar.reactive.messaging.utils.MetadataUtils;
import io.smallrye.reactive.messaging.GenericPayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;
import org.example.project.model.Page;
import org.example.project.model.Product;
import org.jboss.logging.Logger;

@ApplicationScoped
class ListingCreator {

  private static final String RESULT_KEY = "products.html";

  @Inject
  Logger log;

  @Inject
  ProductRepository productRepository;

  @Inject
  @Named(LISTING_TEMPLATE_NAME)
  Mustache mustache;

  Message<Page> createListingPage(EventTime eventTime) {
    List<Entry<GenericPayload<Product>>> entries = productRepository.fetchStoreEntries();

    if (entries.isEmpty()) {
      log.tracef("No products in store to create listing from. Unpublishing listing page '%s'.",
          RESULT_KEY);

      return Message.of(null, Metadata.of(UNPUBLISH, eventTime, Key.of(RESULT_KEY)));
    } else {
      try {
        log.tracef("Preparing listing page '%s'.", RESULT_KEY);
        String result = processTemplate(entries);

        log.tracef("Publishing listing page '%s'.", RESULT_KEY);
        return Message.of(new Page(result), Metadata.of(PUBLISH, eventTime, Key.of(RESULT_KEY)));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private String processTemplate(List<Entry<GenericPayload<Product>>> entries) throws IOException {
    StringWriter writer = new StringWriter();
    Map<String, Object> context = prepareModel(entries);

    mustache.execute(writer, context).flush();
    return writer.toString();
  }

  private static Map<String, Object> prepareModel(
      List<Entry<GenericPayload<Product>>> entries) {
    List<ProductModel> productModels = entries.stream()
        .map(entry -> {
          String key = entry.key();
          Product product = entry.value().getPayload();

          return new ProductModel(key,
              product.getName(), product.getDescription(), product.getImageUrl());
        })
        .toList();
    Map<String, Object> context = new HashMap<>();
    context.put("products", productModels);
    return context;
  }
}
