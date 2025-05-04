package com.example.demo.controller;

import com.example.demo.dto.OrderRequestDto;
import com.example.demo.dto.OrderResponseDto;
import com.example.demo.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Orders", description = "Interaction with orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/users/{userId}/order")
    @Operation(summary = "Creating a new order")
    public ResponseEntity<OrderResponseDto> createOrder(
            @PathVariable long userId,
            @Valid @RequestBody OrderRequestDto order
    ) {
        OrderResponseDto createdOrder = orderService.createOrder(userId, order);
        return ResponseEntity.created(URI.create("/orders/" + createdOrder.getId()))
                .body(createdOrder);
    }

    @PutMapping("/orders/{id}")
    @Operation(summary = "Changing an existing order entirely")
    public ResponseEntity<OrderResponseDto> updateOrder(
            @PathVariable long id,
            @Valid @RequestBody OrderRequestDto order
    ) {
        return ResponseEntity.ok(orderService.updateEntireOrder(id, order));
    }

    @PatchMapping("/orders/{id}")
    @Operation(summary = "Changing an existing order partially")
    public ResponseEntity<OrderResponseDto> patchOrder(
            @PathVariable long id,
            @RequestBody OrderRequestDto order
    ) {
        return ResponseEntity.ok(orderService.updatePartiallyOrder(id, order));
    }

    @DeleteMapping("/orders/{id}")
    @Operation(summary = "Deleting an existing order")
    public ResponseEntity<OrderResponseDto> deleteOrder(@PathVariable long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/orders")
    @Operation(summary = "Getting a list of paginated orders")
    public List<OrderResponseDto> getOrdersPageable(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return orderService.getOrdersPageable(page, size).getContent();
    }

    @GetMapping("/orders/{id}")
    @Operation(summary = "Getting a specific order")
    public OrderResponseDto getOrder(@PathVariable long id) {
        return orderService.getOrderById(id);
    }

    @GetMapping(value = "/orders/by-user-name")
    @Operation(summary = "Getting a list of paginated orders with specific user's name")
    public List<OrderResponseDto> getOrdersByUserName(
            @RequestParam String userName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return orderService.getOrdersByUserName(userName, page, size).getContent();
    }

    @GetMapping(value = "/orders/by-user-email")
    @Operation(summary = "Getting a list of paginated orders with specific user's email")
    public List<OrderResponseDto> getOrdersByUserEmail(
            @RequestParam String userEmail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return orderService.getOrdersByUserEmail(userEmail, page, size).getContent();
    }

    @GetMapping(value = "/users/{userId}/orders")
    @Operation(summary = "Getting a list of paginated orders from a specific user")
    List<OrderResponseDto> getOrdersByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return orderService.getOrdersByUserId(userId, page, size).getContent();
    }
}
