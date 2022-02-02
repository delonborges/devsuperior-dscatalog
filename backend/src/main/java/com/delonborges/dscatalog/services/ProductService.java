package com.delonborges.dscatalog.services;

import com.delonborges.dscatalog.dto.ProductDTO;
import com.delonborges.dscatalog.entities.Product;
import com.delonborges.dscatalog.repositories.ProductRepository;
import com.delonborges.dscatalog.services.exceptions.DatabaseIntegrityViolationException;
import com.delonborges.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(PageRequest pageRequest) {
        Page<Product> page = productRepository.findAll(pageRequest);
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
        //TODO
        //entity.setName(dtoItem.getName());
        entity = productRepository.save(entity);
        return new ProductDTO(entity);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dtoItem) {
        try {
            Product entity = productRepository.getOne(id);
            //TODO
            //entity.setName(dtoItem.getName());
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
}
