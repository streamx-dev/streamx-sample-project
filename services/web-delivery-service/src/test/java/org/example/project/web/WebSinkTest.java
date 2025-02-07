package org.example.project.web;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.example.project.web.WebSink.CHANNEL_PAGES;
import static org.hamcrest.core.StringContains.containsString;

import dev.streamx.quasar.reactive.messaging.metadata.Action;
import dev.streamx.quasar.reactive.messaging.metadata.EventTime;
import dev.streamx.quasar.reactive.messaging.metadata.Key;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySource;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;
import org.example.project.model.Page;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@QuarkusTest
class WebSinkTest {

  private static final String TEST_CONTENT = "test content for %s";
  private static final AtomicLong EVENT_TIME = new AtomicLong(1);

  @Inject
  @Any
  InMemoryConnector connector;

  @ParameterizedTest
  @ValueSource(strings = {
      "test.html",
      "/content/test2.html",
      "/first/second/test3.html"
  })
  void shouldAccessPublishedPage(String key) {
    // when
    String publishedContent = publish(key, CHANNEL_PAGES, Page::new);

    // then
    await().until(() -> canAccessViaHttp(key, publishedContent));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "unpublishedTest.html",
      "/content/unpublishedTest2.html",
      "/first/second/unpublishedTest3.html"
  })
  void shouldNotAccessUnpublishedPage(String key) {
    // given
    String publishedContent = publish(key, CHANNEL_PAGES, Page::new);
    await().until(() -> canAccessViaHttp(key, publishedContent));

    // when
    unpublish(key, CHANNEL_PAGES);

    // then
    await().until(() -> cannotAccessViaHttp(key));
  }

  @Test
  void shouldNotUnpublishWholeDirectory() {
    // given
    String file = "/directory/file.html";
    String directory = "/directory";

    String publishedContent = publish(file, CHANNEL_PAGES, Page::new);
    await().until(() -> canAccessViaHttp(file, publishedContent));

    // when
    unpublish(directory, CHANNEL_PAGES);
    makeSureUnpublishedProcessed();

    // then
    await().until(() -> canAccessViaHttp(file, publishedContent));
  }

  // publication of other file assures all previous publications/unpublications has been procesed
  private void makeSureUnpublishedProcessed() {
    String synchronisationFile = "/sync.txt";
    String syncContent = publish(synchronisationFile, CHANNEL_PAGES, Page::new);
    await().until(() -> canAccessViaHttp(synchronisationFile, syncContent));
  }

  private <T> String publish(String key, String channel, Function<String, T> createPayloadFn) {
    String content = TEST_CONTENT.formatted(key);
    T payload = createPayloadFn.apply(content);
    publish(key, channel, payload);
    return content;
  }

  private <T> void publish(String key, String channel, T payload) {
    sendMessage(key, channel, payload, Action.PUBLISH);
  }

  private void unpublish(String key, String channel) {
    sendMessage(key, channel, null, Action.UNPUBLISH);
  }

  private <T> void sendMessage(String key, String channel, T payload, Action action) {
    InMemorySource<Message<T>> pages = connector.source(channel);

    pages.send(Message.of(payload, Metadata.of(
        Key.of(key),
        EventTime.of(EVENT_TIME.getAndIncrement()),
        action)));
  }

  private boolean canAccessViaHttp(String path, String content) {
    try {
      given().basePath("/")
          .when()
          .get(path)
          .then()
          .statusCode(200)
          .body(containsString(content));
    } catch (AssertionError err) {
      return false;
    }
    return true;
  }

  private boolean cannotAccessViaHttp(String path) {
    try {
      given().basePath("/")
          .when()
          .get(path)
          .then()
          .statusCode(404);
    } catch (AssertionError err) {
      return false;
    }
    return true;
  }
}
