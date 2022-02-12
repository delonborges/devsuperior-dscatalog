package com.delonborges.dscatalog.tests.services;

import com.delonborges.dscatalog.dto.ProductDTO;
import com.delonborges.dscatalog.entities.Category;
import com.delonborges.dscatalog.entities.Product;
import com.delonborges.dscatalog.factory.CategoryFactory;
import com.delonborges.dscatalog.factory.ProductDTOFactory;
import com.delonborges.dscatalog.factory.ProductFactory;
import com.delonborges.dscatalog.repositories.CategoryRepository;
import com.delonborges.dscatalog.repositories.ProductRepository;
import com.delonborges.dscatalog.services.ProductService;
import com.delonborges.dscatalog.services.exceptions.DatabaseIntegrityViolationException;
import com.delonborges.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private Pageable pageable;
    private ProductDTO productDTO;

    @BeforeEach
    public void setUp() {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        pageable = PageRequest.of(0, 10);
        productDTO = ProductDTOFactory.createProductDTOWithCategory();

        Product product = ProductFactory.createProductWithCategory();
        Category category = CategoryFactory.createCategory();

        PageImpl<Product> page = new PageImpl<>(List.of(product));

        when(productRepository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

        when(productRepository.findById(existingId)).thenReturn(Optional.of(product));
        when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        when(productRepository.getOne(existingId)).thenReturn(product);
        when(productRepository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);

        when(categoryRepository.getOne(existingId)).thenReturn(category);
        when(categoryRepository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);

        when(productRepository.save(ArgumentMatchers.any())).thenReturn(product);

        doNothing().when(productRepository).deleteById(existingId);
        doThrow(EmptyResultDataAccessException.class).when(productRepository).deleteById(nonExistingId);
        doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependentId);
    }

    @Test
    public void findAllPagedShouldReturnPage() {
        Page<ProductDTO> result = productService.findAllPaged(pageable);

        assertNotNull(result);
        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    public void findByIdShouldReturnDTOWhenIdExists() {
        ProductDTO result = productService.findById(existingId);

        assertNotNull(result);
        verify(productRepository).findById(existingId);
    }

    @Test
    public void findByIdShouldThrowExceptionWhenIdDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.findById(nonExistingId);
        });
        verify(productRepository).findById(nonExistingId);
    }

    @Test
    public void updateShouldReturnDTOWhenIdExists() {
        ProductDTO result = productService.update(existingId, productDTO);

        assertNotNull(result);
        verify(productRepository).getOne(existingId);
        verify(categoryRepository).getOne(existingId);
    }

    @Test
    public void updateShouldThrowExceptionWhenIdDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.update(nonExistingId, productDTO);
        });
        verify(productRepository).getOne(nonExistingId);
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {
        assertDoesNotThrow(() -> {
            productService.delete(existingId);
        });
        verify(productRepository, times(1)).deleteById(existingId);
    }

    @Test
    public void deleteShouldThrowExceptionWhenIdDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.delete(nonExistingId);
        });
        verify(productRepository, times(1)).deleteById(nonExistingId);
    }

    @Test
    public void deleteShouldThrowExceptionWhenIdIsDependent() {
        assertThrows(DatabaseIntegrityViolationException.class, () -> {
            productService.delete(dependentId);
        });
        verify(productRepository, times(1)).deleteById(dependentId);
    }
}
