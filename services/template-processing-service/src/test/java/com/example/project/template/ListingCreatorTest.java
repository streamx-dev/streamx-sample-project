package com.example.project.template;

import static com.example.project.template.TestUtils.assertAction;
import static com.example.project.template.TestUtils.assertKey;
import static com.example.project.template.TestUtils.assertPayload;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import dev.streamx.quasar.reactive.messaging.Store;
import dev.streamx.quasar.reactive.messaging.metadata.Action;
import dev.streamx.quasar.reactive.messaging.metadata.EventTime;
import dev.streamx.quasar.reactive.messaging.metadata.Key;
import io.quarkus.arc.ClientProxy;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.GenericPayload;
import jakarta.inject.Inject;
import java.util.Arrays;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;
import org.example.project.model.Page;
import org.example.project.model.Product;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

@QuarkusTest
// To replace Store in tested class we need to store original field and restore it after all tests
// @TestInstance allows us to execute @BeforeAll in nonstatic method and access field of test class
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ListingCreatorTest {

  private static final String TEST_TEMPLATE = """
      ${foreach $products as $product}  - Product:
          - Key: ${product.key}
          - Name: ${product.name}
          - ImageUrl: ${product.imageUrl}
          - Description: ${product.description}
      ${/foreach}""";

  @InjectMock
  TemplateRepository repository;

  Store<Product> originalStore;
  Store<Product> store;

  @Inject
  ListingCreator cut;

  @BeforeAll
  void setUpAll() {
    originalStore = ClientProxy.unwrap(cut).store;
  }

  @AfterAll
  void tearDownAll() {
    ClientProxy.unwrap(cut).store = originalStore;
  }

  @BeforeEach
  void setUp() {
    setUpStoreMock();
  }

  @Test
  void shouldSkipListingForMissingForeachTokens() {
    // given
    when(repository.getTemplate("listing")).thenReturn("");

    addToStore(new StoreContent("key", new Product("name", "description", "imageUrl"),
        Metadata.of(EventTime.of(1L), Action.PUBLISH)
    ));

    // when
    Message<Page> result = cut.createListingPage(EventTime.of(1L));

    // then
    assertThat(result).isNull();
  }

  @Test
  void shouldReturnEmptyListing() {
    // given
    when(repository.getTemplate("listing")).thenReturn(TEST_TEMPLATE);

    // when
    Message<Page> result = cut.createListingPage(EventTime.of(1L));

    // then
    assertThat(result).isNotNull();
    assertAction(result, Action.UNPUBLISH);
  }

  @Test
  void shouldSkipUnpublishedProducts() {
    // given
    when(repository.getTemplate("listing")).thenReturn(TEST_TEMPLATE);
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
    when(repository.getTemplate("listing")).thenReturn(TEST_TEMPLATE);
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
    when(store.entriesWithMetadata())
        .then((args) -> Arrays.stream(storeContents)
            .map(storeContent -> new Store.Entry<>(storeContent.key, GenericPayload.of(
                storeContent.payload,
                storeContent.metadata.with(Key.of(storeContent.key))
            ))));
  }

  record StoreContent(String key, Product payload, Metadata metadata) { }

  private void setUpStoreMock() {
    store = Mockito.mock();

    // Quarkus heavily uses proxing, injected field are also proxies.
    // So to explicitly replace field of tested bean we need to unwrap this class
    ClientProxy.unwrap(cut).store = store;
  }
}
