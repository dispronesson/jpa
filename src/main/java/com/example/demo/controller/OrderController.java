package com.example.demo.controller;

import com.example.demo.model.Order;
import com.example.demo.service.OrderService;
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
import org.springframework.web.bind.annotation.RestController;

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
}
