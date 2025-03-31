package com.example.demo.service;

import com.example.demo.component.CustomCache;
import com.example.demo.exceptions.InvalidArgumentsException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.model.Order;
import com.example.demo.model.User;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CustomCache cache;

    private static final String INVALID_ID_MESSAGE = "Invalid order id";
    private static final String ORDER_NOT_FOUND_MESSAGE = "Order not found";
    private static final String INVALID_PRICE_MESSAGE = "Invalid order price";

    @Transactional
    public Order createOrder(Long id, Order order) {
        if (id <= 0) {
            throw new InvalidArgumentsException("Invalid user id");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        order.setId(null);
        order.setUser(user);

        return orderRepository.save(order);
    }

    @Transactional
    public Order updateEntireOrder(Long id, Order newOrder) {
        if (id <= 0) {
            throw new InvalidArgumentsException(INVALID_ID_MESSAGE);
        }

        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_MESSAGE));

        if (newOrder.getDescription() == null || newOrder.getPrice() == null
            || newOrder.getDescription().isBlank() || (newOrder.getPrice() <= 0)) {
            throw new InvalidArgumentsException("Invalid order description or order price");
        }

        existingOrder.setDescription(newOrder.getDescription());
        existingOrder.setPrice(newOrder.getPrice());
        orderRepository.save(existingOrder);

        cache.getOrderCache().remove(id);
        cache.getUserCache().remove(existingOrder.getUser().getId());

        return existingOrder;
    }

    @Transactional
    public Order updatePartiallyOrder(Long id, Order newOrder) {
        if (id <= 0) {
            throw new InvalidArgumentsException(INVALID_ID_MESSAGE);
        }

        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_MESSAGE));

        if (newOrder.getDescription() != null && !newOrder.getDescription().isBlank()) {
            existingOrder.setDescription(newOrder.getDescription());
        }

        if (newOrder.getPrice() != null && newOrder.getPrice() > 0) {
            existingOrder.setPrice(newOrder.getPrice());
        }

        orderRepository.save(existingOrder);

        cache.getOrderCache().remove(id);
        cache.getUserCache().remove(existingOrder.getUser().getId());

        return existingOrder;
    }

    @Transactional
    public void deleteOrder(Long id) {
        if (id <= 0) {
            throw new InvalidArgumentsException(INVALID_ID_MESSAGE);
        }

        if (orderRepository.existsById(id)) {
            Optional<Order> order = orderRepository.findById(id);
            orderRepository.deleteById(id);
            cache.getOrderCache().remove(id);
            order.ifPresent(value -> cache.getUserCache().remove(value.getUser().getId()));
        } else {
            throw new ResourceNotFoundException(ORDER_NOT_FOUND_MESSAGE);
        }
    }

    public Page<Order> getOrdersPageable(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return orderRepository.findOrdersPageable(pageable);
    }

    public Order getOrderById(Long id) {
        if (id <= 0) {
            throw new InvalidArgumentsException(INVALID_ID_MESSAGE);
        }

        if (cache.getOrderCache().containsKey(id)) {
            return cache.getOrderCache().get(id);
        }

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_MESSAGE));
        cache.getOrderCache().put(id, order);

        return order;
    }

    public Page<Order> getOrdersByPrice(Double minPrice, Double maxPrice, int page, int size) {
        if (minPrice < 0 || maxPrice < 0 || (minPrice > maxPrice && maxPrice != 0)
            || (minPrice == 0 && maxPrice == 0)) {
            throw new InvalidArgumentsException(INVALID_PRICE_MESSAGE);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        if (maxPrice == 0) {
            return orderRepository.findByPriceGreaterOrEqual(pageable, minPrice);
        } else if (minPrice == 0) {
            return orderRepository.findByPriceLessOrEqual(pageable, maxPrice);
        } else {
            return orderRepository.findByPriceBetween(pageable, minPrice, maxPrice);
        }
    }

    public Page<Order> getOrdersByUserId(Long userId, int page, int size) {
        if (userId <= 0) {
            throw new InvalidArgumentsException(INVALID_ID_MESSAGE);
        }

        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        return orderRepository.findByUserId(userId, pageable);
    }
}
