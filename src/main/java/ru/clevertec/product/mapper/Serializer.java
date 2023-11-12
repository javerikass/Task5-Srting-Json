package ru.clevertec.product.mapper;

import java.lang.reflect.InvocationTargetException;

public interface Serializer {

  <T> String serialize(T object)
      throws ClassNotFoundException, InvocationTargetException, IllegalAccessException;

}
