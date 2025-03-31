package com.example.demo.service;

import com.example.demo.component.CustomCache;
import com.example.demo.exceptions.AlreadyExistException;
import com.example.demo.exceptions.InvalidArgumentsException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CustomCache cache;

    private static final String INVALID_ID_MESSAGE = "Invalid user id";
    private static final String USER_NOT_FOUND_MESSAGE = "User not found";

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
            throw new InvalidArgumentsException(INVALID_ID_MESSAGE);
        }

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE));

        if (newUser.getEmail() == null || newUser.getName() == null
            || newUser.getEmail().isBlank() || newUser.getName().isBlank()) {
            throw new InvalidArgumentsException("Email or name is required");
        }

        existingUser.setEmail(newUser.getEmail());
        existingUser.setName(newUser.getName());
        userRepository.save(existingUser);

        cache.getUserCache().remove(id);

        return existingUser;
    }

    @Transactional
    public User updatePartiallyUser(Long id, User newUser) {
        if (id <= 0) {
            throw new InvalidArgumentsException(INVALID_ID_MESSAGE);
        }

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE));

        if (newUser.getEmail() != null && !newUser.getEmail().isBlank()) {
            existingUser.setEmail(newUser.getEmail());
        }

        if (newUser.getName() != null && !newUser.getName().isBlank()) {
            existingUser.setName(newUser.getName());
        }

        userRepository.save(existingUser);

        cache.getUserCache().remove(id);

        return existingUser;
    }

    @Transactional
    public void deleteUser(Long id) {
        if (id <= 0) {
            throw new InvalidArgumentsException(INVALID_ID_MESSAGE);
        }

        if (userRepository.existsById(id)) {
            Optional<User> user = userRepository.findById(id);
            userRepository.deleteById(id);
            cache.getUserCache().remove(id);
            user.ifPresent(value -> value.getOrders()
                    .forEach(order -> cache.getOrderCache().remove(order.getId())));
        } else {
            throw new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE);
        }
    }

    public Page<User> getUsersPageable(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<User> existPage = userRepository.findUsersPageable(pageable);
        List<User> users = existPage.getContent();
        users.forEach(user -> user.setOrders(null));
        return existPage;
    }

    public User getUserById(Long id) {
        if (id <= 0) {
            throw new InvalidArgumentsException(INVALID_ID_MESSAGE);
        }

        if (cache.getUserCache().containsKey(id)) {
            return cache.getUserCache().get(id);
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE));
        cache.getUserCache().put(id, user);
        
        return user;
    }

    public List<User> getUsersWithOrders() {
        return userRepository.findByOrdersIsNotEmpty();
    }

    public List<User> getUsersWithoutOrders() {
        List<User> users = userRepository.findByOrdersIsEmpty();
        users.forEach(user -> user.setOrders(null));
        return users;
    }
}
