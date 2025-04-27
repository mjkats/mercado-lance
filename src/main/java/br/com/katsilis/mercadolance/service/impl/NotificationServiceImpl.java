package br.com.katsilis.mercadolance.service.impl;

import br.com.katsilis.mercadolance.model.Notification;
import br.com.katsilis.mercadolance.repository.NotificationRepository;
import br.com.katsilis.mercadolance.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

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
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public Notification update(Long id, Notification notification) {
        Notification existing = findById(id);
        existing.setMessage(notification.getMessage());
        existing.setRead(notification.isRead());
        existing.setUser(notification.getUser());
        return notificationRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        if (!notificationRepository.existsById(id))
            throw new EntityNotFoundException("Notification with id " + id + " not found");
        
        notificationRepository.deleteById(id);
    }
}
