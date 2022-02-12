package com.delonborges.dscatalog.factory;

import com.delonborges.dscatalog.entities.Category;
import com.delonborges.dscatalog.entities.Product;

import java.time.Instant;

public class ProductFactory {

    public static Product createProductWithCategory() {
        Product product = new Product(
                1L,
                "Phone",
                "iPhone XR",
                2000.0,
                "https://support.apple.com/library/APPLE/APPLECARE_ALLGEOS/SP781/SP781-iPhone-xr.jpg",
                Instant.parse("2022-02-06T21:37:28Z")
        );
        product.getCategories().add(CategoryFactory.createCategory());
        return product;
    }
}
