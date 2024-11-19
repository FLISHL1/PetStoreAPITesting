package ru.flish1.testtaskpetshop.entity;

import lombok.*;
import ru.flish1.testtaskpetshop.enums.OrderStatus;

import java.util.Objects;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Long id;
    private Long petId;
    private Integer quantity;
    private String shipDate;
    private OrderStatus status;
    private Boolean complete;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id) && Objects.equals(petId, order.petId) && Objects.equals(quantity, order.quantity) && Objects.equals(shipDate, order.shipDate) && status == order.status && Objects.equals(complete, order.complete);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, petId, quantity, shipDate, status, complete);
    }
}
