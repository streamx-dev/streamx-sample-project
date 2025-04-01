package org.example.project.web;

import static java.util.Objects.requireNonNull;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
class FileSystem {

  @Inject
  Logger log;

  @ConfigProperty(name = "example.resources.directory")
  String directory;

  void writeFile(String file, byte[] data) {
    requireNonNull(file);
    requireNonNull(data);
    Path path = resolvePath(file);
    log.tracef("Saving file: %s", path);
    try {
      Files.createDirectories(path.getParent());
      Files.write(path, data);
      log.tracef("File updated: %s", path);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  void deleteFile(String file) {
    requireNonNull(file);
    Path path = resolvePath(file);
    log.trace("Deleting file: " + path);
    try {
      Files.deleteIfExists(path);
      log.tracef("File deleted: %s", path);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Path resolvePath(String file) {
    return Path.of(directory, file);
  }
}
