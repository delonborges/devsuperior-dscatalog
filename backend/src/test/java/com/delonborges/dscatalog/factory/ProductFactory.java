package com.delonborges.dscatalog.factory;

import com.delonborges.dscatalog.entities.Product;

import java.time.Instant;

public class ProductFactory {

    public static Product createProductWithCategory() {
        Product product = new Product(
                1L,
                "iPhone XR",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                2000.0,
                "https://support.apple.com/library/APPLE/APPLECARE_ALLGEOS/SP781/SP781-iPhone-xr.jpg",
                Instant.parse("2022-02-06T21:37:28Z")
        );
        product.getCategories()
               .add(CategoryFactory.createCategory());
        return product;
    }
}
