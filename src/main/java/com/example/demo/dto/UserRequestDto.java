package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserRequestDto {
    @NotBlank(message = "Name cannot be blank or null")
    @Schema(description = "Имя пользователя", example = "Alex")
    private String name;

    @NotBlank(message = "Email cannot be blank or null")
    @Email(message = "Invalid email format")
    @Schema(description = "Электронная почта пользователя", example = "alex@gmail.com")
    private String email;
}
