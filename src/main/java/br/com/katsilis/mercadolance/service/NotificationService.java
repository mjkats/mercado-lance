package br.com.katsilis.mercadolance.service;

import br.com.katsilis.mercadolance.dto.creation.CreateNotificationDto;
import br.com.katsilis.mercadolance.dto.response.NotificationResponseDto;
import br.com.katsilis.mercadolance.entity.Notification;

import java.util.List;

public interface NotificationService {
    List<NotificationResponseDto> findAll();
    NotificationResponseDto findById(Long id);
    void create(CreateNotificationDto notification);
    void delete(Long id);
    NotificationResponseDto notificationToResponseDto(Notification notification);
}
