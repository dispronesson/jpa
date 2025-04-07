package com.example.demo.dto;

import com.example.demo.model.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDto {
    @EqualsAndHashCode.Include
    @ToString.Include
    @Schema(description = "Уникальный идентификатор пользователя", example = "3")
    private Long id;
    @Schema(description = "Имя пользователя", example = "Alex")
    private String name;
    @Schema(description = "Электронная почта пользователя", example = "alex@gmail.com")
    private String email;
    @Schema(description = "Список заказов пользователя")
    private List<OrderResponseDto> orders;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        if (user.getOrders() != null) {
            this.orders = user.getOrders().stream().map(OrderResponseDto::new).toList();
        } else {
            this.orders = null;
        }
    }
}
