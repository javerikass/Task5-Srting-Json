package ru.clevertec.product.mapper;

import java.util.Optional;

public interface ObjectMapper {

  <T> Optional<T> deserialize(String json, Class<? extends T> classType);

  <T> String serialize(T object);

}
