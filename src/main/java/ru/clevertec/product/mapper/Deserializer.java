package ru.clevertec.product.mapper;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

public interface Deserializer {

  <T> T deserialize(String json, Class<? extends T> classType) throws
      NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException, ParseException, IOException;

}
