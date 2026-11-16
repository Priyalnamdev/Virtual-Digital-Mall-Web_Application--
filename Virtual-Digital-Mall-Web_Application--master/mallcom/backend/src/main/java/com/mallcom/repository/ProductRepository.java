package com.mallcom.repository;

import com.mallcom.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Used by the USER search box (case-insensitive, partial match on name)
    List<Product> findByNameContainingIgnoreCase(String name);

    // Used by the category filter dropdown
    List<Product> findByCategoryIgnoreCase(String category);

    // Used when both a keyword and a category are supplied together
    List<Product> findByNameContainingIgnoreCaseAndCategoryIgnoreCase(String name, String category);
}
