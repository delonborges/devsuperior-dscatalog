package com.delonborges.dscatalog.factory;

import com.delonborges.dscatalog.dto.CategoryDTO;
import com.delonborges.dscatalog.entities.Category;

public class CategoryDTOFactory {
    public static CategoryDTO createCategoryDTO() {
        Category category = CategoryFactory.createCategory();
        return new CategoryDTO(category);
    }
}
