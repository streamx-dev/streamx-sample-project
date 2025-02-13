package org.example.project.model;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.nio.ByteBuffer;
import org.apache.avro.specific.AvroGenerated;

// @AvroGenerated is required for model to be sent to Pulsar's topic or received from Pulsar's subscription
@AvroGenerated
public class Json {

  private ByteBuffer content;

  protected Json() {
    // needed for Avro serialization
  }

  public Json(ByteBuffer content) {
    this.content = content;
  }

  public Json(byte[] content) {
    this(ByteBuffer.wrap(content));
  }

  public Json(String content) {
    this(content.getBytes(UTF_8));
  }

  public ByteBuffer getContent() {
    return content;
  }

  public String getContentAsString() {
    return new String(content.array(), UTF_8);
  }
}
