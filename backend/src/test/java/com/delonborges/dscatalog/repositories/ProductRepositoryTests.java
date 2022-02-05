package com.delonborges.dscatalog.repositories;

import com.delonborges.dscatalog.entities.Product;
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

    @BeforeEach
    public void setUp() {
        existingId = 1L;
        notExistingId = 26L;
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {
        productRepository.deleteById(existingId);
        Optional<Product> result = productRepository.findById(existingId);

        Assertions.assertEquals(result, Optional.empty());
    }

    @Test
    public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdNotExists() {
        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
            productRepository.deleteById(notExistingId);
        });
    }
}
