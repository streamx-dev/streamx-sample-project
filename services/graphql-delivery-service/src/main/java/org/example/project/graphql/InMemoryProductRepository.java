package org.example.project.graphql;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.jboss.logging.Logger;

/**
 * This repository implementation is just for demonstration purposes, and it's not production-ready.
 */
@ApplicationScoped
public class InMemoryProductRepository {

  private final Map<String, Product> products = new ConcurrentHashMap<>();

  @Inject
  Logger log;

  public List<Product> getAllProducts() {
    return products.entrySet()
        .stream()
        .sorted(Entry.comparingByKey())
        .map(Entry::getValue)
        .collect(Collectors.toList());
  }

  public Product getProduct(String key) {
    return products.get(key);
  }

  public void putProduct(String key, Product product) {
    log.tracef("Putting product with key '%s'", key );
    products.put(key, product);
  }

  public void removeProduct(String key) {
    log.tracef("Removing product with key '%s'", key );
    products.remove(key);
  }

  public void clear() {
    log.trace("Clearing all products");
    products.clear();
  }

  public record Product(
      String name,
      String description,
      String imageUrl) { }

}
