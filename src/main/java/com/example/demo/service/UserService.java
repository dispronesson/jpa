package com.example.demo.service;

import com.example.demo.component.CustomCache;
import com.example.demo.dto.OrderResponseDto;
import com.example.demo.dto.UserRequestDto;
import com.example.demo.dto.UserResponseDto;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.InvalidArgumentsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
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

        User newUser = UserRequestDto.toEntity(user);

        userRepository.save(newUser);

        return UserResponseDto.toDto(newUser);
    }

    @Transactional
    public List<UserResponseDto> createUsers(List<UserRequestDto> users) {
        List<String> emails = users.stream()
                .map(UserRequestDto::getEmail).toList();

        List<String> existingEmails = userRepository.findAllByEmailIn(emails)
                .stream().map(User::getEmail).toList();

        if (!existingEmails.isEmpty()) {
            throw new ConflictException("Next emails already exist: " + existingEmails);
        }

        List<User> newUsers = UserRequestDto.toEntityList(users);
        userRepository.saveAll(newUsers);

        return UserResponseDto.toDtoList(newUsers);
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

        return UserResponseDto.toDto(existingUser);
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

    public List<UserResponseDto> getUsers() {
        return UserResponseDto.toDtoList(userRepository.findAll())
                .stream().peek(user -> {
                    List<OrderResponseDto> orders = user.getOrders().stream()
                            .sorted(Comparator.comparing(OrderResponseDto::getId))
                            .toList();
                    user.setOrders(orders);
                }).toList();
    }

    public UserResponseDto getUserById(Long id) {
        if (id <= 0) {
            throw new InvalidArgumentsException(INVALID_ID_MESSAGE);
        }

        if (cache.containsUser(id)) {
            return cache.getUser(id);
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String
                        .format(USER_NOT_FOUND_MESSAGE, id)));
        UserResponseDto userDto = UserResponseDto.toDto(user);

        cache.putUser(id, userDto);
        
        return userDto;
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
