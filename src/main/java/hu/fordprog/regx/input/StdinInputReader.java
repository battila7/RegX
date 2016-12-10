package hu.fordprog.regx.input;

import static java.util.stream.Collectors.joining;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;

public final class StdinInputReader implements InputReader {
  private String cachedContents;

  public StdinInputReader() {
    this.cachedContents = null;
  }

  @Override
  public String readInput() throws InputReaderException {
    if (cachedContents == null) {
      try {
        cachedContents = readStdin();
      } catch (UncheckedIOException e) {
        throw new InputReaderException(e);
      }
    }

    return cachedContents;
  }

  private String readStdin() {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    return reader.lines().collect(joining());
  }
}
