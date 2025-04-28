package org.example.project.web;

import io.quarkus.runtime.StartupEvent;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.FileSystemAccess;
import io.vertx.ext.web.handler.StaticHandler;
import jakarta.enterprise.event.Observes;
import java.nio.file.Path;
import org.eclipse.microprofile.config.inject.ConfigProperty;

public class StaticResources {

  @ConfigProperty(name = "example.resources.directory")
  String directory;

  void installRoute(@Observes StartupEvent startupEvent, Router router) {
    FileSystemAccess handlerVisibility = Path.of(directory).isAbsolute()
        ? FileSystemAccess.ROOT : FileSystemAccess.RELATIVE;
    router.route()
        .handler(StaticHandler
            .create(handlerVisibility, directory)
            .setCachingEnabled(false));
  }
}
