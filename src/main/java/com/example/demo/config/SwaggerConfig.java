package com.example.demo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "User-Orders API",
                description = "API системы пользователь-заказы",
                version = "1.0.0"
        )
)
public class SwaggerConfig {}