package ru.clevertec.product.mapper.tokenizer;

import static java.lang.Character.isDigit;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

  public static final String INVALID_NUMBER_FORMAT = "Invalid number format";
  public static final String INVALID_CHARACTER = "Invalid character";
  public static final String INVALID_JSON_STRING = "Invalid json string";
  public static final String ILLEGAL_CHARACTER = "Illegal character";
  public static final String INVALID_ESCAPE_CHARACTER = "Invalid escape character";
  private CharReader charReader;
  private List<Token> tokens;

  public List<Token> tokenize(String json) throws IOException, ParseException {
    charReader = new CharReader(new StringReader(json));
    tokens = new ArrayList<>();
    tokenize();
    return tokens;
  }

  private void tokenize() throws IOException, ParseException {
    Token token;
    do {
      token = start();
      tokens.add(token);
    } while (token.getTokenType() != TokenType.END_DOCUMENT);
  }

  private Token start() throws IOException, ParseException {
    char ch;
    do {
      if (!charReader.hasMore()) {
        return new Token(TokenType.END_DOCUMENT, null);
      }
      ch = charReader.next();
    } while (Character.isSpaceChar(ch));

    switch (ch) {
      case '{':
        return new Token(TokenType.BEGIN_OBJECT, String.valueOf(ch));
      case '}':
        return new Token(TokenType.END_OBJECT, String.valueOf(ch));
      case '[':
        return new Token(TokenType.BEGIN_ARRAY, String.valueOf(ch));
      case ']':
        return new Token(TokenType.END_ARRAY, String.valueOf(ch));
      case ',':
        return new Token(TokenType.COMMA, String.valueOf(ch));
      case ':':
        return new Token(TokenType.COLON, String.valueOf(ch));
      case 'n':
        return readNull();
      case 'f', 't':
        return readBoolean();
      case '"':
        return readString();
      case '-':
        return readNumber();
    }
    if (isDigit(ch)) {
      return readNumber();
    }
    throw new ParseException(ILLEGAL_CHARACTER, 1);
  }

  private Token readString() throws IOException, ParseException {
    StringBuilder sb = new StringBuilder();
    for (; ; ) {
      char ch = charReader.next();
      if (ch == '\\') {
        if (!isEscape()) {
          throw new ParseException(INVALID_ESCAPE_CHARACTER, 1);
        }
        sb.append('\\');
        ch = charReader.peek();
        sb.append(ch);
        if (ch == 'u') {
          for (int i = 0; i < 4; i++) {
            ch = charReader.next();
            if (isHex(ch)) {
              sb.append(ch);
            } else {
              throw new ParseException(INVALID_CHARACTER, 1);
            }
          }
        }
      } else if (ch == '"') {
        return new Token(TokenType.STRING, sb.toString());
      } else if (ch == '\r' || ch == '\n') {
        throw new ParseException(INVALID_CHARACTER, 1);
      } else {
        sb.append(ch);
      }
    }
  }

  private Token readNumber() throws IOException, ParseException {
    StringBuilder sb = new StringBuilder();
    boolean isDecimal = false;

    char ch = charReader.peek();
    sb.append(ch);
    while (isDigit(ch) || ch == '.') {
      ch = charReader.next();
      if (ch == ',' || ch == '}') {
        charReader.back();
        break;
      }
      sb.append(ch);

      if (ch == '.') {
        if (isDecimal) {
          throw new ParseException(INVALID_NUMBER_FORMAT, 1);
        }
        isDecimal = true;
      } else if (ch == 'e' || ch == 'E') {
        ch = charReader.peek();
        if (ch == '-' || ch == '+') {
          ch = charReader.next();
          sb.append(ch);
        }
      }
      ch = charReader.peek();
    }
    String numberStr = sb.toString();
    if (isDecimal) {
      try {
        double number = Double.parseDouble(numberStr.trim());
        return new Token(TokenType.NUMBER, number);
      } catch (NumberFormatException e) {
        throw new ParseException(INVALID_NUMBER_FORMAT, 1);
      }
    } else {
      try {
        long number = Long.parseLong(numberStr);
        return new Token(TokenType.NUMBER, number);
      } catch (NumberFormatException e) {
        throw new ParseException(INVALID_NUMBER_FORMAT, 1);
      }
    }
  }

  private Token readNull() throws IOException, ParseException {
    if (!(charReader.next() == 'u' && charReader.next() == 'l' && charReader.next() == 'l')) {
      throw new ParseException(INVALID_JSON_STRING, 1);
    }
    return new Token(TokenType.NULL, "null");
  }

  private Token readBoolean() throws IOException, ParseException {
    if ((charReader.next() == 'r' && charReader.next() == 'u' && charReader.next() == 'e')) {
      return new Token(TokenType.BOOLEAN, true);
    }
    if (charReader.peek() == 'a' && charReader.next() == 'l' && charReader.next() == 's'
        && charReader.next() == 'e') {
      return new Token(TokenType.BOOLEAN, false);
    }
    throw new ParseException(INVALID_JSON_STRING, 1);
  }

  private boolean isEscape() throws IOException {
    char ch = charReader.next();
    return (ch == '"' || ch == '\\' || ch == 'u' || ch == 'r'
        || ch == 'n' || ch == 'b' || ch == 't' || ch == 'f');
  }

  private boolean isHex(char ch) {
    return ((ch >= '0' && ch <= '9') || ('a' <= ch && ch <= 'f')
        || ('A' <= ch && ch <= 'F'));
  }
}

