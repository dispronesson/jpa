package com.example.demo.component;

import com.example.demo.model.Order;
import com.example.demo.model.User;
import com.example.demo.util.Cache;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class CustomCache {
    private final Cache<Long, User> userCache = new Cache<>();
    private final Cache<Long, Order> orderCache = new Cache<>();
}
