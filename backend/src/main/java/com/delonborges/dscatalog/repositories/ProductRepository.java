package com.delonborges.dscatalog.repositories;

import com.delonborges.dscatalog.entities.Category;
import com.delonborges.dscatalog.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT DISTINCT p "
           + "FROM Product p "
           + "INNER JOIN p.categories c "
           + "WHERE :category IS NULL "
           + "OR :category IN c")
    Page<Product> findAllPaged(Category category, Pageable pageable);
}
