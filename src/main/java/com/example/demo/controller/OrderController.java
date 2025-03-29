package com.example.demo.controller;

import com.example.demo.model.Order;
import com.example.demo.service.OrderService;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/users/{id}/order")
    public ResponseEntity<Order> createOrder(@PathVariable long id, @RequestBody Order order) {
        Order createdOrder = orderService.createOrder(id, order);
        return ResponseEntity.created(URI.create("/orders/" + createdOrder.getId()))
                .body(createdOrder);
    }

    @PutMapping("/orders/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable long id, @RequestBody Order order) {
        return ResponseEntity.ok(orderService.updateEntireOrder(id, order));
    }

    @PatchMapping("/orders/{id}")
    public ResponseEntity<Order> patchOrder(@PathVariable long id, @RequestBody Order order) {
        return ResponseEntity.ok(orderService.updatePartiallyOrder(id, order));
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Order> deleteOrder(@PathVariable long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/orders")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/orders/{id}")
    public Order getOrder(@PathVariable long id) {
        return orderService.getOrderById(id);
    }

    @GetMapping("/orders")
    public List<Order> getOrdersByPriceGreaterOrEqual(@RequestParam double minPrice) {
        return orderService.getOrdersByPriceGreaterOrEqual(minPrice);
    }

    @GetMapping("/orders")
    List<Order> getOrdersByPriceLessOrEqual(@RequestParam double minPrice) {
        return orderService.getOrdersByPriceLessOrEqual(minPrice);
    }

    @GetMapping("/orders")
    List<Order> getOrdersByPriceBetween(@RequestParam Double minPrice,
                                        @RequestParam Double maxPrice) {
        return orderService.getOrdersByPriceBetween(minPrice, maxPrice);
    }
}
