package br.com.katsilis.mercadolance.service;

import br.com.katsilis.mercadolance.dto.creation.CreateNotificationDto;
import br.com.katsilis.mercadolance.model.Notification;

import java.util.List;

public interface NotificationService {
    List<Notification> findAll();
    Notification findById(Long id);
    Notification create(CreateNotificationDto notification);
    void delete(Long id);
}
