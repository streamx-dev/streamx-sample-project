package com.example.project.template;

import static org.assertj.core.api.Assertions.assertThat;

import dev.streamx.quasar.reactive.messaging.metadata.Action;
import dev.streamx.quasar.reactive.messaging.metadata.EventTime;
import dev.streamx.quasar.reactive.messaging.metadata.Key;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.example.project.model.Page;

public final class TestUtils {
  private TestUtils() {}

  public static <T> void assertKey(Message<T> message, String expected) {
    assertThat(message.getMetadata(Key.class)
        .map(Key::getValue)
        .orElse(null))
        .isEqualTo(expected);
  }

  public static <T> void assertEventTime(Message<T> message, long expected) {
    assertThat(message.getMetadata(EventTime.class)
        .map(EventTime::getValue)
        .orElse(null))
        .isEqualTo(expected);
  }

  public static <T> void assertAction(Message<T> message, Action action) {
    assertThat(message.getMetadata(Action.class)
        .orElse(null))
        .isEqualTo(action);
  }

  public static void assertPayload(Message<Page> message, String content) {
    if (content == null) {
      assertThat(message.getPayload()).isNull();
    } else {
      assertThat(message.getPayload().getContentAsString())
          .isEqualTo(content);
    }
  }

}
