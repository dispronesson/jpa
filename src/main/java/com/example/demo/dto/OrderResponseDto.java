package com.example.demo.dto;

import com.example.demo.model.Order;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class OrderResponseDto {
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;
    private String description;
    private Double price;

    public OrderResponseDto(Order order) {
        this.id = order.getId();
        this.description = order.getDescription();
        this.price = order.getPrice();
    }
}