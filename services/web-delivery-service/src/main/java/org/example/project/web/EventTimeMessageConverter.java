package org.example.project.web;

import dev.streamx.quasar.reactive.messaging.metadata.EventTime;
import io.smallrye.reactive.messaging.MessageConverter;
import jakarta.enterprise.context.ApplicationScoped;
import java.lang.reflect.Type;
import org.eclipse.microprofile.reactive.messaging.Message;

/**
 * EventTimeMessageConverter needed to convert incoming messages to EventTime.
 * EventTimeMessageConverter is used by stores, to reduce data size to only used EventTime
 */
@ApplicationScoped
public class EventTimeMessageConverter implements MessageConverter {

  @Override
  public boolean canConvert(Message<?> message, Type type) {
    return type.equals(Long.class) && message.getMetadata(EventTime.class).isPresent();
  }

  @Override
  public Message<?> convert(Message<?> message, Type type) {
    return message.withPayload(message.getMetadata(EventTime.class).get().getValue());
  }
}
