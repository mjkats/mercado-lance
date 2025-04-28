package br.com.katsilis.mercadolance.service;

import br.com.katsilis.mercadolance.model.Notification;

import java.util.List;

public interface NotificationService {
    List<Notification> findAll();
    Notification findById(Long id);
    Notification save(Notification notification);
    Notification update(Long id, Notification notification);
    void delete(Long id);
}
