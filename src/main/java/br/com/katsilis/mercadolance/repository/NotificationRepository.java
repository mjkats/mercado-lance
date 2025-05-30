package br.com.katsilis.mercadolance.repository;

import br.com.katsilis.mercadolance.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
