package com.example.project.relay;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;
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
import org.example.project.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class RelayTest {

  @Inject
  @Any
  InMemoryConnector connector;

  private InMemorySource<Message<Product>> source;
  private InMemorySink<Product> sink;

  @BeforeEach
  void setUp() {
    source = connector.source(Relay.MESSAGES_CHANNEL);
    sink = connector.sink(Relay.RELAYED_MESSAGE_CHANNEL);
  }

  @Test
  void shouldRelayProductUnchanged() {
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
    Message<Product> message = sink.received().get(0);
    assertKey(message, "key");
    assertEventTime(message, 1L);
    assertAction(message, Action.PUBLISH);
    assertPayload(message, data);
    sink.clear();
  }

  private static void assertKey(Message<Product> message, String expected) {
    assertThat(message.getMetadata(Key.class)
        .map(Key::getValue)
        .orElse(null))
        .isEqualTo(expected);
  }

  private static void assertEventTime(Message<Product> message, long expected) {
    assertThat(message.getMetadata(EventTime.class)
        .map(EventTime::getValue)
        .orElse(null))
        .isEqualTo(expected);
  }

  private static void assertAction(Message<Product> message, Action action) {
    assertThat(message.getMetadata(Action.class)
        .orElse(null))
        .isEqualTo(action);
  }

  private static void assertPayload(Message<Product> message, Product content) {
    assertThat(message.getPayload())
        .returns(content.getName(), from(Product::getName))
        .returns(content.getDescription(), from(Product::getDescription))
        .returns(content.getImageUrl(), from(Product::getImageUrl));
  }
}
