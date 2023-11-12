package ru.clevertec.product.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import ru.clevertec.product.entity.Order;
import ru.clevertec.product.entity.Product;

@Data
@Builder(setterPrefix = "with")
public class OrderTestData {

  public static final java.util.UUID UUID = java.util.UUID.fromString(
      "809ca3b0-7736-11ee-b962-0242ac120002");
  public static final java.util.UUID UUID2 = java.util.UUID.fromString(
      "850393c0-7736-11ee-b962-0242ac120002");

  private static final Product product = ProductTestData.builder().build().buildProduct();
  private static final Product product2 = ProductTestData.builder()
      .withPrice(BigDecimal.valueOf(24.9))
      .withId(UUID2)
      .withName("Product 2")
      .withIsGreen(true)
      .withWeight(24.8)
      .withProductionDate(
          OffsetDateTime.of(2023, 9, 9, 9, 9, 9, 9, ZoneOffset.UTC))
      .build().buildProduct();
  private static final LocalDateTime CREATE_DATE = LocalDateTime.of(2022, Month.JANUARY, 10, 10,
      10,10);


  @Builder.Default
  private UUID id = UUID;
  @Builder.Default
  private List<Product> products = List.of(product, product2);
  @Builder.Default
  private LocalDateTime createDate = CREATE_DATE;

  public Order buildOrder() {
    return new Order(id, products, createDate);
  }

  public static List<Order> getListOfOrders() {
    List<Order> orders = new ArrayList();
    orders.add(OrderTestData.builder().build().buildOrder());
    orders.add(OrderTestData.builder().build().buildOrder());
    return orders;
  }
}
