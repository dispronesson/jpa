package com.example.demo.component;

import com.example.demo.dto.OrderResponseDto;
import com.example.demo.dto.UserResponseDto;
import com.example.demo.util.Cache;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class CustomCache {
    private final Cache<Long, UserResponseDto> userCache = new Cache<>();
    private final Cache<Long, OrderResponseDto> orderCache = new Cache<>();
}
