package ru.clevertec.product.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Getter
@Setter
public class Order {

  private UUID id;
  private List<Product> products;
  private LocalDateTime createDate;

}
