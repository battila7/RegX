package hu.fordprog.regx.input;

import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class FileInputReader implements InputReader {
  private final Path path;

  private String cachedContents;

  public static FileInputReader fromPath(String path) {
    if (path == null) {
      throw new NullPointerException("The path must not be null!");
    }

    Path filePath = Paths.get(path);

    if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
      throw new
          IllegalArgumentException("The path must be existent and must not point to a directory!");
    }

    return new FileInputReader(filePath);
  }

  private FileInputReader(Path path) {
    this.path = path;

    this.cachedContents = null;
  }

  @Override
  public String readInput() throws InputReaderException {
    if (cachedContents == null) {
      try {
        this.cachedContents = readFile();
      } catch (IOException e) {
        throw new InputReaderException(e);
      }
    }

    return cachedContents;
  }

  private String readFile() throws IOException {
    return Files.readAllLines(path).stream().collect(joining());
  }
}
