package com.example.demo.service;

import com.example.demo.component.CustomCache;
import com.example.demo.dto.UserRequestDto;
import com.example.demo.dto.UserResponseDto;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.InvalidArgumentsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.User;
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
public class UserService {
    private final UserRepository userRepository;
    private final CustomCache cache;

    private static final String INVALID_ID_MESSAGE = "Invalid user id";
    private static final String USER_NOT_FOUND_MESSAGE = "User with id %d not found";
    private static final String EMAIL_ALREADY_EXISTS_MESSAGE = "User with email %s already exists";

    @Transactional
    public UserResponseDto createUser(UserRequestDto user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ConflictException(String
                    .format(EMAIL_ALREADY_EXISTS_MESSAGE, user.getEmail()));
        }

        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setName(user.getName());

        userRepository.save(newUser);

        return new UserResponseDto(newUser);
    }

    @Transactional
    public UserResponseDto updateEntireUser(Long id, UserRequestDto newUser) {
        User existingUser = checkUser(id, newUser);

        existingUser.setEmail(newUser.getEmail());
        existingUser.setName(newUser.getName());

        userRepository.save(existingUser);

        cache.removeUser(id);

        return new UserResponseDto(existingUser);
    }

    @Transactional
    public UserResponseDto updatePartiallyUser(Long id, UserRequestDto newUser) {
        User existingUser = checkUser(id, newUser);

        if ((newUser.getEmail() == null || newUser.getEmail().isBlank())
                && (newUser.getName() == null || newUser.getName().isBlank())) {
            throw new InvalidArgumentsException("Email and name cannot be null "
                                                + "or blank at the same time");
        }

        if (newUser.getEmail() != null) {
            if (newUser.getEmail().isBlank()) {
                throw new InvalidArgumentsException("Email cannot be blank");
            }
            existingUser.setEmail(newUser.getEmail());
        }

        if (newUser.getName() != null) {
            if (newUser.getName().isBlank()) {
                throw new InvalidArgumentsException("Name cannot be blank");
            }
            existingUser.setName(newUser.getName());
        }

        userRepository.save(existingUser);

        cache.removeUser(id);

        return new UserResponseDto(existingUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (id <= 0) {
            throw new InvalidArgumentsException(INVALID_ID_MESSAGE);
        }

        if (userRepository.existsById(id)) {
            Optional<User> user = userRepository.findById(id);
            userRepository.deleteById(id);
            cache.removeUser(id);
            user.ifPresent(value -> value.getOrders()
                    .forEach(order -> cache.removeOrder(order.getId())));
        } else {
            throw new NotFoundException(String.format(USER_NOT_FOUND_MESSAGE, id));
        }
    }

    public Page<UserResponseDto> getUsersPageable(int page, int size) {
        if (page < 0) {
            throw new InvalidArgumentsException("Page number cannot be negative");
        } else if (size <= 0) {
            throw new InvalidArgumentsException("Page size cannot be negative or zero");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<User> usersPage = userRepository.findUsersPageable(pageable);

        usersPage.getContent().forEach(user -> user.setOrders(null));
        List<UserResponseDto> usersDto = usersPage.getContent().stream()
                .map(UserResponseDto::new).toList();

        return new PageImpl<>(usersDto, pageable, usersPage.getTotalElements());
    }

    public UserResponseDto getUserById(Long id) {
        if (id <= 0) {
            throw new InvalidArgumentsException(INVALID_ID_MESSAGE);
        }

        if (cache.getUserCache().containsKey(id)) {
            return cache.getUser(id);
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String
                        .format(USER_NOT_FOUND_MESSAGE, id)));
        UserResponseDto userDto = new UserResponseDto(user);

        cache.putUser(id, userDto);
        
        return userDto;
    }

    public List<UserResponseDto> getUsersWithOrders() {
        return userRepository.findByOrdersIsNotEmpty().stream()
                .map(UserResponseDto::new).toList();
    }

    public List<UserResponseDto> getUsersWithoutOrders() {
        List<User> users = userRepository.findByOrdersIsEmpty();
        users.forEach(user -> user.setOrders(null));
        return users.stream().map(UserResponseDto::new).toList();
    }

    private User checkUser(Long id, UserRequestDto newUser) {
        if (id <= 0) {
            throw new InvalidArgumentsException(INVALID_ID_MESSAGE);
        }

        if (userRepository.existsByEmail(newUser.getEmail())) {
            throw new ConflictException(String
                    .format(EMAIL_ALREADY_EXISTS_MESSAGE, newUser.getEmail()));
        }

        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String
                        .format(USER_NOT_FOUND_MESSAGE, id)));
    }
}
