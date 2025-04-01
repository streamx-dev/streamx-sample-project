package org.example.project.model;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.nio.ByteBuffer;
import org.apache.avro.specific.AvroGenerated;

/**
 * <p>
 *   WebResource contains file content. It's ingested into "inbox/web-resource" channel.
 *   After ingestion, it's relayed to Web Delivery Service.
 * </p>
 * <p>
 *   WebResource is also the result of transforming Product to JSON format.
 * </p>
 */
// @AvroGenerated is required for model to be sent to Pulsar's topic or received from Pulsar's subscription
@AvroGenerated
public class WebResource {

  private ByteBuffer content;

  protected WebResource() {
    // needed for Avro serialization
  }

  public WebResource(ByteBuffer content) {
    this.content = content;
  }

  public WebResource(byte[] content) {
    this(ByteBuffer.wrap(content));
  }

  public WebResource(String content) {
    this(content.getBytes(UTF_8));
  }

  public ByteBuffer getContent() {
    return content;
  }

  public String getContentAsString() {
    return new String(content.array(), UTF_8);
  }
}
