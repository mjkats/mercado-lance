package br.com.katsilis.mercadolance.service;

import br.com.katsilis.mercadolance.dto.creation.CreateProductDto;
import br.com.katsilis.mercadolance.model.Product;

import java.util.List;

public interface ProductService {
    List<Product> findAll();
    Product findById(Long id);
    Product create(CreateProductDto product);
    void deleteById(Long id);
}
