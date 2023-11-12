package ru.clevertec.product.mapper.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.clevertec.product.entity.Customer;
import ru.clevertec.product.util.CustomerTestData;

class ObjectMapperImplTest {

  private ObjectMapper objectMapper;
  private ObjectMapperImpl objectMapperImpl;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    objectMapperImpl = new ObjectMapperImpl();
  }

  @Test
  void deserializeShouldReturnObject() throws JsonProcessingException {
    // given
    String json = CustomerTestData.getJsonCustomer();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    //when
    Customer actual = objectMapperImpl.deserialize(json, Customer.class).orElseThrow();
    Customer expected = objectMapper.readValue(json, Customer.class);

    // then
    Assertions.assertEquals(expected, actual);
  }

  @Test
  void serializeShouldReturnJsonString() throws JsonProcessingException {
    // given
    Customer customer = CustomerTestData.builder().build().buildCustomer();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    //when
    String expectedJson = objectMapper.writeValueAsString(customer);
    String actualJson = objectMapperImpl.serialize(customer);

    // then
    Assertions.assertEquals(expectedJson, actualJson);
  }
}