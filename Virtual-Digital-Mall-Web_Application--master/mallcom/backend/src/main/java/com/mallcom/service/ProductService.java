package com.mallcom.service;

import com.mallcom.dto.ProductRequest;
import com.mallcom.dto.ProductResponse;
import com.mallcom.entity.Product;
import com.mallcom.exception.ProductNotFoundException;
import com.mallcom.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * All product business logic lives here so controllers stay thin.
 * Shared by both the USER-facing (read-only) and ADMIN-facing (CRUD) controllers.
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Returns products for the USER/ADMIN listing screen.
     * - no params           -> all products
     * - name only           -> search by name (partial, case-insensitive)
     * - category only       -> filter by category
     * - name + category     -> both applied together
     */
    public List<ProductResponse> search(String name, String category) {
        List<Product> products;

        boolean hasName = name != null && !name.isBlank();
        boolean hasCategory = category != null && !category.isBlank();

        if (hasName && hasCategory) {
            products = productRepository.findByNameContainingIgnoreCaseAndCategoryIgnoreCase(name, category);
        } else if (hasName) {
            products = productRepository.findByNameContainingIgnoreCase(name);
        } else if (hasCategory) {
            products = productRepository.findByCategoryIgnoreCase(category);
        } else {
            products = productRepository.findAll();
        }

        return products.stream().map(ProductResponse::from).toList();
    }

    public ProductResponse getById(Long id) {
        Product product = findEntityOrThrow(id);
        return ProductResponse.from(product);
    }

    public ProductResponse create(ProductRequest request) {
        Product product = new Product(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getCategory(),
                request.getImage()
        );
        return ProductResponse.from(productRepository.save(product));
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findEntityOrThrow(id);

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setImage(request.getImage());

        return ProductResponse.from(productRepository.save(product));
    }

    public void delete(Long id) {
        Product product = findEntityOrThrow(id);
        productRepository.delete(product);
    }

    private Product findEntityOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}
