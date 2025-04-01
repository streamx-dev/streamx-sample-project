package com.example.project.template;

import static com.example.project.template.TemplateConfig.PAGE_TEMPLATE_NAME;

import com.github.mustachejava.Mustache;
import dev.streamx.quasar.reactive.messaging.metadata.Action;
import dev.streamx.quasar.reactive.messaging.metadata.EventTime;
import dev.streamx.quasar.reactive.messaging.metadata.Key;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;
import org.example.project.model.Page;
import org.example.project.model.Product;
import org.jboss.logging.Logger;

@ApplicationScoped
class PageCreator {

  @Inject
  Logger log;

  @Inject
  @Named(PAGE_TEMPLATE_NAME)
  Mustache mustache;

  Message<Page> createProductPage(Key key, Product product, Action action, EventTime eventTime) {
    String keyValue = key.getValue();
    String newKey = "products/%s.html".formatted(keyValue);

    if (Action.UNPUBLISH.equals(action)) {
      log.tracef("Unpublishing product page '%s' for key %s", newKey, keyValue);

      return Message.of(null, Metadata.of(action, eventTime, Key.of(newKey)));
    } else {
      try {
        log.tracef("Preparing product page '%s' for key %s", newKey, keyValue);
        String result = processTemplate(keyValue, product);
        log.tracef("Publishing product page '%s' for key %s", newKey, keyValue);
        return Message.of(new Page(result), Metadata.of(action, eventTime, Key.of(newKey)));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private String processTemplate(String keyValue, Product product) throws IOException {
    Map<String, Object> context = prepareModel(keyValue, product);

    StringWriter writer = new StringWriter();
    mustache.execute(writer, context).flush();
    return writer.toString();
  }

  private Map<String, Object> prepareModel(String keyValue, Product product) {
    ProductModel productModel = new ProductModel(keyValue,
        product.getName(), product.getDescription(), product.getImageUrl());
    Map<String, Object> context = new HashMap<>();
    context.put("product", productModel);
    return context;
  }
}
