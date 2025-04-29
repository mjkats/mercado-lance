package br.com.katsilis.mercadolance.service.impl;

import br.com.katsilis.mercadolance.dto.creation.CreateNotificationDto;
import br.com.katsilis.mercadolance.model.Notification;
import br.com.katsilis.mercadolance.model.User;
import br.com.katsilis.mercadolance.repository.NotificationRepository;
import br.com.katsilis.mercadolance.service.NotificationService;
import br.com.katsilis.mercadolance.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;

    @Override
    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }

    @Override
    public Notification findById(Long id) {
        return notificationRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Notification with id " + id + " not found"));
    }

    @Override
    public Notification create(CreateNotificationDto notification) {
        LocalDateTime now = LocalDateTime.now();
        User user = userService.findById(notification.getUserId());

        Notification newNotification = Notification
            .builder()
            .message(notification.getMessage())
            .user(user)
            .read(false)
            .sentAt(now)
            .build();

        return notificationRepository.save(newNotification);
    }

    @Override
    public void delete(Long id) {
        if (!notificationRepository.existsById(id))
            throw new EntityNotFoundException("Notification with id " + id + " not found");
        
        notificationRepository.deleteById(id);
    }
}
