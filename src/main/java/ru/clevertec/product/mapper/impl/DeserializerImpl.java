package ru.clevertec.product.mapper.impl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.UUID;
import ru.clevertec.product.mapper.Deserializer;
import ru.clevertec.product.mapper.Parser;

public class DeserializerImpl implements Deserializer {

  private final Parser parseJson;

  public DeserializerImpl() {
    this.parseJson = new JsonParserImpl();
  }

  public <T> T deserialize(String json, Class<? extends T> classType)
      throws
      NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException, ParseException, IOException {
    Class<?> clazz = Class.forName(classType.getName());
    T object = (T) clazz.getConstructor().newInstance();

    Field[] declaredFields = clazz.getDeclaredFields();

    Map<String, Object> objectMap = parseJson.parseJson(json);

    for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
      for (Field field : declaredFields) {
        field.setAccessible(true);
        if (entry.getKey().equalsIgnoreCase(field.getName())) {
          if (Temporal.class.isAssignableFrom(field.getType())) {
            field.set(object, deserializeTemporalType(entry.getValue(), field.getType()));
            break;
          }
          if (field.getType().isAssignableFrom(UUID.class)) {
            field.set(object, UUID.fromString(entry.getValue().toString()));
            break;
          }
          if (Collection.class.isAssignableFrom(field.getType())) {
            Class<?> fieldGenericType = getFieldGenericType(field);
            List<Map<String, Object>> value = (List<Map<String, Object>>) entry.getValue();
            Class<? extends Collection<Object>> typeCollection = (Class<? extends Collection<Object>>) field.getType();
            Collection<?> objects = deserializeCollection(value, typeCollection,
                fieldGenericType);
            field.set(object, objects);
            break;
          }
          field.set(object, entry.getValue());
          break;
        }
      }
    }
    return object;
  }

  public <T> T deserializeObject(Map<String, Object> objectMap,
      Class<?> classType)
      throws
      ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    T o = (T) classType.getConstructor().newInstance();
    Field[] declaredFields = classType.getDeclaredFields();
    for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
      for (Field field : declaredFields) {
        field.setAccessible(true);
        if (entry.getKey().equalsIgnoreCase(field.getName())) {
          if (Temporal.class.isAssignableFrom(field.getType())) {
            field.set(o, deserializeTemporalType(entry.getValue(), field.getType()));
            break;
          }
          if (field.getType().isAssignableFrom(UUID.class)) {
            field.set(o, UUID.fromString(entry.getValue().toString()));
            break;
          }
          if (Collection.class.isAssignableFrom(field.getType())) {
            Class<?> fieldGenericType = getFieldGenericType(field);
            Collection<?> objects = deserializeCollection(
                (List<Map<String, Object>>) entry.getValue(),
                (Class<? extends Collection<?>>) field.getType(),
                fieldGenericType);

            field.set(o, objects);
            break;
          }
          if (Number.class.isAssignableFrom(field.getType())) {
            field.set(o, deserializeNumberType(entry.getValue(), field.getType()));
          } else {
            field.set(o, entry.getValue());
          }
          break;
        }
      }
    }
    return o;
  }

  private static <T> T deserializeTemporalType(T object, Class<?> type) {
    if (Objects.isNull(object)) {
      return object;
    }
    switch (type.getSimpleName()) {
      case "LocalDate":
        return (T) LocalDate.parse(object.toString());
      case "LocalDateTime":
        return (T) LocalDateTime.parse(object.toString());
      case "OffsetDateTime":
        return (T) OffsetDateTime.parse(object.toString());
      case "ZonedDateTime":
        return (T) ZonedDateTime.parse(object.toString());
      default:
        return object;
    }
  }

  private static <T> T deserializeNumberType(T object, Class<?> type) {
    switch (type.getSimpleName()) {
      case "BigDecimal":
        return (T) BigDecimal.valueOf(Double.valueOf(object.toString()));
      case "Integer":
        return (T) Integer.valueOf(object.toString());
      case "Short":
        return (T) Short.valueOf(object.toString());
      case "Double":
        return (T) Double.valueOf(object.toString());
      case "Float":
        return (T) Float.valueOf(object.toString());
      case "BigInteger":
        return (T) BigInteger.valueOf(Long.valueOf(object.toString()));
      default:
        return object;
    }
  }

  public Collection<?> deserializeCollection(
      List<Map<String, Object>> objects,
      Class<? extends Collection<?>> collectionType,
      Class<?> classType)
      throws
      NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {

    Collection<?> collection = getCollectionInstanceByItsType(collectionType);

    for (Map<String, Object> map : objects) {
      collection.add(deserializeObject(map, classType));
    }
    return collection;
  }

  private static Collection<?> getCollectionInstanceByItsType(
      Class<? extends Collection<?>> collectionType) {
    return switch (collectionType.getSimpleName()) {
      case "List" -> new ArrayList<>();
      case "Set" -> new HashSet<>();
      case "Queue" -> new PriorityQueue<>();
      default -> throw new IllegalArgumentException(
          "Unsupported collection type: " + collectionType.getSimpleName());
    };
  }

  private static Class<?> getFieldGenericType(Field field) {
    Type fieldType = field.getGenericType();
    if (fieldType instanceof ParameterizedType parameterizedType) {
      Type[] typeArguments = parameterizedType.getActualTypeArguments();
      if (typeArguments.length > 0 && typeArguments[0] instanceof Class) {
        return (Class<?>) typeArguments[0];
      }
    }
    return fieldType.getClass();
  }

}
