package com.example.project.jsonconverter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import dev.streamx.quasar.reactive.messaging.metadata.Action;
import dev.streamx.quasar.reactive.messaging.metadata.EventTime;
import dev.streamx.quasar.reactive.messaging.metadata.Key;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;
import io.smallrye.reactive.messaging.memory.InMemorySource;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;
import org.example.project.model.Json;
import org.example.project.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class JsonConverterTest {

  @Inject
  @Any
  InMemoryConnector connector;

  private InMemorySource<Message<Product>> source;
  private InMemorySink<Json> sink;

  @BeforeEach
  void setUp() {
    source = connector.source(JsonConverter.CHANNEL_PRODUCTS);
    sink = connector.sink(JsonConverter.CHANNEL_JSONS);
  }

  @Test
  void shouldConvertProduct() {
    // given
    Product data = new Product("name", "description", "imageUrl");

    // when
    source.send(Message.of(data, Metadata.of(
        Key.of("key"),
        EventTime.of(1L),
        Action.PUBLISH)));
    await().until(() -> !sink.received().isEmpty());

    // then
    assertThat(sink.received()).hasSize(1);
    Message<Json> message = sink.received().get(0);
    assertKey(message, "jsons/key.json");
    assertEventTime(message, 1L);
    assertAction(message, Action.PUBLISH);
    assertPayload(message, """
        {\
        "name":"name",\
        "description":"description",\
        "imageUrl":"imageUrl"\
        }""");
    sink.clear();
  }

  @Test
  void shouldUnpublishJson() {
    // when
    source.send(Message.of(null, Metadata.of(
        Key.of("key"),
        EventTime.of(1L),
        Action.UNPUBLISH)));
    await().until(() -> !sink.received().isEmpty());

    // then
    assertThat(sink.received()).hasSize(1);
    Message<Json> message = sink.received().get(0);
    assertKey(message, "jsons/key.json");
    assertEventTime(message, 1L);
    assertAction(message, Action.UNPUBLISH);
    assertPayload(message, null);
    sink.clear();
  }

  private static void assertKey(Message<?> message, String expected) {
    assertThat(message.getMetadata(Key.class)
        .map(Key::getValue)
        .orElse(null))
        .isEqualTo(expected);
  }

  private static void assertEventTime(Message<?> message, long expected) {
    assertThat(message.getMetadata(EventTime.class)
        .map(EventTime::getValue)
        .orElse(null))
        .isEqualTo(expected);
  }

  private static void assertAction(Message<?> message, Action action) {
    assertThat(message.getMetadata(Action.class)
        .orElse(null))
        .isEqualTo(action);
  }

  private static void assertPayload(Message<Json> message, String content) {
    if (content == null) {
      assertThat(message.getPayload()).isNull();
    } else {
      assertThat(message.getPayload()).isNotNull();
      assertThat(message.getPayload().getContentAsString()).isEqualTo(content);
    }
  }
}
