package com.example.demo.dto;

import com.example.demo.model.Order;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
public class OrderResponseDto {
    @ToString.Include
    @Schema(description = "Уникальный идентификатор заказа", example = "5")
    private Long id;
    @Schema(description = "Описание заказа", example = "TV")
    private String description;
    @Schema(description = "Цена заказа", example = "199.99")
    private Double price;

    public OrderResponseDto(Order order) {
        this.id = order.getId();
        this.description = order.getDescription();
        this.price = order.getPrice();
    }
}