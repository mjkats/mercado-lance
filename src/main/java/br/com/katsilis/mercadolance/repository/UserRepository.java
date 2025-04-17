package br.com.katsilis.mercadolance.repository;

import br.com.katsilis.mercadolance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
