package org.example.project.graphql;

import dev.streamx.quasar.reactive.messaging.utils.MetadataUtils;
import io.smallrye.reactive.messaging.MessageConverter;
import jakarta.enterprise.context.ApplicationScoped;
import java.lang.reflect.Type;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.example.project.model.Product;

@ApplicationScoped
public class ProductMessageConverter implements MessageConverter {

  @Override
  public boolean canConvert(Message<?> message, Type type) {
    return type.equals(StoreProduct.class)
        && message.getPayload() instanceof Product;
  }

  @Override
  public Message<?> convert(Message<?> message, Type type) {
    Product product = (Product) message.getPayload();
    String key = MetadataUtils.extractKey(message);

    return message.withPayload(mapProduct(key, product));
  }

  private static StoreProduct mapProduct(String key, Product product) {
    return new StoreProduct(
        key,
        product.getName(),
        product.getDescription(),
        product.getImageUrl()
    );
  }
}
