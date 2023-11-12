package ru.clevertec.product.mapper;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

public interface Parser {

  Map<String, Object> parseJson(String json) throws IOException, ParseException;

}
