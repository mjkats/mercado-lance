package br.com.katsilis.mercadolance.repository;

import br.com.katsilis.mercadolance.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
