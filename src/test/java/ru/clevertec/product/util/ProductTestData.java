package ru.clevertec.product.util;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.Builder;
import lombok.Data;
import ru.clevertec.product.entity.Product;

@Data
@Builder(setterPrefix = "with")
public class ProductTestData {

  public static final java.util.UUID UUID = java.util.UUID.fromString(
      "890388c0-7736-11ee-b962-0242ac120002");
  private static final BigDecimal PRICE = BigDecimal.valueOf(10.0);
  private static final Double WEIGHT = 10.0;
  private static final String NAME = "Product";
  private static final OffsetDateTime PRODUCTION_DATE = OffsetDateTime.of(2023, 8, 8, 8, 8, 8, 8,
      ZoneOffset.UTC);

  @Builder.Default
  private java.util.UUID id = UUID;
  @Builder.Default
  private String name = NAME;
  @Builder.Default
  private BigDecimal price = PRICE;
  @Builder.Default
  private Double weight = WEIGHT;
  @Builder.Default
  private boolean isGreen = false;
  @Builder.Default
  private OffsetDateTime productionDate = PRODUCTION_DATE;

  public Product buildProduct() {
    return new Product(id, name, price, weight, isGreen, productionDate);
  }

}
