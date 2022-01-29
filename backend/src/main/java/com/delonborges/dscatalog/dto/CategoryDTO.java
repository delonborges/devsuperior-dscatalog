package com.delonborges.dscatalog.dto;

import com.delonborges.dscatalog.entities.Category;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

public class CategoryDTO implements Serializable {

    private static final AtomicLong serialVersionUID = new AtomicLong(1L);

    private Long id;
    private String name;

    public CategoryDTO() {
    }

    public CategoryDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public CategoryDTO(Category entity) {
        this.id = entity.getId();
        this.name = entity.getName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
