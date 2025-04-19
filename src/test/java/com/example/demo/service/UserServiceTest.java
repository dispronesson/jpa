package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.demo.component.CustomCache;
import com.example.demo.dto.UserResponseDto;
import com.example.demo.exception.InvalidArgumentsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.ArrayList;
import java.util.Optional;

class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomCache cache;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findUserById_whenUserExists_returnsUser() {
        User user = new User(1L, "John", "JohnDoe@mail.ru", new ArrayList<>());

        when(cache.containsUser(1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cache.putUser(1L, UserResponseDto.toDto(user))).thenReturn(null);

        UserResponseDto foundUser = userService.getUserById(1L);

        assertEquals(user.getId(), foundUser.getId());
        assertEquals(user.getEmail(), foundUser.getEmail());
    }

    @Test
    void findUserById_whenUserExistsInCache_returnsUser() {
        User user = new User(1L, "John", "JohnDoe@mail.ru", new ArrayList<>());

        when(cache.containsUser(1L)).thenReturn(true);
        when(cache.getUser(1L)).thenReturn(UserResponseDto.toDto(user));

        UserResponseDto foundUser = userService.getUserById(1L);

        assertEquals(user.getId(), foundUser.getId());
        assertEquals(user.getEmail(), foundUser.getEmail());
    }

    @Test
    void findUserById_whenUserDoesNotExist_throwException() {
        when(cache.containsUser(1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void findUserById_WhenIdIsInvalid_throwException() {
        assertThrows(InvalidArgumentsException.class, () -> userService.getUserById(-1L));
    }
}
