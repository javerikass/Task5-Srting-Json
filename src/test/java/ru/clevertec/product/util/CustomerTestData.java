package ru.clevertec.product.util;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import ru.clevertec.product.entity.Customer;
import ru.clevertec.product.entity.Order;

@Data
@Builder(setterPrefix = "with")
public class CustomerTestData {

  public static final java.util.UUID UUID = java.util.UUID.fromString(
      "f26a8f36-7702-11ee-b962-0242ac120002");


  private static final String FIRST_NAME = "Jon";
  private static final String LAST_NAME = "Doe";
  private static final int AGE = 53;
  private static final LocalDate DATE_BIRTH = LocalDate.EPOCH;
  private static final List<Order> ORDERS = OrderTestData.getListOfOrders();


  @Builder.Default
  private UUID id = UUID;
  @Builder.Default
  private String firstName = FIRST_NAME;
  @Builder.Default
  private String lastName = LAST_NAME;
  @Builder.Default
  private int age = AGE;
  @Builder.Default
  private LocalDate dateBirth = DATE_BIRTH;
  @Builder.Default
  private List<Order> orders = ORDERS;

  public Customer buildCustomer() {
    return new Customer(id, firstName, lastName, age, dateBirth, orders);
  }

  public static String getJsonCustomer() {
    return "{\"id\":\"f26a8f36-7702-11ee-b962-0242ac120002\",\"firstName\":\"Jon\",\"lastName\":\"Doe\",\"age\":53,\"dateBirth\":\"1970-01-01\",\"orders\":[{\"id\":\"809ca3b0-7736-11ee-b962-0242ac120002\",\"products\":[{\"id\":\"890388c0-7736-11ee-b962-0242ac120002\",\"name\":\"Product\",\"price\":10.0,\"weight\":10.0,\"isGreen\":false,\"productionDate\":\"2023-08-08T08:08:08.000000008Z\"},{\"id\":\"850393c0-7736-11ee-b962-0242ac120002\",\"name\":\"Product 2\",\"price\":24.9,\"weight\":24.8,\"isGreen\":true,\"productionDate\":\"2023-09-09T09:09:09.000000009Z\"}],\"createDate\":\"2022-01-10T10:10:10\"},{\"id\":\"809ca3b0-7736-11ee-b962-0242ac120002\",\"products\":[{\"id\":\"890388c0-7736-11ee-b962-0242ac120002\",\"name\":\"Product\",\"price\":10.0,\"weight\":10.0,\"isGreen\":false,\"productionDate\":\"2023-08-08T08:08:08.000000008Z\"},{\"id\":\"850393c0-7736-11ee-b962-0242ac120002\",\"name\":\"Product 2\",\"price\":24.9,\"weight\":24.8,\"isGreen\":true,\"productionDate\":\"2023-09-09T09:09:09.000000009Z\"}],\"createDate\":\"2022-01-10T10:10:10\"}]}";
  }
}
