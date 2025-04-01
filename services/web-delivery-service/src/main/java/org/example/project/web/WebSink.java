package org.example.project.web;

import dev.streamx.quasar.reactive.messaging.Store;
import dev.streamx.quasar.reactive.messaging.annotations.FromChannel;
import dev.streamx.quasar.reactive.messaging.metadata.Action;
import dev.streamx.quasar.reactive.messaging.metadata.EventTime;
import dev.streamx.quasar.reactive.messaging.metadata.Key;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.nio.ByteBuffer;
import java.util.function.Function;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.example.project.model.WebResource;
import org.example.project.model.Page;
import org.jboss.logging.Logger;

@ApplicationScoped
public class WebSink {

  public static final String CHANNEL_PAGES = "pages";
  public static final String CHANNEL_WEB_RESOUCRES = "web-resources";

  @Inject
  Logger log;

  // Store is needed to filter out outdated versions.
  // As Store remains unused, we can store only data required for filtering (EventTime).
  // Long type stands for EventTime extracted by EventTimeMessageConverter
  @FromChannel(CHANNEL_PAGES)
  Store<Long> pageEventTimeByKey;

  @FromChannel(CHANNEL_WEB_RESOUCRES)
  Store<Long> webResourcesEventTimeByKey;

  @Inject
  FileSystem fileSystem;

  @Incoming(CHANNEL_PAGES)
  public void consume(Page page, Key key, Action action, EventTime eventTime) {
    process(page, Page::getContent, key, action, eventTime);
  }

  @Incoming(CHANNEL_WEB_RESOUCRES)
  public void consume(WebResource webResource, Key key, Action action, EventTime eventTime) {
    process(webResource, WebResource::getContent, key, action, eventTime);
  }

  private <T> void process(T entity, Function<T, ByteBuffer> byteExtractor,
      Key key, Action action, EventTime eventTime) {
    log.tracef("Storing json: key %s, action %s, event time %s", key, action, eventTime);
    ByteBuffer byteBuffer = entity != null ? byteExtractor.apply(entity) : null;
    updateStorage(byteBuffer, key.getValue(), action);
  }

  private void updateStorage(ByteBuffer byteBuffer, String key, Action action) {
    if (Action.PUBLISH.equals(action)) {
      fileSystem.writeFile(key, byteBuffer.array());
    } else if (Action.UNPUBLISH.equals(action)) {
      fileSystem.deleteFile(key);
    } else {
      log.tracef("Skipping storing of resource with action {}", action);
    }
  }
}
