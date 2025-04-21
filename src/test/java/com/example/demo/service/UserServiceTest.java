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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    void updateEntireUser_whenUserExists_returnsUpdateUser() {
        UserRequestDto newUser = new UserRequestDto("Alex", "AlexAlex@mail.ru");

        User existingUser = new User(1L, "John", "JohnDoe@mail.ru", new ArrayList<>());

        when(userRepository.existsByEmail(newUser.getEmail())).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        when(cache.removeUser(1L)).thenReturn(null);

        UserResponseDto result = userService.updateEntireUser(1L, newUser);

        assertEquals(existingUser.getId(), result.getId());
        assertEquals(existingUser.getEmail(), result.getEmail());
        verify(userRepository).existsByEmail(newUser.getEmail());
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
        verify(cache).removeUser(1L);
    }

    @Test
    void updateEntireUser_whenUserDoesNotExist_throwException() {
        UserRequestDto newUser = new UserRequestDto("Alex", "AlexAlex@mail.ru");

        when(userRepository.existsByEmail(newUser.getEmail())).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateEntireUser(1L, newUser));
    }

    @Test
    void updateEntireUser_whenEmailIsAlreadyExists_throwException() {
        UserRequestDto newUser = new UserRequestDto("Alex", "AlexAlex@mail.ru");

        when(userRepository.existsByEmail(newUser.getEmail())).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.updateEntireUser(1L, newUser));
    }

    @Test
    void updateEntireUser_whenIdIsInvalid_throwException() {
        assertThrows(InvalidArgumentsException.class,
                () -> userService.updateEntireUser(-1L, new UserRequestDto()));
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
    void getUsersPageable_whenPageIsNegative_throwException() {
        assertThrows(InvalidArgumentsException.class,
                () -> userService.getUsersPageable(-1, 0));
    }

    @Test
    void getUsersPageable_whenSizeIsNegativeOrZero_throwException() {
        assertThrows(InvalidArgumentsException.class,
                () -> userService.getUsersPageable(0, -5));
    }

    @Test
    void getUsersPageable_whenArgumentsIsValid_returnsUsersPageable() {
        Pageable pageable = PageRequest.of(1, 5);
        List<User> users = List
                .of(new User(1L, "John", "JohnDoe@mail.ru", new ArrayList<>()));
        Page<User> usersPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findUsersPageable(any(Pageable.class))).thenReturn(usersPage);

        Page<UserResponseDto> result = userService.getUsersPageable(1, 5);

        assertEquals(usersPage.getTotalElements(), result.getTotalElements());
        assertEquals(usersPage.getContent().getFirst().getId(),
                result.getContent().getFirst().getId());
    }

    @Test
    void updatePartiallyUser_whenIdIsInvalid_throwException() {
        assertThrows(InvalidArgumentsException.class,
                () -> userService.updatePartiallyUser(-1L, new UserRequestDto()));
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

    @Test
    void updatePartiallyUser_whenEmailAndNameIsInvalid_throwException() {
        UserRequestDto newUser = new UserRequestDto("  ", "  ");

        User existingUser = new User(1L, "John", "JohnDoe@mail.ru", new ArrayList<>());

        when(userRepository.existsByEmail(newUser.getEmail())).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        assertThrows(InvalidArgumentsException.class,
                () -> userService.updatePartiallyUser(1L, newUser));
    }

    @Test
    void updatePartiallyUser_whenEmailIsBlank_throwException() {
        UserRequestDto newUser = new UserRequestDto("Alex", "  ");

        User existingUser = new User(1L, "John", "JohnDoe@mail.ru", new ArrayList<>());

        when(userRepository.existsByEmail(newUser.getEmail())).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        assertThrows(InvalidArgumentsException.class,
                () -> userService.updatePartiallyUser(1L, newUser));
    }

    @Test
    void updatePartiallyUser_whenNameIsBlank_throwException() {
        UserRequestDto newUser = new UserRequestDto(" ", "AlexAlex@mail.ru");

        User existingUser = new User(1L, "John", "JohnDoe@mail.ru", new ArrayList<>());

        when(userRepository.existsByEmail(newUser.getEmail())).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        assertThrows(InvalidArgumentsException.class,
                () -> userService.updatePartiallyUser(1L, newUser));
    }
}
