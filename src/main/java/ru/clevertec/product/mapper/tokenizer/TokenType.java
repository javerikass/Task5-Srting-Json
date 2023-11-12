package ru.clevertec.product.mapper.tokenizer;


public enum TokenType {
  BEGIN_OBJECT(1),
  END_OBJECT(2),
  BEGIN_ARRAY(4),
  END_ARRAY(8),
  NEWLINE(10),
  NULL(16),
  NUMBER(32),
  STRING(64),
  BOOLEAN(128),
  COLON(256),
  COMMA(512),
  END_DOCUMENT(1024);

  TokenType(int code) {
    this.code = code;
  }

  private int code;

  public int getTokenCode() {
    return code;
  }

}