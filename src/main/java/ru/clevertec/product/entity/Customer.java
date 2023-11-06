package ru.clevertec.product.entity;

import java.time.LocalDate;
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
public class Customer {

  private UUID id;
  private String firstName;
  private String lastName;
  private LocalDate dateBirth;
  private List<Order> orders;

}
