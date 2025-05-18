package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.example.demo.component.CustomCache;
import com.example.demo.dto.UserRequestDto;
import com.example.demo.dto.UserResponseDto;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.InvalidArgumentsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomCache cache;

    @InjectMocks
    private UserService userService;

    @Test
    void getUserById_whenUserExists_returnsUser() {
        User user = new User(1L, "John", "JohnDoe@mail.ru", new ArrayList<>());

        when(cache.containsUser(1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cache.putUser(eq(1L), any(UserResponseDto.class))).thenReturn(null);

        UserResponseDto foundUser = userService.getUserById(1L);

        assertEquals(user.getId(), foundUser.getId());
        assertEquals(user.getEmail(), foundUser.getEmail());
    }

    @Test
    void getUserById_whenUserExistsInCache_returnsUser() {
        User user = new User(1L, "John", "JohnDoe@mail.ru", new ArrayList<>());

        when(cache.containsUser(1L)).thenReturn(true);
        when(cache.getUser(1L)).thenReturn(UserResponseDto.toDto(user));

        UserResponseDto foundUser = userService.getUserById(1L);

        assertEquals(user.getId(), foundUser.getId());
        assertEquals(user.getEmail(), foundUser.getEmail());
    }

    @Test
    void getUserById_whenUserDoesNotExist_throwException() {
        when(cache.containsUser(1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void findUserById_WhenIdIsInvalid_throwException() {
        assertThrows(InvalidArgumentsException.class, () -> userService.getUserById(-1L));
    }

    @Test
    void createUser_ifEmailIsAlreadyExists_throwException() {
        UserRequestDto user = new UserRequestDto();
        user.setName("John");
        user.setEmail("JohnDoe@mail.ru");

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.createUser(user));
    }

    @Test
    void createUser_whenUserSaved_returnsUser() {
        UserRequestDto user = new UserRequestDto("John", "JohnDoe@mail.ru");

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserResponseDto result = userService.createUser(user);

        assertEquals(1L, result.getId());
        assertEquals("JohnDoe@mail.ru", result.getEmail());
    }

    @Test
    void createUsers_whenEmailsIsAlreadyExists_throwException() {
        List<UserRequestDto> usersRequests = List.of(new UserRequestDto("John", "JohnDoe@mail.ru"));

        List<User> users = List.of(new User(1L, "John", "JohnDoe@mail.ru", new ArrayList<>()));

        when(userRepository.findAllByEmailIn(anyList())).thenReturn(users);

        assertThrows(ConflictException.class, () -> userService.createUsers(usersRequests));
    }

    @Test
    void createUsers_whenUsersSaved_returnsUsers() {
        List<UserRequestDto> users = List.of(new UserRequestDto("John", "JohnDoe@mail.ru"));

        when(userRepository.findAllByEmailIn(anyList())).thenReturn(new ArrayList<>());

        when(userRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<User> userList = invocation.getArgument(0);
            userList.forEach(user -> user.setId(1L));
            return userList;
        });

        List<UserResponseDto> result = userService.createUsers(users);

        assertEquals(users.size(), result.size());
        assertEquals(1L, result.getFirst().getId());
        assertEquals("JohnDoe@mail.ru", result.getFirst().getEmail());
    }

    @Test
    void deleteUser_whenUserExists_return() {
        User user = new User(1L, "John", "JohnDoe@mail.ru", new ArrayList<>());

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(1L);
        when(cache.removeUser(1L)).thenReturn(null);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
        verify(cache).removeUser(1L);
    }

    @Test
    void deleteUser_whenUserDoesNotExist_throwException() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void deleteUser_whenIdIsInvalid_throwException() {
        assertThrows(InvalidArgumentsException.class, () -> userService.deleteUser(-1L));
    }

    @Test
    void updatePartiallyUser_whenIdIsInvalid_throwException() {
        UserRequestDto userRequestDto = new UserRequestDto();

        assertThrows(InvalidArgumentsException.class,
                () -> userService.updatePartiallyUser(-1L, userRequestDto));
    }

    @Test
    void updatePartiallyUser_whenEmailIsAlreadyExists_throwException() {
        UserRequestDto newUser = new UserRequestDto("Alex", "AlexAlex@mail.ru");

        when(userRepository.existsByEmail(newUser.getEmail())).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.updatePartiallyUser(1L, newUser));
    }

    @Test
    void updatePartiallyUser_whenUserDoesNotExist_throwException() {
        UserRequestDto newUser = new UserRequestDto("Alex", "AlexAlex@mail.ru");

        when(userRepository.existsByEmail(newUser.getEmail())).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updatePartiallyUser(1L, newUser));
    }

    @ParameterizedTest
    @CsvSource({
            "'  ', '  '",
            "'Alex', '  '",
            "'  ', 'AlexAlex@mail.ru'"
    })
    void updatePartiallyUser_withInvalidNameOrEmail_throwException(String name, String email) {
        UserRequestDto newUser = new UserRequestDto(name, email);
        User existingUser = new User(1L, "John", "JohnDoe@mail.ru", new ArrayList<>());

        when(userRepository.existsByEmail(newUser.getEmail())).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        assertThrows(InvalidArgumentsException.class,
                () -> userService.updatePartiallyUser(1L, newUser));
    }
}
