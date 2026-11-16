package com.mallcom.controller;

import com.mallcom.dto.ApiResponse;
import com.mallcom.dto.ProductRequest;
import com.mallcom.dto.ProductResponse;
import com.mallcom.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Product CRUD, restricted to ADMIN only.
 * Mounted under /api/admin/** so SecurityConfig's hasRole("ADMIN") rule applies;
 * any USER hitting these URLs gets a 403 from JsonAccessDeniedHandler.
 */
@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    // Powers the admin dashboard's product table (same data as the user view,
    // but only reachable by an authenticated ADMIN).
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> list() {
        return ResponseEntity.ok(ApiResponse.ok("OK", productService.search(null, null)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("OK", productService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(@Valid @RequestBody ProductRequest request) {
        ProductResponse created = productService.create(request);
        return ResponseEntity.ok(ApiResponse.ok("Product created successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(@PathVariable Long id,
                                                                 @Valid @RequestBody ProductRequest request) {
        ProductResponse updated = productService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Product updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Product deleted successfully", null));
    }
}
