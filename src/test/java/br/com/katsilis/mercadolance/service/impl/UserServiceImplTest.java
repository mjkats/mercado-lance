package br.com.katsilis.mercadolance.service.impl;

import br.com.katsilis.mercadolance.dto.creation.CreateUserDto;
import br.com.katsilis.mercadolance.dto.response.UserResponseDto;
import br.com.katsilis.mercadolance.dto.update.UpdateUserDto;
import br.com.katsilis.mercadolance.exception.notfound.UserNotFoundException;
import br.com.katsilis.mercadolance.entity.User;
import br.com.katsilis.mercadolance.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .id(1L)
            .auth0Id("auth0|123")
            .email("user@example.com")
            .name("John Doe")
            .createdAt(LocalDateTime.now())
            .build();

        userResponseDto = new UserResponseDto(1L, "user@example.com", "John Doe");
    }

    @Test
    void testFindAll_Success() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponseDto> result = userService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userResponseDto, result.getFirst());
    }

    @Test
    void testFindById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDto result = userService.findById(1L);

        assertNotNull(result);
        assertEquals(userResponseDto, result);
    }

    @Test
    void testFindById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findById(1L));
    }

    @Test
    void testFindOriginalById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findOriginalById(1L);

        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    void testFindOriginalById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findOriginalById(1L));
    }

    @Test
    void testCreate_Success() {
        CreateUserDto dto = new CreateUserDto("auth0|123", "user@example.com", "John Doe");

        userService.create(dto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(dto.getEmail(), savedUser.getEmail());
        assertEquals(dto.getName(), savedUser.getName());
        assertEquals(dto.getAuth0Id(), savedUser.getAuth0Id());
        assertNotNull(savedUser.getCreatedAt());
    }

    @Test
    void testUpdate_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UpdateUserDto updateDto = new UpdateUserDto("Jane Doe");
        userService.update(1L, updateDto);

        assertEquals("Jane Doe", user.getName());
        verify(userRepository).save(user);
    }

    @Test
    void testUpdate_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UpdateUserDto updateDto = new UpdateUserDto("Jane Doe");

        assertThrows(UserNotFoundException.class, () -> userService.update(1L, updateDto));
    }

    @Test
    void testDeleteById_Success() {
        assertDoesNotThrow(() -> userService.deleteById(1L));
        verify(userRepository).deleteById(1L);
    }

    @Test
    void testUserToResponseDto() {
        UserResponseDto result = userService.userToResponseDto(user);
        assertEquals(userResponseDto, result);
    }
}
