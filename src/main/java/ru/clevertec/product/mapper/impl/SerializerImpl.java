package ru.clevertec.product.mapper.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import ru.clevertec.product.mapper.Serializer;

public class SerializerImpl implements Serializer {

  public <T> String serialize(T object)
      throws ClassNotFoundException, InvocationTargetException, IllegalAccessException {
    if (Objects.isNull(object)) {
      throw new IllegalArgumentException("Object is null");
    }
    StringBuilder jsonBuilder = new StringBuilder("{");
    Class<?> clazz = Class.forName(object.getClass().getName());
    Field[] declaredFields = clazz.getDeclaredFields();
    boolean firstField = true;

    for (Field field : declaredFields) {
      String fieldName = field.getName();
      field.setAccessible(true);
      Object fieldValue = field.get(object);
      if (!firstField) {
        jsonBuilder.append(",");
      } else {
        firstField = false;
      }
      jsonBuilder.append("\"").append(fieldName).append("\":");
      defineFormatWriting(fieldValue, jsonBuilder);
    }
    return jsonBuilder.append("}").toString();
  }

  private void defineFormatWriting(Object fieldValue, StringBuilder jsonBuilder)
      throws ClassNotFoundException, InvocationTargetException, IllegalAccessException {
    if (Objects.isNull(fieldValue)) {
      jsonBuilder.append("null");
      return;
    }
    if (Map.class.isAssignableFrom(fieldValue.getClass())) {
      writeMapToJson(fieldValue, jsonBuilder);
      return;
    } else if (Collection.class.isAssignableFrom(fieldValue.getClass())) {
      writeCollectionToJson(fieldValue, jsonBuilder);
      return;
    }
    if (writeStringAndTemporalTypeToJson(fieldValue, jsonBuilder)) {
      return;
    }
    jsonBuilder.append(fieldValue);
  }

  private static boolean writeStringAndTemporalTypeToJson(Object fieldValue,
      StringBuilder jsonBuilder) {
    if (Temporal.class.isAssignableFrom(fieldValue.getClass()) ||
        String.class.isAssignableFrom(fieldValue.getClass()) ||
        UUID.class.isAssignableFrom(fieldValue.getClass())) {
      jsonBuilder.append("\"").append(fieldValue).append("\"");
      return true;
    }
    return false;
  }

  private void writeCollectionToJson(Object fieldValue, StringBuilder jsonBuilder)
      throws ClassNotFoundException, InvocationTargetException, IllegalAccessException {
    jsonBuilder.append("[");
    boolean firstElement = true;
    Collection<?> collection = (Collection<?>) fieldValue;
    for (Object o : collection) {
      if (!firstElement) {
        jsonBuilder.append(",");
      } else {
        firstElement = false;
      }
      if (o.getClass().isPrimitive() || o instanceof String) {
        defineFormatWriting(o, jsonBuilder);
      } else {
        jsonBuilder.append(serialize(o));
      }
    }
    jsonBuilder.append("]");
  }

  private void writeMapToJson(Object fieldValue, StringBuilder jsonBuilder)
      throws ClassNotFoundException, InvocationTargetException, IllegalAccessException {
    jsonBuilder.append("{");
    boolean firstElement = true;
    Map<?, ?> map = (Map<?, ?>) fieldValue;
    Set<? extends Map.Entry<?, ?>> entries = map.entrySet();
    for (Map.Entry<?, ?> entry : entries) {
      if (!firstElement) {
        jsonBuilder.append(",");
      } else {
        firstElement = false;
      }
      defineFormatWriting(entry.getKey(), jsonBuilder);
      jsonBuilder.append(": ");
      defineFormatWriting(entry.getValue(), jsonBuilder);
    }
    jsonBuilder.append("}");
  }

}
