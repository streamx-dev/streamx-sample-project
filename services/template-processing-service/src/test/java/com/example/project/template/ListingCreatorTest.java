package com.example.project.template;

import static com.example.project.template.TestUtils.assertAction;
import static com.example.project.template.TestUtils.assertKey;
import static com.example.project.template.TestUtils.assertPayload;
import static dev.streamx.quasar.reactive.messaging.metadata.Action.PUBLISH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import dev.streamx.quasar.reactive.messaging.Store;
import dev.streamx.quasar.reactive.messaging.Store.Entry;
import dev.streamx.quasar.reactive.messaging.metadata.Action;
import dev.streamx.quasar.reactive.messaging.metadata.EventTime;
import dev.streamx.quasar.reactive.messaging.metadata.Key;
import dev.streamx.quasar.reactive.messaging.utils.MetadataUtils;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.GenericPayload;
import jakarta.inject.Inject;
import java.util.Arrays;
import java.util.Comparator;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;
import org.example.project.model.Page;
import org.example.project.model.Product;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ListingCreatorTest {

  @InjectMock
  ProductRepository productRepository;

  @Inject
  ListingCreator cut;

  @Test
  void shouldReturnEmptyListing() {
    // when
    Message<Page> result = cut.createListingPage(EventTime.of(1L));

    // then
    assertThat(result).isNotNull();
    assertAction(result, Action.UNPUBLISH);
  }

  @Test
  void shouldSkipUnpublishedProducts() {
    // given
    addToStore(new StoreContent("key", null,
        Metadata.of(EventTime.of(1L), Action.UNPUBLISH)
    ));

    // when
    Message<Page> result = cut.createListingPage(EventTime.of(1L));

    // then
    assertThat(result).isNotNull();
    assertAction(result, Action.UNPUBLISH);
  }

  @Test
  void shouldCreateListing() {
    // given
    addToStore(new StoreContent(
        "key",
        new Product("name", "description", "imageUrl"),
        Metadata.of(EventTime.of(1L), Action.PUBLISH)
    ), new StoreContent(
        "otherKey",
        new Product("otherName", "otherDescription", "otherImageUrl"),
        Metadata.of(EventTime.of(2L), Action.PUBLISH)
    ));

    // when
    Message<Page> result = cut.createListingPage(EventTime.of(2L));

    // then
    assertPayload(result, """
          - Product:
            - Key: key
            - Name: name
            - ImageUrl: imageUrl
            - Description: description
          - Product:
            - Key: otherKey
            - Name: otherName
            - ImageUrl: otherImageUrl
            - Description: otherDescription
        """);
    assertKey(result, "products.html");
  }

  private void addToStore(StoreContent... storeContents) {
    when(productRepository.fetchStoreEntries())
        .then((args) -> Arrays.stream(storeContents)
            .map(storeContent -> new Store.Entry<>(storeContent.key, GenericPayload.of(
                storeContent.payload,
                storeContent.metadata.with(Key.of(storeContent.key))
            )))
            .filter(message -> PUBLISH.equals(MetadataUtils.extractAction(message.value())))
            .sorted(Comparator.comparing(Entry::key))
            .toList()
        );
  }

  record StoreContent(String key, Product payload, Metadata metadata) { }
}
