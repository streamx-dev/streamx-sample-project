package com.example.project.template;

import dev.streamx.quasar.reactive.messaging.metadata.Action;
import dev.streamx.quasar.reactive.messaging.metadata.EventTime;
import dev.streamx.quasar.reactive.messaging.metadata.Key;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;
import org.example.project.model.Page;
import org.example.project.model.Product;
import org.jboss.logging.Logger;

@ApplicationScoped
class PageCreator {

  private static final String TEMPLATE_NAME = "page";

  @Inject
  Logger log;

  @Inject
  TemplateRepository repository;

  Message<Page> createProductPage(Key key, Product product, Action action, EventTime eventTime) {
    String keyValue = key.getValue();
    String newKey = "products/%s.html".formatted(keyValue);

    if (Action.UNPUBLISH.equals(action)) {
      log.tracef("Unpublishing product page '%s' for key %s", newKey, keyValue);

      return Message.of(null, Metadata.of(action, eventTime, Key.of(newKey)));
    } else {
      log.tracef("Publishing product page '%s' for key %s", newKey, keyValue);
      String result = processTemplate(keyValue, product);

      return Message.of(new Page(result), Metadata.of(action, eventTime, Key.of(newKey)));
    }
  }

  private String processTemplate(String key, Product product) {
    String template = repository.getTemplate(TEMPLATE_NAME);

    return template
        .replaceAll("\\$\\{product.key}", key)
        .replaceAll("\\$\\{product.name}", product.getName())
        .replaceAll("\\$\\{product.description}", product.getDescription())
        .replaceAll("\\$\\{product.imageUrl}", product.getImageUrl());
  }
}
