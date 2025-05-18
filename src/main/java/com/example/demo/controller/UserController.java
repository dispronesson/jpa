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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Interaction with users")
public class UserController {
    private final UserService userService;

    @PostMapping
    @Operation(summary = "Creating a new user")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto user) {
        UserResponseDto createdUser = userService.createUser(user);
        return ResponseEntity.created(URI.create("/users/" + createdUser.getId()))
                .body(createdUser);
    }

    @PostMapping("/bulk")
    @Operation(summary = "Creating new users")
    public ResponseEntity<List<UserResponseDto>> createUsers(
                    @Valid @RequestBody List<UserRequestDto> users
    ) {
        List<UserResponseDto> createdUsers = userService.createUsers(users);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUsers);
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
    @Operation(summary = "Getting a list of users")
    public List<UserResponseDto> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Getting a specific user by id")
    public UserResponseDto getUserById(@PathVariable long id) {
        return userService.getUserById(id);
    }
}
