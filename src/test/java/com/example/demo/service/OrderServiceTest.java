package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.example.demo.component.CustomCache;
import com.example.demo.dto.OrderRequestDto;
import com.example.demo.dto.OrderResponseDto;
import com.example.demo.exception.InvalidArgumentsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.Order;
import com.example.demo.model.User;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomCache cache;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_whenUserIdIsInvalid_throwsException() {
        OrderRequestDto orderRequestDto = new OrderRequestDto();

        assertThrows(InvalidArgumentsException.class,
                () -> orderService.createOrder(-1L, orderRequestDto));
    }

    @Test
    void createdOrder_whenUserDoesNotExist_throwsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        OrderRequestDto orderRequestDto = new OrderRequestDto();

        assertThrows(NotFoundException.class,
                () -> orderService.createOrder(1L, orderRequestDto));
    }

    @Test
    void createOrder_whenUserExists_returnsOrder() {
        OrderRequestDto orderRequestDto = new OrderRequestDto("TV", 500.0);
        User user = new User(1L, "John", "JohnDoe@mail.ru", new ArrayList<>());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });
        when(cache.removeUser(1L)).thenReturn(null);

        OrderResponseDto result = orderService.createOrder(1L, orderRequestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("TV", result.getDescription());
    }

    @Test
    void updatePartiallyOrder_whenIdIsInvalid_throwsException() {
        OrderRequestDto orderRequestDto = new OrderRequestDto();

        assertThrows(InvalidArgumentsException.class,
                () -> orderService.updatePartiallyOrder(-1L, orderRequestDto));
    }

    @Test
    void updatePartiallyOrder_whenOrderDoesNotExist_throwsException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        OrderRequestDto orderRequestDto = new OrderRequestDto();

        assertThrows(NotFoundException.class,
                () -> orderService.updatePartiallyOrder(1L, orderRequestDto));
    }

    @Test
    void updatePartiallyOrder_whenDescriptionAndPriceIsInvalid_throwsException() {
        OrderRequestDto orderRequestDto = new OrderRequestDto("  ", null);
        User user = new User(1L, "John", "JohnDoe@mail.ru", new ArrayList<>());
        Order existingOrder = new Order(1L, "Fridge", 300.0, user);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));

        assertThrows(InvalidArgumentsException.class,
                () -> orderService.updatePartiallyOrder(1L, orderRequestDto));
    }

    @Test
    void updatePartiallyOrder_whenDescriptionIsBlank_returnsOrder() {
        OrderRequestDto orderRequestDto = new OrderRequestDto("  ", 300.0);
        User user = new User(1L, "John", "JohnDoe@mail.ru", new ArrayList<>());
        Order existingOrder = new Order(1L, "Fridge", 300.0, user);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));

        assertThrows(InvalidArgumentsException.class,
                () -> orderService.updatePartiallyOrder(1L, orderRequestDto));
    }

    @Test
    void updatePartiallyOrder_whenPriceIsInvalid_throwsException() {
        OrderRequestDto orderRequestDto = new OrderRequestDto("TV", -300.0);
        User user = new User(1L, "John", "JohnDoe@mail.ru", new ArrayList<>());
        Order existingOrder = new Order(1L, "Fridge", 300.0, user);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));

        assertThrows(InvalidArgumentsException.class,
                () -> orderService.updatePartiallyOrder(1L, orderRequestDto));
    }

    @Test
    void deleteOrder_whenIdIsInvalid_throwsException() {
        assertThrows(InvalidArgumentsException.class,
                () -> orderService.deleteOrder(-1L));
    }

    @Test
    void deleteOrder_whenOrderDoesNotExist_throwsException() {
        when(orderRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> orderService.deleteOrder(1L));
    }

    @Test
    void deleteOrder_whenOrderExists_return() {
        User user = new User(1L, "John", "JohnDoe@mail.ru", new ArrayList<>());
        Order order = new Order(1L, "TV", 300.0, user);

        when(orderRepository.existsById(1L)).thenReturn(true);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        doNothing().when(orderRepository).deleteById(1L);
        when(cache.removeOrder(1L)).thenReturn(null);
        when(cache.removeUser(anyLong())).thenReturn(null);

        orderService.deleteOrder(1L);

        verify(orderRepository).deleteById(1L);
        verify(cache).removeOrder(1L);
    }

    @Test
    void getOrdersPageable_whenPageIsNegative_throwsException() {
        assertThrows(InvalidArgumentsException.class,
                () -> orderService.getOrdersPageable(-1, 5));
    }

    @Test
    void getOrdersPageable_whenSizeIsNegativeOrZero_throwsException() {
        assertThrows(InvalidArgumentsException.class,
                () -> orderService.getOrdersPageable(0, -5));
    }

    @Test
    void getOrdersPageable_whenArgumentsIsValid_returnsOrdersPageable() {
        Pageable pageable = PageRequest.of(1, 5);
        User user = new User(1L, "John", "JohnDoe@mail.ru", new ArrayList<>());
        List<Order> orders = List.of(new Order(1L, "TV", 300.0, user));
        Page<Order> ordersPage = new PageImpl<>(orders, pageable, orders.size());

        when(orderRepository.findOrdersPageable(any(Pageable.class))).thenReturn(ordersPage);

        Page<OrderResponseDto> result = orderService.getOrdersPageable(1, 5);

        assertNotNull(result);
        assertEquals(ordersPage.getTotalElements(), result.getTotalElements());
        assertEquals(ordersPage.getContent().getFirst().getId(),
                result.getContent().getFirst().getId());
    }

    @Test
    void getOrderById_whenIdIsValid_returnsOrder() {
        assertThrows(InvalidArgumentsException.class,
                () -> orderService.getOrderById(-1L));
    }

    @Test
    void getOrderById_whenOrderDoesNotExist_throwsException() {
        when(cache.containsOrder(1L)).thenReturn(false);
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> orderService.getOrderById(1L));
    }

    @Test
    void getOrderById_whenOrderExists_returnsOrder() {
        User user = new User(1L, "John", "JohnDoe@mail.ru", new ArrayList<>());
        Order order = new Order(1L, "TV", 300.0, user);

        when(cache.containsOrder(1L)).thenReturn(false);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(cache.putOrder(eq(1L), any(OrderResponseDto.class))).thenReturn(null);

        OrderResponseDto result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(order.getId(), result.getId());
        assertEquals(order.getDescription(), result.getDescription());
    }

    @Test
    void getOrderById_whenOrdersExistsInCache_returnsOrder() {
        User user = new User(1L, "John", "JohnDoe@mail.ru", new ArrayList<>());
        Order order = new Order(1L, "TV", 300.0, user);

        when(cache.containsOrder(1L)).thenReturn(true);
        when(cache.getOrder(1L)).thenReturn(OrderResponseDto.toDto(order));

        OrderResponseDto result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(order.getId(), result.getId());
        assertEquals(order.getDescription(), result.getDescription());
    }
}
