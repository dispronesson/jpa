package com.example.demo.dto;

import com.example.demo.model.Order;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
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
    @Schema(description = "Unique order ID", example = "5")
    private Long id;
    @Schema(description = "Order description", example = "TV")
    private String description;
    @Schema(description = "Order price", example = "199.99")
    private Double price;

    public static OrderResponseDto toDto(Order entity) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(entity.getId());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        return dto;
    }

    public static List<OrderResponseDto> toDtoList(List<Order> entityList) {
        return entityList.stream().map(OrderResponseDto::toDto).toList();
    }
}