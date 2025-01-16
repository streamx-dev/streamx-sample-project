package org.example.project.model;

import org.apache.avro.specific.AvroGenerated;

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
