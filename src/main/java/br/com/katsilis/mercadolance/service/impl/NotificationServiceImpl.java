package br.com.katsilis.mercadolance.service.impl;

import br.com.katsilis.mercadolance.dto.creation.CreateNotificationDto;
import br.com.katsilis.mercadolance.dto.response.NotificationResponseDto;
import br.com.katsilis.mercadolance.dto.response.UserResponseDto;
import br.com.katsilis.mercadolance.exception.DatabaseException;
import br.com.katsilis.mercadolance.exception.notfound.NotificationNotFoundException;
import br.com.katsilis.mercadolance.model.Notification;
import br.com.katsilis.mercadolance.model.User;
import br.com.katsilis.mercadolance.repository.NotificationRepository;
import br.com.katsilis.mercadolance.service.NotificationService;
import br.com.katsilis.mercadolance.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;

    @Override
    public List<NotificationResponseDto> findAll() {
        log.info("Fetching all notifications");

        try {
            List<Notification> notifications = notificationRepository.findAll();
            List<NotificationResponseDto> response = notifications.stream().map(this::notificationToResponseDto).toList();

            log.info("Fetched notifications: {}", notifications);
            return response;
        } catch (Exception e) {
            throw new DatabaseException("Error while fetching all notifications", e);
        }
    }

    @Override
    public NotificationResponseDto findById(Long id) {
        log.info("Fetching notification by id: {}", id);

        try {
            Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification with id " + id + " not found"));

            NotificationResponseDto response = notificationToResponseDto(notification);
            log.info("Fetched notification: {}", notification);
            return response;
        } catch (NotificationNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Error while fetching notification with id " + id, e);
        }
    }

    @Override
    public void create(CreateNotificationDto notification) {
        log.info("Creating new notification with data: {}", notification);

        try {
            LocalDateTime now = LocalDateTime.now();
            User user = userService.findOriginalById(notification.getUserId());

            Notification newNotification = Notification
                .builder()
                .message(notification.getMessage())
                .user(user)
                .read(false)
                .sentAt(now)
                .build();

            notificationRepository.save(newNotification);
            log.info("Successfully created notification: {}", newNotification);
        } catch (Exception e) {
            throw new DatabaseException("Error while creating notification", e);
        }
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting notification by id: {}", id);

        try {
            if (!notificationRepository.existsById(id))
                throw new NotificationNotFoundException("Notification with id " + id + " not found");

            notificationRepository.deleteById(id);
            log.info("Successfully deleted notification with id: {}", id);
        } catch (NotificationNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Error while deleting notification with id " + id, e);
        }
    }

    @Override
    public NotificationResponseDto notificationToResponseDto(Notification notification) {
        UserResponseDto userResponseDto = userService.userToResponseDto(notification.getUser());
        return new NotificationResponseDto(userResponseDto, notification.getMessage(), notification.getSentAt(), notification.isRead());
    }
}
