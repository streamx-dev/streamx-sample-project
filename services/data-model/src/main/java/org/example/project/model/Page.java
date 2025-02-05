package org.example.project.model;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.nio.ByteBuffer;
import org.apache.avro.specific.AvroGenerated;

@AvroGenerated
public class Page {

  private ByteBuffer content;

  protected Page() {
    // needed for Avro serialization
  }

  public Page(ByteBuffer content) {
    this.content = content;
  }

  public Page(byte[] content) {
    this(ByteBuffer.wrap(content));
  }

  public Page(String content) {
    this(content.getBytes(UTF_8));
  }

  public ByteBuffer getContent() {
    return content;
  }

  public String getContentAsString() {
    return new String(content.array(), UTF_8);
  }
}
