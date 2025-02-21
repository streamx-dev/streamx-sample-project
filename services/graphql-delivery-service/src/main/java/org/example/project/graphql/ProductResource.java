package org.example.project.graphql;

import jakarta.inject.Inject;
import java.util.List;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;
import org.jboss.logging.Logger;

@GraphQLApi
public class ProductResource {

  @Inject
  Logger log;

  @Inject
  ProductRepository repository;

  @Query("allProducts")
  @Description("Get all Products")
  public List<StoreProduct> getAllProducts() {
    log.trace("Get all Products");
    return repository.getAllProducts();
  }

  @Query("getProduct")
  @Description("Get Product by key")
  public StoreProduct getProduct(@Name("key") String key) {
    log.tracef("Get Product by key %s", key);
    return repository.getProduct(key);
  }
}
