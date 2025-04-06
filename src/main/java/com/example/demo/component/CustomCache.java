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

    @SuppressWarnings("UnusedReturnValue")
    public UserResponseDto putUser(Long userId, UserResponseDto userResponseDto) {
        return userCache.put(userId, userResponseDto);
    }

    public UserResponseDto getUser(Long userId) {
        return userCache.get(userId);
    }

    @SuppressWarnings("UnusedReturnValue")
    public UserResponseDto removeUser(Long userId) {
        return userCache.remove(userId);
    }

    @SuppressWarnings("UnusedReturnValue")
    public OrderResponseDto putOrder(Long orderId, OrderResponseDto orderResponseDto) {
        return orderCache.put(orderId, orderResponseDto);
    }

    public OrderResponseDto getOrder(Long orderId) {
        return orderCache.get(orderId);
    }

    @SuppressWarnings("UnusedReturnValue")
    public OrderResponseDto removeOrder(Long orderId) {
        return orderCache.remove(orderId);
    }
}
