package com.example.project.template;

import static com.example.project.template.TestUtils.assertAction;
import static com.example.project.template.TestUtils.assertEventTime;
import static com.example.project.template.TestUtils.assertKey;
import static com.example.project.template.TestUtils.assertPayload;

import dev.streamx.quasar.reactive.messaging.metadata.Action;
import dev.streamx.quasar.reactive.messaging.metadata.EventTime;
import dev.streamx.quasar.reactive.messaging.metadata.Key;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.example.project.model.Page;
import org.example.project.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@QuarkusTest
class PageCreatorTest {

  private static final String TEST_KEY = "testKey";

  @Inject
  PageCreator cut;

  @Test
  void shouldUnpublishPageForUnpublishedProducts() {
    // when
    Message<Page> message = cut.createProductPage(Key.of(TEST_KEY),
        null,
        Action.UNPUBLISH,
        EventTime.of(1L));

    // then
    Assertions.assertThat(message).isNotNull();

    assertKey(message, "products/%s.html".formatted(TEST_KEY));
    assertEventTime(message, 1L);
    assertAction(message, Action.UNPUBLISH);
    assertPayload(message, null);
  }

  @ParameterizedTest
  @MethodSource(value = "source")
  void shouldReplaceKey(String name, String description, String imageUrl,
      String result) {
    // when
    Message<Page> message = cut.createProductPage(Key.of(TEST_KEY),
        new Product(name, description, imageUrl),
        Action.PUBLISH,
        EventTime.of(1L));

    // then
    Assertions.assertThat(message).isNotNull();

    assertKey(message, "products/%s.html".formatted(TEST_KEY));
    assertEventTime(message, 1L);
    assertAction(message, Action.PUBLISH);
    assertPayload(message, result);
  }

  static Stream<Arguments> source() {
    return Stream.of(
        Arguments.of(
            null, null, null, """
                - Key: testKey
                - Name:\s
                - ImageUrl:\s
                - Description:\s
                """
        ),
        Arguments.of(
            "name", null, null,  """
                - Key: testKey
                - Name: name
                - ImageUrl:\s
                - Description:\s
                """
        ),
        Arguments.of(
            null, "description", null,  """
                - Key: testKey
                - Name:\s
                - ImageUrl:\s
                - Description: description
                """
        ),
        Arguments.of(
            null, null, "imageUrl",  """
                - Key: testKey
                - Name:\s
                - ImageUrl: imageUrl
                - Description:\s
                """
        )
    );
  }
}
