package com.example.project.template;

import static dev.streamx.quasar.reactive.messaging.metadata.Action.PUBLISH;

import dev.streamx.quasar.reactive.messaging.Store;
import dev.streamx.quasar.reactive.messaging.Store.Entry;
import dev.streamx.quasar.reactive.messaging.annotations.FromChannel;
import dev.streamx.quasar.reactive.messaging.utils.MetadataUtils;
import io.smallrye.reactive.messaging.GenericPayload;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Comparator;
import java.util.List;
import org.example.project.model.Product;

@ApplicationScoped
public class ProductRepository {

  @FromChannel(TemplateProcessor.CHANNEL_PRODUCTS)
  Store<Product> store;

  List<Entry<GenericPayload<Product>>> fetchStoreEntries() {
    return store.entriesWithMetadata()
        .filter(message -> PUBLISH.equals(MetadataUtils.extractAction(message.value())))
        .sorted(Comparator.comparing(Entry::key))
        .toList();
  }
}
