package com.example.demo.dto;

import com.example.demo.model.Order;
import com.example.demo.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderRequestDto {
    @NotBlank(message = "Description cannot be blank or null")
    @Size(min = 2, max = 50, message = "Description must be 2-50 length")
    @Schema(description = "Order description", example = "TV")
    private String description;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Schema(description = "Order price", example = "199.99")
    private Double price;

    public static Order toEntity(OrderRequestDto orderDto, User userEntity) {
        Order entity = new Order();
        entity.setDescription(orderDto.getDescription());
        entity.setPrice(orderDto.getPrice());
        entity.setUser(userEntity);
        return entity;
    }

    public static List<Order> toEntityList(List<OrderRequestDto> orderDtoList, User userEntity) {
        return orderDtoList.stream()
                .map(order -> OrderRequestDto.toEntity(order, userEntity)).toList();
    }
}
