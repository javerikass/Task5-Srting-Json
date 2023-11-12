package ru.clevertec.product.mapper.impl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Optional;
import ru.clevertec.product.mapper.Deserializer;
import ru.clevertec.product.mapper.Serializer;

public class ObjectMapperImpl {

  private final Deserializer deserializer;
  private final Serializer serializer;

  public ObjectMapperImpl() {
    this.deserializer = new DeserializerImpl();
    this.serializer = new SerializerImpl();
  }

  public <T> Optional<T> deserialize(String json, Class<? extends T> classType) {
    try {
      return Optional.ofNullable(deserializer.deserialize(json, classType));
    } catch (ParseException e) {
      System.out.println("Error parsing JSON: " + e.getMessage());
    } catch (IOException e) {
      System.out.println("Error reading JSON: " + e.getMessage());
    } catch (ClassNotFoundException e) {
      System.out.println("Error finding class: " + e.getMessage());
    } catch (InvocationTargetException e) {
      System.out.println("Error invoking method: " + e.getMessage());
    } catch (NoSuchMethodException e) {
      System.out.println("Error finding method: " + e.getMessage());
    } catch (InstantiationException e) {
      System.out.println("Error instantiating class: " + e.getMessage());
    } catch (IllegalAccessException e) {
      System.out.println("Error accessing field or method: " + e.getMessage());
    }
    return Optional.empty();
  }

  public <T> String serialize(T object) {
    try {
      return serializer.serialize(object);
    } catch (ClassNotFoundException e) {
      System.out.println("Error finding class: " + e.getMessage());
    } catch (InvocationTargetException e) {
      System.out.println("Error invoking method: " + e.getMessage());
    } catch (IllegalAccessException e) {
      System.out.println("Error accessing field or method: " + e.getMessage());
    }
    return "";
  }
}
