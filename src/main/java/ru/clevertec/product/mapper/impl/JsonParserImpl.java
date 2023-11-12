package ru.clevertec.product.mapper.impl;

import static ru.clevertec.product.mapper.tokenizer.TokenType.BEGIN_ARRAY;
import static ru.clevertec.product.mapper.tokenizer.TokenType.BEGIN_OBJECT;
import static ru.clevertec.product.mapper.tokenizer.TokenType.END_ARRAY;
import static ru.clevertec.product.mapper.tokenizer.TokenType.END_OBJECT;
import static ru.clevertec.product.mapper.tokenizer.TokenType.NULL;
import static ru.clevertec.product.mapper.tokenizer.TokenType.COLON;
import static ru.clevertec.product.mapper.tokenizer.TokenType.COMMA;
import static ru.clevertec.product.mapper.tokenizer.TokenType.STRING;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.clevertec.product.mapper.Parser;
import ru.clevertec.product.mapper.tokenizer.Token;
import ru.clevertec.product.mapper.tokenizer.TokenType;
import ru.clevertec.product.mapper.tokenizer.Tokenizer;

public class JsonParserImpl implements Parser {

  public static final String PARSE_ERROR_INVALID_TOKEN = "Parse error, invalid Token.";
  public static final String UNEXPECTED_TOKEN = "Unexpected Token.";
  private int tokenPosition = 0;
  private final Tokenizer tokenizer;

  public JsonParserImpl() {
    this.tokenizer = new Tokenizer();
  }

  @Override
  public Map<String, Object> parseJson(String json) throws IOException, ParseException {
    List<Token> tokenize = tokenizer.tokenize(json);
    return parseJsonTokens(tokenize);
  }

  public Map<String, Object> parseJsonTokens(List<Token> tokens) throws ParseException {
    Map<String, Object> objectMap = new HashMap<>();
    int expectToken = STRING.getTokenCode() | END_OBJECT.getTokenCode();
    String key = null;
    Object value;
    TokenType preTokenType = null;
    List<Token> tokenList = tokens.stream().skip(1).toList();

    for (; tokenPosition < tokenList.size(); tokenPosition++) {
      TokenType tokenType = tokenList.get(tokenPosition).getTokenType();
      String tokenValue = tokenList.get(tokenPosition).getValue().toString();
      if(tokenPosition ==130){
        System.out.println();
      }
      switch (tokenType) {
        case BEGIN_OBJECT:
          checkExpectToken(tokenType, expectToken);
          objectMap.put(key, parseJsonTokens(tokenList));
          expectToken = COMMA.getTokenCode() | END_OBJECT.getTokenCode();
          break;
        case END_OBJECT, END_DOCUMENT:
          checkExpectToken(tokenType, expectToken);
          return objectMap;
        case BEGIN_ARRAY:
          checkExpectToken(tokenType, expectToken);
          objectMap.put(key, parseJsonArray(tokenList));
          preTokenType = null;
          expectToken = COMMA.getTokenCode() | END_OBJECT.getTokenCode();
          break;
        case NULL:
          checkExpectToken(tokenType, expectToken);
          objectMap.put(key, null);
          expectToken = COMMA.getTokenCode() | END_OBJECT.getTokenCode();
          break;
        case NUMBER:
          checkExpectToken(tokenType, expectToken);
          if (tokenValue.contains(".")) {
            objectMap.put(key, Double.valueOf(tokenValue));
          } else {
            Long num = Long.valueOf(tokenValue);
            if (num > Integer.MAX_VALUE || num < Integer.MIN_VALUE) {
              objectMap.put(key, num);
            } else {
              objectMap.put(key, num.intValue());
            }
          }
          preTokenType = null;
          expectToken = COMMA.getTokenCode() | END_OBJECT.getTokenCode();
          break;
        case BOOLEAN:
          checkExpectToken(tokenType, expectToken);
          objectMap.put(key, Boolean.valueOf(tokenList.get(tokenPosition).getValue().toString()));
          preTokenType = null;
          expectToken = COMMA.getTokenCode() | END_OBJECT.getTokenCode();
          break;
        case STRING:
          checkExpectToken(tokenType, expectToken);
          if (preTokenType == COLON) {
            value = tokenList.get(tokenPosition).getValue();
            objectMap.put(key, value);
            expectToken = COMMA.getTokenCode() | END_OBJECT.getTokenCode();
            preTokenType = null;
          } else {
            key = tokenList.get(tokenPosition).getValue().toString();
            expectToken = COLON.getTokenCode();
          }
          break;
        case COLON:
          checkExpectToken(tokenType, expectToken);
          expectToken = NULL.getTokenCode() | TokenType.NUMBER.getTokenCode()
              | TokenType.BOOLEAN.getTokenCode() | STRING.getTokenCode()
              | BEGIN_OBJECT.getTokenCode() | BEGIN_ARRAY.getTokenCode();
          preTokenType = COLON;
          break;
        case COMMA:
          checkExpectToken(tokenType, expectToken);
          expectToken = STRING.getTokenCode();
          break;
        default:
          throw new ParseException(UNEXPECTED_TOKEN, 1);
      }
    }
    throw new ParseException(PARSE_ERROR_INVALID_TOKEN, 1);
  }

  public List<Map<String, Object>> parseJsonArray(List<Token> tokenList)
      throws ParseException {
    List<Map<String, Object>> array = new ArrayList<>();
    int expectToken = TokenType.NULL.getTokenCode() | TokenType.NUMBER.getTokenCode()
        | TokenType.BOOLEAN.getTokenCode() | TokenType.STRING.getTokenCode()
        | TokenType.BEGIN_OBJECT.getTokenCode() | TokenType.BEGIN_ARRAY.getTokenCode();
    tokenPosition = tokenPosition + 1;
    for (; tokenPosition < tokenList.size(); tokenPosition++) {
      TokenType tokenType = tokenList.get(tokenPosition).getTokenType();
      if (tokenType.equals(COMMA) || tokenType.equals(END_OBJECT)) {
        expectToken = BEGIN_OBJECT.getTokenCode() | COMMA.getTokenCode();
      } else if (tokenType.equals(END_ARRAY)) {
        expectToken = COMMA.getTokenCode() | END_ARRAY.getTokenCode();
      }
      switch (tokenType) {
        case BEGIN_OBJECT:
          checkExpectToken(tokenType, expectToken);
          array.add(parseJsonTokens(tokenList));
          expectToken = TokenType.COMMA.getTokenCode() | TokenType.END_ARRAY.getTokenCode();
          break;
        case BEGIN_ARRAY:
          checkExpectToken(tokenType, expectToken);
          array.addAll(parseJsonArray(tokenList));
          expectToken = TokenType.COMMA.getTokenCode() | TokenType.END_ARRAY.getTokenCode();
          break;
        case END_ARRAY:
          checkExpectToken(tokenType, expectToken);
          return array;
      }
    }
    throw new ParseException(PARSE_ERROR_INVALID_TOKEN, 1);
  }

  private void checkExpectToken(TokenType tokenType, int expectToken) throws ParseException {
    if ((tokenType.getTokenCode() & expectToken) == 0) {
      throw new ParseException(PARSE_ERROR_INVALID_TOKEN, 1);
    }
  }

}
