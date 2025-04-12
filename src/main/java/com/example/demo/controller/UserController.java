package com.example.demo.controller;

import com.example.demo.dto.UserRequestDto;
import com.example.demo.dto.UserResponseDto;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
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
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Interaction with users")
public class UserController {
    public final UserService userService;

    @PostMapping
    @Operation(summary = "Creating a new user")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto user) {
        UserResponseDto createdUser = userService.createUser(user);
        return ResponseEntity.created(URI.create("/users/" + createdUser.getId()))
                .body(createdUser);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Changing an existing user entirely")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable long id,
            @Valid @RequestBody UserRequestDto user
    ) {
        return ResponseEntity.ok(userService.updateEntireUser(id, user));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Changing an existing user partially")
    public ResponseEntity<UserResponseDto> patchUser(
            @PathVariable long id,
            @RequestBody UserRequestDto user
    ) {
        return ResponseEntity.ok(userService.updatePartiallyUser(id, user));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleting an existing user")
    public ResponseEntity<User> deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Getting a list of paginated users")
    public List<UserResponseDto> getUsersPageable(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return userService.getUsersPageable(page, size).getContent();
    }

    @GetMapping("/with-orders")
    @Operation(summary = "Getting a list of users with orders")
    public List<UserResponseDto> getUsersWithOrders() {
        return userService.getUsersWithOrders();
    }

    @GetMapping("/without-orders")
    @Operation(summary = "Getting a list of users without orders")
    public List<UserResponseDto> getUsersWithoutOrders() {
        return userService.getUsersWithoutOrders();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Getting a specific user")
    public UserResponseDto getUser(@PathVariable long id) {
        return userService.getUserById(id);
    }
}
