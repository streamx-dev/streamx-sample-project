package org.example.project.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@QuarkusTest
@TestProfile(FileSystemTest.Configuration.class)
class FileSystemTest {

  @TempDir
  static Path sharedTempDir;

  public static class Configuration implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
      return Map.of(
          "example.resources.directory",
          sharedTempDir.toString());
    }
  }

  @ConfigProperty(name = "example.resources.directory")
  String tempDirectory;

  @Inject
  FileSystem tested;

  @Test
  void expectEmptyFileCreatedWhenDataIsEmpty() throws IOException {
    // given
    String file = "empty.txt";
    Path path = Path.of(tempDirectory, file);

    // when
    tested.writeFile(file, new byte[0]);

    // then
    assertThat(Files.exists(path)).isTrue();
    assertThat(Files.size(path)).isEqualTo(0L);
  }

  @Test
  void expectFileWithDataCreated() throws IOException {
    // given
    String file = "sample.txt";
    Path path = Path.of(tempDirectory, file);

    // when
    tested.writeFile(file, "test-value".getBytes(StandardCharsets.UTF_8));

    // then
    assertThat(Files.exists(path)).isTrue();
    assertLinesMatch(List.of("test-value"), Files.readAllLines(path));
  }

  @Test
  void expectFileAndParendDirectoriesCreated() {
    // given
    String file = "some/directory/test.txt";
    Path path = Path.of(tempDirectory, file);

    // when
    tested.writeFile(file, new byte[0]);

    // then
    assertThat(Files.exists(path)).isTrue();
  }

  @Test
  void expectFileOverwrittenWhenAlreadyExists() throws IOException {
    // given
    String file = "file.txt";
    Path path = Path.of(tempDirectory, file);
    Files.writeString(path, "existing-value");

    // when
    tested.writeFile(file, "new-value".getBytes(StandardCharsets.UTF_8));

    // then
    assertThat(Files.exists(path)).isTrue();
    assertLinesMatch(List.of("new-value"), Files.readAllLines(path));
  }

  @Test
  void expectFileDeletedWhenFileExists() throws IOException {
    // given
    String file = "file.txt";
    Path path = Path.of(tempDirectory, file);
    Files.writeString(path, "expectFileDeletedWhenFileExists");

    // when
    tested.deleteFile(file);

    // then
    assertThat(Files.exists(path)).isFalse();
  }
}
