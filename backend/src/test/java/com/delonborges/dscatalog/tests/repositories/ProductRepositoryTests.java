package com.delonborges.dscatalog.tests.repositories;

import com.delonborges.dscatalog.entities.Product;
import com.delonborges.dscatalog.factory.ProductFactory;
import com.delonborges.dscatalog.repositories.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Optional;

@DataJpaTest
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository productRepository;

    private long existingId;
    private long notExistingId;
    private Product product;
    private int countTotalProducts;

    @BeforeEach
    public void setUp() {
        existingId = 1L;
        notExistingId = 26L;
        product = ProductFactory.createProductWithCategory();
        countTotalProducts = 25;
    }

    @Test
    public void findByIdShouldReturnANonEmptyOptionalObjectWhenIdExists() {
        Optional<Product> result = productRepository.findById(existingId);

        Assertions.assertTrue(result.isPresent());
    }

    @Test
    public void findByIdShouldReturnAnEmptyOptionalObjectWhenIdDoesNotExists() {
        Optional<Product> result = productRepository.findById(notExistingId);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void saveShouldPersistWhitAutoIncrementWhenIdIsNull() {
        product.setId(null);
        product = productRepository.save(product);

        Assertions.assertNotNull(product.getId());
        Assertions.assertEquals(countTotalProducts + 1, product.getId());
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {
        productRepository.deleteById(existingId);
        Optional<Product> result = productRepository.findById(existingId);

        Assertions.assertEquals(result, Optional.empty());
    }

    @Test
    public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExists() {
        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
            productRepository.deleteById(notExistingId);
        });
    }
}
