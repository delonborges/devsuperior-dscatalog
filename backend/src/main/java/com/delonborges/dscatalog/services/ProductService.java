package com.delonborges.dscatalog.services;

import com.delonborges.dscatalog.dto.CategoryDTO;
import com.delonborges.dscatalog.dto.ProductDTO;
import com.delonborges.dscatalog.entities.Category;
import com.delonborges.dscatalog.entities.Product;
import com.delonborges.dscatalog.repositories.CategoryRepository;
import com.delonborges.dscatalog.repositories.ProductRepository;
import com.delonborges.dscatalog.services.exceptions.DatabaseIntegrityViolationException;
import com.delonborges.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(Long categoryId, Pageable pageable) {
        Category categories = (categoryId == 0) ? null : categoryRepository.getOne(categoryId);
        Page<Product> page = productRepository.findAllPaged(categories, pageable);
        return page.map(ProductDTO::new);
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Optional<Product> object = productRepository.findById(id);
        Product entity = object.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        return new ProductDTO(entity, entity.getCategories());
    }

    @Transactional
    public ProductDTO insert(ProductDTO dtoItem) {
        Product entity = new Product();
        dtoToEntity(dtoItem, entity);
        entity = productRepository.save(entity);
        return new ProductDTO(entity, entity.getCategories());
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dtoItem) {
        try {
            Product entity = productRepository.getOne(id);
            dtoToEntity(dtoItem, entity);
            entity = productRepository.save(entity);
            return new ProductDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found: " + id);
        }
    }

    public void delete(Long id) {
        try {
            productRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Id not found: " + id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseIntegrityViolationException("Integrity violation");
        }
    }

    private void dtoToEntity(ProductDTO dtoItem, Product entity) {
        entity.setName(dtoItem.getName());
        entity.setDescription(dtoItem.getDescription());
        entity.setPrice(dtoItem.getPrice());
        entity.setImgUrl(dtoItem.getImgUrl());
        entity.setDate(dtoItem.getDate());

        entity.getCategories()
              .clear();

        for (CategoryDTO categorias : dtoItem.getCategories()) {
            Category category = categoryRepository.getOne(categorias.getId());
            entity.getCategories()
                  .add(category);
        }
    }
}
