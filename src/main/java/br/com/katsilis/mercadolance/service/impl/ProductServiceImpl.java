package br.com.katsilis.mercadolance.service.impl;

import br.com.katsilis.mercadolance.dto.creation.CreateProductDto;
import br.com.katsilis.mercadolance.model.Product;
import br.com.katsilis.mercadolance.repository.ProductRepository;
import br.com.katsilis.mercadolance.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Product with id " + id + " not found"));
    }

    @Override
    public Product create(CreateProductDto product) {
        Product newProduct = Product.builder().name(product.getName()).build();

        return productRepository.save(newProduct);
    }

    @Override
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }
}
