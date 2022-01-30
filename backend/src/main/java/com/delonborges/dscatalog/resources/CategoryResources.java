package com.delonborges.dscatalog.resources;

import com.delonborges.dscatalog.dto.CategoryDTO;
import com.delonborges.dscatalog.services.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/categories")
public class CategoryResources {

    private final CategoryService categoryService;

    public CategoryResources(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> findAll() {
        List<CategoryDTO> dtoList = categoryService.findAll();
        return ResponseEntity.ok().body(dtoList);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<CategoryDTO> findById(@PathVariable Long id) {
        CategoryDTO dtoItem = categoryService.findById(id);
        return ResponseEntity.ok().body(dtoItem);
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> insert(@RequestBody CategoryDTO dtoItem) {
        dtoItem = categoryService.insert(dtoItem);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dtoItem.getId()).toUri();
        return ResponseEntity.created(uri).body(dtoItem);
    }
}
