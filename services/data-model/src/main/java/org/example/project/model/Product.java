package org.example.project.model;

import org.apache.avro.specific.AvroGenerated;

/**
 * Product contains product data. It's ingested into "inbox/pages" channel. After ingestion there are two usages of this object:
 * <ol>
 *   <li>Product is source data to fill template to generate Page</li>
 *   <li>Its data is exposed via GraphQL Delivery Service</li>
 *   <li>Product is converted to JSON and exposed as WebResource on Web Delivery Service</li>
 * </ol>
 */
// @AvroGenerated is required for model to be sent to Pulsar's topic or received from Pulsar's subscription
@AvroGenerated
public class Product {

  private String name;
  private String description;
  private String imageUrl;

  public Product() {
    // needed for Avro serialization
  }

  public Product(String name, String description, String imageUrl) {
    this.name = name;
    this.description = description;
    this.imageUrl = imageUrl;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getImageUrl() {
    return imageUrl;
  }
}
