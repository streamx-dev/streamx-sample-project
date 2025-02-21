package com.example.project.relay;

import io.smallrye.common.annotation.Identifier;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.apache.pulsar.client.api.Schema;
import org.example.project.model.Page;
import org.example.project.model.Product;
import org.example.project.model.WebResource;

// All defined Schemas are accessible by relay schema type definition
// set in the `mp.messaging.incoming.messages.schema` and `mp.messaging.outgoing.relayed-messages.schema` and properties.
@ApplicationScoped
public class AvroSchemaProvider {

  @Produces
  @Identifier("web-resource-schema")
  Schema<WebResource> webResourceSchema = Schema.AVRO(WebResource.class);

  @Produces
  @Identifier("product-schema")
  Schema<Product> productSchema = Schema.AVRO(Product.class);

  @Produces
  @Identifier("page-schema")
  Schema<Page> pageSchema = Schema.AVRO(Page.class);
}
