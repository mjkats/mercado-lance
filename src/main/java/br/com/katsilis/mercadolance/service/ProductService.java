package br.com.katsilis.mercadolance.service;

import br.com.katsilis.mercadolance.model.Product;

import java.util.List;

public interface ProductService {
    List<Product> findAll();
    Product findById(Long id);
    Product save(Product product);
    void deleteById(Long id);
}
