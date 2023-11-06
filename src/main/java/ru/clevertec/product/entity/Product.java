package ru.clevertec.product.entity;

import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Getter
@Setter
public class Product {

  private UUID id;
  private String name;
  private Double price;

}
