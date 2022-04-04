package com.delonborges.dscatalog.tests.repositories;

import com.delonborges.dscatalog.entities.Product;
import com.delonborges.dscatalog.factory.ProductFactory;
import com.delonborges.dscatalog.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ProductRepositoryUnitTests {

    @Autowired
    private ProductRepository productRepository;

    private long existingId;
    private long nonExistingId;
    private Product product;
    private int countTotalProducts;

    @BeforeEach
    public void setUp() {
        existingId = 1L;
        nonExistingId = 26L;
        product = ProductFactory.createProductWithCategory();
        countTotalProducts = 25;
    }

    @Test
    public void findByIdShouldReturnANonEmptyOptionalObjectWhenIdExists() {
        Optional<Product> result = productRepository.findById(existingId);

        assertTrue(result.isPresent());
    }

    @Test
    public void findByIdShouldReturnAnEmptyOptionalObjectWhenIdDoesNotExist() {
        Optional<Product> result = productRepository.findById(nonExistingId);

        assertTrue(result.isEmpty());
    }

    @Test
    public void saveShouldPersistWhitAutoIncrementWhenIdIsNull() {
        product.setId(null);
        product = productRepository.save(product);

        assertNotNull(product.getId());
        assertEquals(countTotalProducts + 1, product.getId());
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {
        productRepository.deleteById(existingId);
        Optional<Product> result = productRepository.findById(existingId);

        assertEquals(result, Optional.empty());
    }

    @Test
    public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExist() {
        assertThrows(EmptyResultDataAccessException.class, () -> productRepository.deleteById(nonExistingId));
    }
}
