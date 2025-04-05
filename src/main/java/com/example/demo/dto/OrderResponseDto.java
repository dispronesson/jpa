package com.example.demo.dto;

import com.example.demo.model.Order;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class OrderResponseDto {
    private Long id;
    private String description;
    private Double price;

    public OrderResponseDto(Order order) {
        this.id = order.getId();
        this.description = order.getDescription();
        this.price = order.getPrice();
    }
}