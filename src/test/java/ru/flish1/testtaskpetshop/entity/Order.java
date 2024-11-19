package ru.flish1.testtaskpetshop.entity;

import lombok.*;
import ru.flish1.testtaskpetshop.enums.OrderStatus;

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
}
