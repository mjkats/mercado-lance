package br.com.katsilis.mercadolance.repository;

import br.com.katsilis.mercadolance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByAuth0Id(String id);
}
