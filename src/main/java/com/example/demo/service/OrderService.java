package com.example.demo.service;

import com.example.demo.component.CustomCache;
import com.example.demo.dto.OrderRequestDto;
import com.example.demo.dto.OrderResponseDto;
import com.example.demo.exception.InvalidArgumentsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.Order;
import com.example.demo.model.User;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    private static final String ORDER_NOT_FOUND_MESSAGE = "Order with id %d not found";

    @Transactional
    public OrderResponseDto createOrder(Long id, OrderRequestDto order) {
        if (id <= 0) {
            throw new InvalidArgumentsException("Invalid user id");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));

        Order newOrder = new Order();
        newOrder.setDescription(order.getDescription());
        newOrder.setPrice(order.getPrice());
        newOrder.setUser(user);

        orderRepository.save(newOrder);
        cache.removeUser(id);

        return new OrderResponseDto(newOrder);
    }

    @Transactional
    public OrderResponseDto updateEntireOrder(Long id, OrderRequestDto newOrder) {
        Order existingOrder = checkOrder(id);

        existingOrder.setDescription(newOrder.getDescription());
        existingOrder.setPrice(newOrder.getPrice());
        orderRepository.save(existingOrder);

        cache.removeOrder(id);
        cache.removeUser(existingOrder.getUser().getId());

        return new OrderResponseDto(existingOrder);
    }

    @Transactional
    public OrderResponseDto updatePartiallyOrder(Long id, OrderRequestDto newOrder) {
        Order existingOrder = checkOrder(id);

        if ((newOrder.getDescription() == null || newOrder.getDescription().isBlank())
                && (newOrder.getPrice() == null || newOrder.getPrice() <= 0)) {
            throw new InvalidArgumentsException("Description and price cannot be null"
                                                + " or blank at the same time");
        }

        if (newOrder.getDescription() != null) {
            if (newOrder.getDescription().isBlank()) {
                throw new InvalidArgumentsException("Description cannot be blank");
            }
            existingOrder.setDescription(newOrder.getDescription());
        }

        if (newOrder.getPrice() != null) {
            if (newOrder.getPrice() <= 0) {
                throw new InvalidArgumentsException("Price must be greater than 0");
            }
            existingOrder.setPrice(newOrder.getPrice());
        }

        orderRepository.save(existingOrder);

        cache.removeOrder(id);
        cache.removeUser(existingOrder.getUser().getId());

        return new OrderResponseDto(existingOrder);
    }

    @Transactional
    public void deleteOrder(Long id) {
        if (id <= 0) {
            throw new InvalidArgumentsException(INVALID_ID_MESSAGE);
        }

        if (orderRepository.existsById(id)) {
            Optional<Order> order = orderRepository.findById(id);
            orderRepository.deleteById(id);
            cache.removeOrder(id);
            order.ifPresent(value -> cache.removeUser(value.getUser().getId()));
        } else {
            throw new NotFoundException(String.format(ORDER_NOT_FOUND_MESSAGE, id));
        }
    }

    public Page<OrderResponseDto> getOrdersPageable(int page, int size) {
        checkPageAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<Order> ordersPage = orderRepository.findOrdersPageable(pageable);
        List<OrderResponseDto> ordersDto = ordersPage.getContent().stream()
                .map(OrderResponseDto::new).toList();

        return new PageImpl<>(ordersDto, pageable, ordersPage.getTotalElements());
    }

    public OrderResponseDto getOrderById(Long id) {
        if (id <= 0) {
            throw new InvalidArgumentsException(INVALID_ID_MESSAGE);
        }

        if (cache.getOrderCache().containsKey(id)) {
            return cache.getOrder(id);
        }

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String
                        .format(ORDER_NOT_FOUND_MESSAGE, id)));
        OrderResponseDto orderDto = new OrderResponseDto(order);

        cache.putOrder(id, orderDto);

        return orderDto;
    }

    public Page<OrderResponseDto> getOrdersByUserName(String userName, int page, int size) {
        if (userName.isBlank()) {
            throw new InvalidArgumentsException("User name cannot be blank");
        }

        checkPageAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<Order> ordersPage = orderRepository.findByUserName(userName, pageable);
        List<OrderResponseDto> ordersDto = ordersPage.getContent().stream()
                .map(OrderResponseDto::new).toList();

        return new PageImpl<>(ordersDto, pageable, ordersPage.getTotalElements());
    }

    public Page<OrderResponseDto> getOrdersByUserEmail(String userEmail, int page, int size) {
        if (userEmail.isBlank()) {
            throw new InvalidArgumentsException("User email cannot be blank");
        }

        checkPageAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<Order> ordersPage = orderRepository.findByUserEmail(userEmail, pageable);
        List<OrderResponseDto> ordersDto = ordersPage.getContent().stream()
                .map(OrderResponseDto::new).toList();

        return new PageImpl<>(ordersDto, pageable, ordersPage.getTotalElements());
    }

    public Page<OrderResponseDto> getOrdersByUserId(Long userId, int page, int size) {
        if (userId <= 0) {
            throw new InvalidArgumentsException(INVALID_ID_MESSAGE);
        }

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));

        checkPageAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<Order> ordersPage = orderRepository.findByUserId(userId, pageable);
        List<OrderResponseDto> ordersDto = ordersPage.getContent().stream()
                .map(OrderResponseDto::new).toList();

        return new PageImpl<>(ordersDto, pageable, ordersPage.getTotalElements());
    }

    private Order checkOrder(Long id) {
        if (id <= 0) {
            throw new InvalidArgumentsException(INVALID_ID_MESSAGE);
        }

        return  orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String
                        .format(ORDER_NOT_FOUND_MESSAGE, id)));
    }

    private void checkPageAndSize(int page, int size) {
        if (page < 0) {
            throw new InvalidArgumentsException("Page number cannot be negative");
        } else if (size <= 0) {
            throw new InvalidArgumentsException("Page size cannot be negative or zero");
        }
    }
}
