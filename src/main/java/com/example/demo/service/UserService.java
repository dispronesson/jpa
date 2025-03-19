package com.example.demo.service;

import com.example.demo.exceptions.AlreadyExistException;
import com.example.demo.exceptions.InvalidArgumentsException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new AlreadyExistException("User with email "
                    + user.getEmail() + " already exists");
        }
        user.setId(null);
        user.setOrders(null);
        return userRepository.save(user);
    }

    @Transactional
    public User updateEntireUser(Long id, User newUser) {
        if (id <= 0) {
            throw new InvalidArgumentsException("Invalid user id");
        }

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (newUser.getEmail() == null || newUser.getName() == null
            || newUser.getEmail().isBlank() && newUser.getName().isBlank()) {
            throw new InvalidArgumentsException("Email or name is required");
        }

        existingUser.setEmail(newUser.getEmail());
        existingUser.setName(newUser.getName());
        userRepository.save(existingUser);

        existingUser.getOrders().forEach(order -> order.setUser(null));

        return existingUser;
    }

    @Transactional
    public User updatePartiallyUser(Long id, User newUser) {
        if (id <= 0) {
            throw new InvalidArgumentsException("Invalid user id");
        }

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (newUser.getEmail() != null && !newUser.getEmail().isBlank()) {
            existingUser.setEmail(newUser.getEmail());
        }

        if (newUser.getName() != null && !newUser.getName().isBlank()) {
            existingUser.setName(newUser.getName());
        }

        userRepository.save(existingUser);

        existingUser.getOrders().forEach(order -> order.setUser(null));

        return existingUser;
    }

    @Transactional
    public void deleteUser(Long id) {
        if (id <= 0) {
            throw new InvalidArgumentsException("Invalid user id");
        }

        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("User not found");
        }
    }

    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        users.forEach(user -> user.setOrders(null));
        return users;
    }

    public User getUserById(Long id) {
        if (id <= 0) {
            throw new InvalidArgumentsException("Invalid user id");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.getOrders().forEach(order -> order.setUser(null));
        
        return user;
    }

    public List<User> getUsersWithOrders() {
        List<User> users = userRepository.findByOrdersIsNotEmpty();
        users.forEach(user -> user.getOrders().forEach(order -> order.setUser(null)));
        return users;
    }

    public List<User> getUsersWithoutOrders() {
        List<User> users = userRepository.findByOrdersIsEmpty();
        users.forEach(user -> user.setOrders(null));
        return users;
    }
}
