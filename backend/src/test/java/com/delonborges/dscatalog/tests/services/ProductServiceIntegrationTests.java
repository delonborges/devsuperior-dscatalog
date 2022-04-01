package com.delonborges.dscatalog.tests.services;

import com.delonborges.dscatalog.dto.ProductDTO;
import com.delonborges.dscatalog.repositories.ProductRepository;
import com.delonborges.dscatalog.services.ProductService;
import com.delonborges.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ProductServiceIntegrationTests {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private Long existingId;
    private Long nonExistingId;
    private Long totalProducts;
    private Integer firstPage;
    private Integer totalPages;
    private Integer validPageSize;
    private PageRequest validPageRequest;
    private PageRequest invalidPageRequest;
    private PageRequest sortByNamePageRequest;

    @BeforeEach
    protected void setUp() {
        existingId = 1L;
        nonExistingId = 26L;
        totalProducts = 25L;
        firstPage = 0;
        totalPages = 3;
        validPageSize = 10;
        validPageRequest = PageRequest.of(firstPage, validPageSize);
        invalidPageRequest = PageRequest.of(totalPages + 1, validPageSize);
        sortByNamePageRequest = PageRequest.of(firstPage, validPageSize, Sort.by("name"));
    }

//    @Test
//    public void findAllPagedShouldReturnPageWhenPageExists() {
//        Page<ProductDTO> result = productService.findAllPaged(validPageRequest);
//
//        assertFalse(result.isEmpty());
//        assertEquals(firstPage, result.getNumber());
//        assertEquals(totalPages, result.getTotalPages());
//        assertEquals(validPageSize, result.getSize());
//        assertEquals(totalProducts, result.getTotalElements());
//    }
//
//    @Test
//    public void findAllPagedShouldReturnEmptyWhenPageDoesNotExist() {
//        Page<ProductDTO> result = productService.findAllPaged(invalidPageRequest);
//
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    public void findAllPagedShouldReturnSortedPageWhenSortByName() {
//        Page<ProductDTO> result = productService.findAllPaged(sortByNamePageRequest);
//
//        String firstItem = "MacBook Pro";
//        String lastItem = "PC Gamer Hera";
//
//        assertFalse(result.isEmpty());
//        assertEquals(firstItem, result.getContent().get(0).getName());
//        assertEquals(lastItem, result.getContent().get(9).getName());
//    }

    @Test
    public void deleteShouldDeleteResourceWhenIdExists() {
        productService.delete(existingId);

        Long expected = totalProducts - 1;
        Long actual = productRepository.count();

        assertEquals(expected, actual);
    }

    @Test
    public void deleteShouldThrowExceptionWhenIdDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> productService.delete(nonExistingId));
    }
}
