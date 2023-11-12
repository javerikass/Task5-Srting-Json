package ru.clevertec.product.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id", "name", "price", "weight", "isGreen", "productionDate"})
public class Product {

  private UUID id;
  private String name;
  private BigDecimal price;
  private Double weight;
  @JsonProperty("isGreen")
  private boolean isGreen;
  private OffsetDateTime productionDate;

}
