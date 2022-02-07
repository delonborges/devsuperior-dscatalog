package com.delonborges.dscatalog.factory;

import com.delonborges.dscatalog.dto.ProductDTO;
import com.delonborges.dscatalog.entities.Product;

public class ProductDTOFactory {

    public static ProductDTO createProductDTOWithCategory() {
        Product product = ProductFactory.createProductWithCategory();
        return new ProductDTO(product, product.getCategories());
    }
}
