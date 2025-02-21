package com.example.project.relay;

import dev.streamx.quasar.reactive.messaging.metadata.Action;
import dev.streamx.quasar.reactive.messaging.metadata.EventTime;
import dev.streamx.quasar.reactive.messaging.metadata.Key;
import io.smallrye.reactive.messaging.GenericPayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

@ApplicationScoped
public class Relay {

  public static final String MESSAGES_CHANNEL = "messages";
  public static final String RELAYED_MESSAGE_CHANNEL = "relayed-messages";

  @Inject
  Logger log;

  @Incoming(MESSAGES_CHANNEL)
  @Outgoing(RELAYED_MESSAGE_CHANNEL)
  public <T> GenericPayload<T> relayMessage(T payload, Key key, Action action,
      EventTime eventTime) {
    log.tracef("Relaying: key %s, action %s, event time %s", key, action, eventTime);
    return GenericPayload.of(payload);
  }

}
