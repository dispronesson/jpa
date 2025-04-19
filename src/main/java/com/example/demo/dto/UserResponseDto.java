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

    public static UserResponseDto toDto(User entity) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());

        if (entity.getOrders() != null) {
            dto.setOrders(OrderResponseDto.toDtoList(entity.getOrders()));
        } else {
            dto.setOrders(null);
        }

        return dto;
    }

    public static List<UserResponseDto> toDtoList(List<User> entityList) {
        return entityList.stream().map(UserResponseDto::toDto).toList();
    }
}
