package com.example.demo.dto;

import com.example.demo.model.User;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDto {
    @ToString.Include
    @Schema(description = "Unique user ID", example = "3")
    private Long id;
    @Schema(description = "User's name", example = "Alex")
    private String name;
    @Schema(description = "User's email", example = "alex@gmail.com")
    private String email;
    @Schema(description = "List of user's orders")
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
