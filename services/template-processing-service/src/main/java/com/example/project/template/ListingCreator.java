package com.example.project.template;

import static dev.streamx.quasar.reactive.messaging.metadata.Action.PUBLISH;
import static dev.streamx.quasar.reactive.messaging.metadata.Action.UNPUBLISH;

import dev.streamx.quasar.reactive.messaging.Store;
import dev.streamx.quasar.reactive.messaging.Store.Entry;
import dev.streamx.quasar.reactive.messaging.annotations.FromChannel;
import dev.streamx.quasar.reactive.messaging.metadata.EventTime;
import dev.streamx.quasar.reactive.messaging.metadata.Key;
import dev.streamx.quasar.reactive.messaging.utils.MetadataUtils;
import io.smallrye.reactive.messaging.GenericPayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Comparator;
import java.util.List;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;
import org.example.project.model.Page;
import org.example.project.model.Product;
import org.jboss.logging.Logger;

@ApplicationScoped
class ListingCreator {

  private static final String RESULT_KEY = "products.html";

  private static final String FOREACH_START_TOKEN = "${foreach $products as $product}";
  private static final String FOREACH_END_TOKEN = "${/foreach}";
  private static final String TEMPLATE_NAME = "listing";

  @FromChannel(TemplateProcessor.CHANNEL_PRODUCTS)
  Store<Product> store;

  @Inject
  Logger log;

  @Inject
  TemplateRepository repository;

  Message<Page> createListingPage(EventTime eventTime) {
    List<Entry<GenericPayload<Product>>> entries = fetchStoreEntries();

    if (entries.isEmpty()) {
      log.tracef("No products in store to create listing from. Unpublishing listing page '%s'.",
          RESULT_KEY);

      return Message.of(null, Metadata.of(UNPUBLISH, eventTime, Key.of(RESULT_KEY)));
    } else {
      log.tracef("No products to create listing for. Unpublishing listing page '%s'.",
          RESULT_KEY);

      String template = repository.getTemplate(TEMPLATE_NAME);
      Replacement replacement = extractBetween(template, FOREACH_START_TOKEN, FOREACH_END_TOKEN);
      if (replacement == null) {
        log.warnf("Listing template page has no '%s' or '%s'. Skipping processing listing page '%s'.",
            FOREACH_START_TOKEN, FOREACH_END_TOKEN, RESULT_KEY);
        return null;
      }

      List<String> fragments = convertForEachFragments(entries, replacement);
      String result = createResult(template, replacement, fragments);

      return Message.of(new Page(result), Metadata.of(PUBLISH, eventTime, Key.of(RESULT_KEY)));
    }
  }

  private List<Entry<GenericPayload<Product>>> fetchStoreEntries() {
    return store.entriesWithMetadata()
        .filter(message -> PUBLISH.equals(MetadataUtils.extractAction(message.value())))
        .sorted(Comparator.comparing(Entry::key))
        .toList();
  }

  private static Replacement extractBetween(String text, String start, String end) {
    int startIndex = text.indexOf(start);
    if (startIndex == -1) {
      return null; // Start token not found
    }
    int fragmentStartIndex = startIndex + start.length();

    int fragmentEndIndex = text.indexOf(end, fragmentStartIndex);
    if (fragmentEndIndex == -1) {
      return null; // End token not found
    }
    int endIndex = fragmentEndIndex + end.length();

    String replacementText = text.substring(fragmentStartIndex, fragmentEndIndex);

    return new Replacement(startIndex, endIndex, replacementText);
  }

  private record Replacement(int startIndex, int endIndex, String processedFragment) { }

  private List<String> convertForEachFragments(List<Entry<GenericPayload<Product>>> entries,
      Replacement replacement) {
    return entries.stream()
        .map(entry -> convertFragment(replacement, entry))
        .toList();
  }

  private static String convertFragment(Replacement replacement, Entry<GenericPayload<Product>> entry) {
    Product product = entry.value().getPayload();
    String key = entry.key();

    return replacement.processedFragment()
        .replaceAll("\\$\\{product.key}", key)
        .replaceAll("\\$\\{product.name}", product.getName())
        .replaceAll("\\$\\{product.description}", product.getDescription())
        .replaceAll("\\$\\{product.imageUrl}", product.getImageUrl());
  }

  private static String createResult(String template,
      Replacement replacement, List<String> fragments) {
    StringBuilder sb = new StringBuilder();

    sb.append(template.substring(0, replacement.startIndex()));
    fragments.forEach(sb::append);
    sb.append(template.substring(replacement.endIndex()));

    return sb.toString();
  }
}
