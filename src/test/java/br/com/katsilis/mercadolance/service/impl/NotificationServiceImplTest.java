package br.com.katsilis.mercadolance.service.impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import br.com.katsilis.mercadolance.dto.creation.CreateNotificationDto;
import br.com.katsilis.mercadolance.dto.response.NotificationResponseDto;
import br.com.katsilis.mercadolance.dto.response.UserResponseDto;
import br.com.katsilis.mercadolance.exception.DatabaseException;
import br.com.katsilis.mercadolance.exception.notfound.NotificationNotFoundException;
import br.com.katsilis.mercadolance.model.Notification;
import br.com.katsilis.mercadolance.model.User;
import br.com.katsilis.mercadolance.repository.NotificationRepository;
import br.com.katsilis.mercadolance.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private CreateNotificationDto createNotificationDto;
    private Notification notification;
    private User user;
    private NotificationResponseDto notificationResponseDto;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).name("Michel").email("teste@teste.com").build();
        createNotificationDto = new CreateNotificationDto(1L, "Test message");
        notification = new Notification(1L, user, "Test message", LocalDateTime.now(), false);

        notificationResponseDto = new NotificationResponseDto(
            new UserResponseDto(user.getName(), user.getEmail()),
            "Test message",
            notification.getSentAt(),
            notification.isRead()
        );
    }

    @Test
    void testFindAll() {
        when(notificationRepository.findAll()).thenReturn(List.of(notification));
        when(userService.userToResponseDto(user)).thenReturn(new UserResponseDto(user.getName(), user.getEmail()));

        List<NotificationResponseDto> result = notificationService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(notificationResponseDto, result.getFirst());
    }

    @Test
    void testFindById_Success() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(userService.userToResponseDto(user)).thenReturn(new UserResponseDto(user.getName(), user.getEmail()));

        NotificationResponseDto result = notificationService.findById(1L);

        assertNotNull(result);
        assertEquals(notificationResponseDto, result);
    }

    @Test
    void testFindById_NotFound() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotificationNotFoundException.class, () -> notificationService.findById(1L));
    }

    @Test
    void testCreate() {
        when(userService.findOriginalById(1L)).thenReturn(user);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        notificationService.create(createNotificationDto);

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testCreate_Exception() {
        when(userService.findOriginalById(1L)).thenThrow(new RuntimeException("User not found"));

        assertThrows(DatabaseException.class, () -> notificationService.create(createNotificationDto));
    }

    @Test
    void testDelete_Success() {
        when(notificationRepository.existsById(1L)).thenReturn(true);

        notificationService.delete(1L);

        verify(notificationRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDelete_NotFound() {
        when(notificationRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotificationNotFoundException.class, () -> notificationService.delete(1L));
    }

    @Test
    void testNotificationToResponseDto() {
        when(userService.userToResponseDto(user)).thenReturn(new UserResponseDto(user.getName(), user.getEmail()));

        NotificationResponseDto result = notificationService.notificationToResponseDto(notification);

        assertNotNull(result);
        assertEquals(notificationResponseDto, result);
    }
}