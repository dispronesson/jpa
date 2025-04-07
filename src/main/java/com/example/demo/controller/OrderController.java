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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Заказы", description = "Взаимодействие с заказами")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/users/{userId}/order")
    @Operation(summary = "Создание нового заказа")
    public ResponseEntity<OrderResponseDto> createOrder(@PathVariable long userId,
                                                        @Valid @RequestBody OrderRequestDto order) {
        OrderResponseDto createdOrder = orderService.createOrder(userId, order);
        return ResponseEntity.created(URI.create("/orders/" + createdOrder.getId()))
                .body(createdOrder);
    }

    @PutMapping("/orders/{id}")
    @Operation(summary = "Изменение существующего заказа целиком")
    public ResponseEntity<OrderResponseDto> updateOrder(@PathVariable long id,
                                                        @Valid @RequestBody OrderRequestDto order) {
        return ResponseEntity.ok(orderService.updateEntireOrder(id, order));
    }

    @PatchMapping("/orders/{id}")
    @Operation(summary = "Изменение существующего заказа частично")
    public ResponseEntity<OrderResponseDto> patchOrder(@PathVariable long id,
                                                       @RequestBody OrderRequestDto order) {
        return ResponseEntity.ok(orderService.updatePartiallyOrder(id, order));
    }

    @DeleteMapping("/orders/{id}")
    @Operation(summary = "Удаление существующего заказа")
    public ResponseEntity<OrderResponseDto> deleteOrder(@PathVariable long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/orders")
    @Operation(summary = "Получение списка заказов с пагинацией")
    public List<OrderResponseDto> getOrdersPageable(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return orderService.getOrdersPageable(page, size).getContent();
    }

    @GetMapping("/orders/{id}")
    @Operation(summary = "Получение конкретного заказа")
    public OrderResponseDto getOrder(@PathVariable long id) {
        return orderService.getOrderById(id);
    }

    @GetMapping("/orders/price")
    @Operation(summary = "Получение списка заказов с фильтрацией и пагинацией")
    public List<OrderResponseDto> getOrdersByPrice(
            @RequestParam(defaultValue = "0") double minPrice,
            @RequestParam(defaultValue = "0") double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return orderService.getOrdersByPrice(minPrice, maxPrice, page, size).getContent();
    }

    @GetMapping(value = "/orders/user/{userId}")
    @Operation(summary = "Получение списка заказов конкретного пользователя с пагинацией")
    List<OrderResponseDto> getOrdersByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return orderService.getOrdersByUserId(userId, page, size).getContent();
    }
}
