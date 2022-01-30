package com.delonborges.dscatalog.services;

import com.delonborges.dscatalog.dto.CategoryDTO;
import com.delonborges.dscatalog.entities.Category;
import com.delonborges.dscatalog.repositories.CategoryRepository;
import com.delonborges.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll() {
        List<Category> list = categoryRepository.findAll();
        return list.stream().map(CategoryDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        Optional<Category> object = categoryRepository.findById(id);
        Category entity = object.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        return new CategoryDTO(entity);
    }

    @Transactional
    public CategoryDTO insert(CategoryDTO dtoItem) {
        Category entity = new Category();
        entity.setName(dtoItem.getName());
        entity = categoryRepository.save(entity);
        return new CategoryDTO(entity);
    }
}
