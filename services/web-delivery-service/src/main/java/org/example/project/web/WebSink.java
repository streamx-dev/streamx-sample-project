package org.example.project.web;

import dev.streamx.quasar.reactive.messaging.Store;
import dev.streamx.quasar.reactive.messaging.annotations.FromChannel;
import dev.streamx.quasar.reactive.messaging.metadata.Action;
import dev.streamx.quasar.reactive.messaging.metadata.EventTime;
import dev.streamx.quasar.reactive.messaging.metadata.Key;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.example.project.model.Page;
import org.jboss.logging.Logger;

@ApplicationScoped
public class WebSink {

  public static final String CHANNEL_PAGES = "pages";

  @Inject
  Logger log;

  // Store is needed to filter out outdated versions.
  // As productsStore remains unused, we can store only data required for filtering (EventTime).
  // Long type stands for EventTime extracted by EventTimeMessageConverter
  @FromChannel(CHANNEL_PAGES)
  Store<Long> pageEventTimeByKey;

  @Inject
  FileSystem fileSystem;

  @Incoming(CHANNEL_PAGES)
  public void consume(Page page, Key key, Action action, EventTime eventTime) {
    if (key == null || action == null || eventTime == null) { // FIXME is this required?
      log.trace("Skipping storing of resource without required metadata");
    } else {
      log.tracef("Storing resource: key %s, action %s, event time %s", key, action, eventTime);
      updateStorage(page, key.getValue(), action);
    }
  }

  private void updateStorage(Page resource, String key, Action action) {
    if (Action.PUBLISH.equals(action)) {
      fileSystem.writeFile(key, resource.getContent().array());
    } else if (Action.UNPUBLISH.equals(action)) {
      fileSystem.deleteFile(key);
    } else {
      log.tracef("Skipping storing of resource with action {}", action);
    }
  }
}
