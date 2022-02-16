package com.delonborges.dscatalog.tests.services;

import com.delonborges.dscatalog.repositories.ProductRepository;
import com.delonborges.dscatalog.services.ProductService;
import com.delonborges.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ProductServiceIntegrationTests {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private Long existingId;
    private Long nonExistingId;
    private Long totalProducts;

    @BeforeEach
    protected void setUp() {
        existingId = 1L;
        nonExistingId = 26L;
        totalProducts = 25L;
    }

    @Test
    public void deleteShouldDeleteResourceWhenIdExists() {
        productService.delete(existingId);

        Long expected = totalProducts - 1;
        Long actual = productRepository.count();

        assertEquals(expected, actual);
    }

    @Test
    public void deleteShouldThrowExceptionWhenIdDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.delete(nonExistingId);
        });
    }
}
