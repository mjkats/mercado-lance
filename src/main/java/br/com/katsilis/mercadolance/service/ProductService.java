package br.com.katsilis.mercadolance.service;

import br.com.katsilis.mercadolance.dto.creation.CreateProductDto;
import br.com.katsilis.mercadolance.dto.response.ProductResponseDto;
import br.com.katsilis.mercadolance.entity.Product;

import java.util.List;

public interface ProductService {
    List<ProductResponseDto> findAll();
    ProductResponseDto findById(Long id);
    Product findOriginalById(Long id);
    Long create(CreateProductDto product);
    void deleteById(Long id);
    ProductResponseDto productToResponseDto(Product product);
}
