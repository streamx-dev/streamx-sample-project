package com.example.project.template;

import static com.example.project.template.TestUtils.assertAction;
import static com.example.project.template.TestUtils.assertEventTime;
import static com.example.project.template.TestUtils.assertKey;
import static com.example.project.template.TestUtils.assertPayload;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import dev.streamx.quasar.reactive.messaging.metadata.Action;
import dev.streamx.quasar.reactive.messaging.metadata.EventTime;
import dev.streamx.quasar.reactive.messaging.metadata.Key;
import dev.streamx.quasar.reactive.messaging.utils.MetadataUtils;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;
import io.smallrye.reactive.messaging.memory.InMemorySource;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import java.util.stream.Stream;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;
import org.example.project.model.Page;
import org.example.project.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class TemplateProcessorTest {

  private static final String PRODUCT_PAGE = "products/key.html";
  private static final String LISTING_PAGE = "products.html";
  private static final long FIRST_MESSAGE_EVENT_TIME = 1L;
  private static final long LAST_MESSAGE_EVENT_TIME = 2L;

  @Inject
  @Any
  InMemoryConnector connector;

  private InMemorySource<Message<Product>> source;
  private InMemorySink<Page> sink;

  @BeforeEach
  void setUp() {
    source = connector.source(TemplateProcessor.CHANNEL_PRODUCTS);
    sink = connector.sink(TemplateProcessor.CHANNEL_PAGES);
  }

  @Test
  void shouldFillPageTemplateByProduct() {
    // given
    String key = "key";
    Product data = new Product("name", "description", "imageUrl");

    // when
    source.send(Message.of(data, Metadata.of(
        Key.of(key),
        EventTime.of(FIRST_MESSAGE_EVENT_TIME),
        Action.PUBLISH)));
    String resultKey = PRODUCT_PAGE;
    await().until(() -> messagesWithKey(resultKey).count() == 1);

    // then
    assertThat(messagesWithKey(resultKey).count()).isEqualTo(FIRST_MESSAGE_EVENT_TIME);
    Message<Page> message = messagesWithKey(resultKey)
        .findFirst().get();

    assertKey(message, resultKey);
    assertEventTime(message, FIRST_MESSAGE_EVENT_TIME);
    assertAction(message, Action.PUBLISH);
    assertPayload(message, """
        - Key: key
        - Name: name
        - ImageUrl: imageUrl
        - Description: description
        """);
    sink.clear();
  }

  @Test
  void shouldFillListingTemplateByProducts() {
    // given
    String key = "key";
    Product data = new Product("name", "description", "imageUrl");
    String otherKey = "otherKey";
    Product otherData = new Product("otherName", "otherDescription", "otherImageUrl");

    // when
    source.send(Message.of(data, Metadata.of(
        Key.of(key),
        EventTime.of(FIRST_MESSAGE_EVENT_TIME),
        Action.PUBLISH)));
    source.send(Message.of(otherData, Metadata.of(
        Key.of(otherKey),
        EventTime.of(LAST_MESSAGE_EVENT_TIME),
        Action.PUBLISH)));

    String resultKey = LISTING_PAGE;
    await().until(() -> {
      long count = messagesWithKey(resultKey).count();
      return count == 2;
    });

    // then
    assertThat(messagesWithKey(resultKey).count()).isEqualTo(LAST_MESSAGE_EVENT_TIME);
    Message<Page> message = messagesWithKey(resultKey)
        .filter(pageMessage -> MetadataUtils.extractEventTime(pageMessage).equals(
            LAST_MESSAGE_EVENT_TIME))
        .findFirst().get();

    assertKey(message, LISTING_PAGE);
    assertEventTime(message, LAST_MESSAGE_EVENT_TIME);
    assertAction(message, Action.PUBLISH);
    assertPayload(message, """
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
    sink.clear();
  }

  private Stream<? extends Message<Page>> messagesWithKey(String key) {
    return sink.received().stream()
        .filter(e -> MetadataUtils.extractKey(e).equals(key));
  }
}
