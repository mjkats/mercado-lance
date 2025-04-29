package br.com.katsilis.mercadolance.service.impl;

import br.com.katsilis.mercadolance.dto.creation.CreateProductDto;
import br.com.katsilis.mercadolance.dto.response.ProductResponseDto;
import br.com.katsilis.mercadolance.exception.DatabaseException;
import br.com.katsilis.mercadolance.exception.notfound.ProductNotFoundException;
import br.com.katsilis.mercadolance.model.Product;
import br.com.katsilis.mercadolance.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductResponseDto productResponseDto;

    @BeforeEach
    void setUp() {
        product = new Product(1L, "Product 1");
        productResponseDto = new ProductResponseDto("Product 1");
    }

    @Test
    void testFindAll() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductResponseDto> result = productService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(productResponseDto, result.getFirst());
    }

    @Test
    void testFindById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponseDto result = productService.findById(1L);

        assertNotNull(result);
        assertEquals(productResponseDto, result);
    }

    @Test
    void testFindById_ProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.findById(1L));
    }

    @Test
    void testCreate() {
        CreateProductDto createProductDto = new CreateProductDto("Product 1");
        Product newProduct = new Product(1L, "Product 1");

        when(productRepository.save(any(Product.class))).thenReturn(newProduct);

        productService.create(createProductDto);

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testDeleteById() {
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteById(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteById_ProductNotFound() {
        Throwable cause = new RuntimeException("Underlying error");
        doThrow(new DatabaseException("Error while deleting product with id 1", cause))
            .when(productRepository).deleteById(1L);

        DatabaseException exception = assertThrows(DatabaseException.class, () -> productService.deleteById(1L));

        assertNotNull(exception);
        assertEquals("Error while deleting product with id 1", exception.getMessage());
        assertNotNull(exception.getCause());
    }

}
