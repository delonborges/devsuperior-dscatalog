package com.delonborges.dscatalog.resources;

import com.delonborges.dscatalog.dto.ProductDTO;
import com.delonborges.dscatalog.services.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping(value = "/products")
public class ProductResource {

    private final ProductService productService;

    public ProductResource(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> findAllPaged(@RequestParam(value = "categoryId",
                                                                       defaultValue = "0") Long categoryId,
                                                         @RequestParam(value = "name",
                                                                       defaultValue = "0") Long name,
                                                         Pageable pageable) {
        Page<ProductDTO> dtoList = productService.findAllPaged(categoryId, name.trim(), pageable);
        return ResponseEntity.ok()
                             .body(dtoList);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ProductDTO> findById(@PathVariable Long id) {
        ProductDTO dtoItem = productService.findById(id);
        return ResponseEntity.ok()
                             .body(dtoItem);
    }

    @PostMapping
    public ResponseEntity<ProductDTO> insert(@Valid @RequestBody ProductDTO dtoItem) {
        dtoItem = productService.insert(dtoItem);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                                             .path("/{id}")
                                             .buildAndExpand(dtoItem.getId())
                                             .toUri();
        return ResponseEntity.created(uri)
                             .body(dtoItem);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ProductDTO> update(@PathVariable Long id, @Valid @RequestBody ProductDTO dtoItem) {
        dtoItem = productService.update(id, dtoItem);
        return ResponseEntity.ok()
                             .body(dtoItem);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent()
                             .build();
    }
}
