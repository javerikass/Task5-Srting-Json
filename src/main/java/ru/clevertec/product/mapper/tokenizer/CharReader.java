package ru.clevertec.product.mapper.tokenizer;

import java.io.IOException;
import java.io.Reader;

public class CharReader {

  private static final int BUFFER_SIZE = 1024;
  private int position;
  private int size;
  private final Reader reader;
  private final char[] buffer;

  public CharReader(Reader reader) {
    this.reader = reader;
    buffer = new char[BUFFER_SIZE];
  }

  public char peek() {
    if (position - 1 >= size) {
      return (char) -1;
    }
    return buffer[position - 1];
  }

  public char next() throws IOException {
    if (!hasMore()) {
      return (char) -1;
    }
    return buffer[position++];
  }

  public void back() {
    position = Math.max(0, --position);
  }

  public boolean hasMore() throws IOException {
    if (position < size) {
      return true;
    }
    fillBuffer();
    return position < size;
  }

  void fillBuffer() throws IOException {
    int n = reader.read(buffer, 0, BUFFER_SIZE);
    if (n == -1) {
      return;
    }
    position = 0;
    size = n;
  }
}
