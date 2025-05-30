package br.com.katsilis.mercadolance.service.impl;

import br.com.katsilis.mercadolance.dto.creation.CreateProductDto;
import br.com.katsilis.mercadolance.dto.response.ProductResponseDto;
import br.com.katsilis.mercadolance.exception.DatabaseException;
import br.com.katsilis.mercadolance.exception.notfound.ProductNotFoundException;
import br.com.katsilis.mercadolance.entity.Product;
import br.com.katsilis.mercadolance.repository.ProductRepository;
import br.com.katsilis.mercadolance.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<ProductResponseDto> findAll() {
        log.info("Fetching all products");

        try {
            List<Product> products = productRepository.findAll();
            List<ProductResponseDto> response = products.stream().map(this::productToResponseDto).toList();

            log.info("Fetched products: {}", products);
            return response;
        } catch (Exception e) {
            throw new DatabaseException("Error while fetching all products", e);
        }
    }

    @Override
    public ProductResponseDto findById(Long id) {
        log.info("Fetching product by id: {}", id);

        try {
            Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));

            ProductResponseDto response = productToResponseDto(product);
            log.info("Fetched product: {}", product);
            return response;
        } catch (ProductNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Error while fetching product with id " + id, e);
        }
    }

    @Override
    public Product findOriginalById(Long id) {
        log.info("Fetching original product entity by id: {}", id);

        try {
            Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));

            log.info("Fetched original product entity: {}", product);
            return product;
        } catch (ProductNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Error while fetching original product with id " + id, e);
        }
    }

    @Override
    public Long create(CreateProductDto product) {
        log.info("Creating new product with data: {}", product);

        try {
            Product newProduct = Product.builder().name(product.getName()).build();
            Product savedProduct = productRepository.save(newProduct);
            log.info("Successfully created product: {}", newProduct);
            return savedProduct.getId();
        } catch (Exception e) {
            throw new DatabaseException("Error while creating product", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        log.info("Deleting product by id: {}", id);

        try {
            productRepository.deleteById(id);
            log.info("Successfully deleted product with id: {}", id);
        } catch (Exception e) {
            throw new DatabaseException("Error while deleting product with id " + id, e);
        }
    }

    @Override
    public ProductResponseDto productToResponseDto(Product product) {
        return new ProductResponseDto(product.getId(), product.getName());
    }
}
