package org.example.project.graphql;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import dev.streamx.quasar.reactive.messaging.metadata.Action;
import dev.streamx.quasar.reactive.messaging.metadata.EventTime;
import dev.streamx.quasar.reactive.messaging.metadata.Key;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySource;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import java.util.List;
import org.assertj.core.groups.Tuple;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;
import org.example.project.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class GraphQLSinkTest {

  public static final String KEY = "key";
  public static final String NAME = "name";
  public static final String DESCRIPTION = "description";
  public static final String IMAGE_URL = "imageUrl";
  public static final String ALL_PRODUCTS_QUERY = """
      {
        "query": "\
           query { \
             allProducts { \
               key \
               name \
               description \
               imageUrl \
             } \
           }\
        "
      }""";
  public static final String GET_PRODUCT_QUERY = """
      {
        "query": "\
           query { \
             getProduct(key:\\"%s\\") {\
               key \
               name \
               description \
               imageUrl \
             }\
           }\
        "
      }""";

  @Inject
  @Any
  InMemoryConnector connector;

  private InMemorySource<Message<Product>> source;

  @Inject
  InMemoryProductRepository productRepository;

  @BeforeEach
  void setUp() {
    productRepository.clear();

    source = connector.source(GraphQLSink.CHANNEL_PRODUCTS);
  }

  @Test
  void shouldFeedRepositoryWithProduct() {
    // given
    Product data = new Product(NAME, DESCRIPTION, IMAGE_URL);

    // when
    publish(KEY, data);
    await().until(() -> !productRepository.getAllProducts().isEmpty());

    // then
    Response response = query(ALL_PRODUCTS_QUERY);

    List<GraphQLProduct> allProducts = response.jsonPath()
        .getList("data.allProducts", GraphQLProduct.class);

    assertThat(allProducts)
        .isNotEmpty()
        .extracting(GraphQLProduct::key, GraphQLProduct::name, GraphQLProduct::description, GraphQLProduct::imageUrl)
        .contains(Tuple.tuple(KEY, NAME, DESCRIPTION, IMAGE_URL));
  }

  @Test
  void shouldFeedRepositoryWithProductWithSpecificKey() {
    // given
    String specificKey = "specificKey";
    Product data = new Product(NAME, DESCRIPTION, IMAGE_URL);

    // when
    publish(specificKey, data);
    await().until(() -> productRepository.getProduct(specificKey) != null);

    // then
    Response response = query(GET_PRODUCT_QUERY.formatted(specificKey));

    GraphQLProduct product = response.jsonPath()
        .getObject("data.getProduct", GraphQLProduct.class);

    assertThat(product)
        .isNotNull()
        .extracting(GraphQLProduct::key, GraphQLProduct::name, GraphQLProduct::description, GraphQLProduct::imageUrl)
        .contains(specificKey, NAME, DESCRIPTION, IMAGE_URL);
  }

  @Test
  void shouldRemoveProductFromRepository() {
    // given
    String specificKey = "specificKey";
    Product data = new Product(NAME, DESCRIPTION, IMAGE_URL);
    publish(specificKey, data);
    await().until(() -> productRepository.getProduct(specificKey) != null);

    // when
    unpublish(specificKey);
    await().until(() -> productRepository.getProduct(specificKey) == null);

    // then
    Response response = query(GET_PRODUCT_QUERY.formatted(specificKey));

    GraphQLProduct product = response.jsonPath()
        .getObject("data.getProduct", GraphQLProduct.class);

    assertThat(product)
        .isNull();
  }

  private void publish(String key, Product data) {
    send(key, data, Action.PUBLISH);
  }

  private void unpublish(String key) {
    send(key, null, Action.UNPUBLISH);
  }

  private void send(String key, Product data, Action publish) {
    source.send(Message.of(data, Metadata.of(
        Key.of(key),
        EventTime.of(1L),
        publish)));
  }

  private static Response query(String graphQLQuery) {
    final Response response = given()
        .contentType(ContentType.JSON)
        .body(graphQLQuery)
        .when()
        .post("/graphql")
        .then()
        .assertThat()
        .statusCode(200)
        .and()
        .extract()
        .response();
    return response;
  }

  record GraphQLProduct(String key, String name, String description, String imageUrl) { }

}
