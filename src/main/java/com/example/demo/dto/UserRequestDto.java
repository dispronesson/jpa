package com.example.demo.dto;

import com.example.demo.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class UserRequestDto {
    @NotBlank(message = "Name cannot be blank or null")
    @Size(min = 2, max = 50, message = "Name must be 2-50 length")
    @Schema(description = "User's name", example = "Alex")
    private String name;

    @NotBlank(message = "Email cannot be blank or null")
    @Email(message = "Invalid email format")
    @Schema(description = "User's email", example = "alex@gmail.com")
    private String email;

    public static User toEntity(UserRequestDto dto) {
        User entity = new User();
        entity.setEmail(dto.getEmail());
        entity.setName(dto.getName());
        return entity;
    }

    public static List<User> toEntityList(List<UserRequestDto> dtoList) {
        return dtoList.stream().map(UserRequestDto::toEntity).toList();
    }
}
