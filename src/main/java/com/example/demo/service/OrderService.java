package com.example.demo.service;

import com.example.demo.exceptions.InvalidArgumentsException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.model.Order;
import com.example.demo.model.User;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    private static final String INVALID_ID_MESSAGE = "Invalid order id";
    private static final String ORDER_NOT_FOUND_MESSAGE = "Order not found";

    @Transactional
    public Order createOrder(Long id, Order order) {
        if (id <= 0) {
            throw new InvalidArgumentsException("Invalid user id");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        //user.setOrders(null);
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

        //existingOrder.getUser().setOrders(null);

        if (newOrder.getDescription() == null || newOrder.getPrice() == null
            || newOrder.getDescription().isBlank() || (newOrder.getPrice() <= 0)) {
            throw new InvalidArgumentsException("Invalid order description or order price");
        }

        existingOrder.setDescription(newOrder.getDescription());
        existingOrder.setPrice(newOrder.getPrice());

        return orderRepository.save(existingOrder);
    }

    @Transactional
    public Order updatePartiallyOrder(Long id, Order newOrder) {
        if (id <= 0) {
            throw new InvalidArgumentsException(INVALID_ID_MESSAGE);
        }

        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_MESSAGE));

        //existingOrder.getUser().setOrders(null);

        if (newOrder.getDescription() != null && !newOrder.getDescription().isBlank()) {
            existingOrder.setDescription(newOrder.getDescription());
        }

        if (newOrder.getPrice() != null && newOrder.getPrice() > 0) {
            existingOrder.setPrice(newOrder.getPrice());
        }

        return orderRepository.save(existingOrder);
    }

    @Transactional
    public void deleteOrder(Long id) {
        if (id <= 0) {
            throw new InvalidArgumentsException(INVALID_ID_MESSAGE);
        }

        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException(ORDER_NOT_FOUND_MESSAGE);
        }
    }

    public List<Order> getAllOrders() {
        //List<Order> orders = orderRepository.findAll();
        //orders.forEach(order -> order.getUser().setOrders(null));
        return orderRepository.findAll(); //orders;
    }

    public Order getOrderById(Long id) {
        if (id <= 0) {
            throw new InvalidArgumentsException(INVALID_ID_MESSAGE);
        }

        /*Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_MESSAGE));
        order.getUser().setOrders(null);*/

        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_MESSAGE)); //order;
    }
}
