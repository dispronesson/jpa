package com.example.demo.dto;

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
    private String name;

    @NotBlank(message = "Email cannot be blank or null")
    @Email(message = "Invalid email format")
    private String email;
}
