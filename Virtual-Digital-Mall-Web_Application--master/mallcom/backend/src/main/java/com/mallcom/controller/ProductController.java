package com.mallcom.controller;

import com.mallcom.dto.ApiResponse;
import com.mallcom.dto.ProductResponse;
import com.mallcom.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Read-only product endpoints available to any logged-in user (USER or ADMIN).
 * Mounted at /api/products, protected in SecurityConfig by an explicit
 * hasAnyRole("USER","ADMIN") rule on "/api/products/**".
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET /api/products                        -> all products
    // GET /api/products?name=shirt              -> search by name
    // GET /api/products?category=Electronics    -> filter by category
    // GET /api/products?name=shirt&category=Men -> both combined
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category) {
        List<ProductResponse> products = productService.search(name, category);
        return ResponseEntity.ok(ApiResponse.ok("OK", products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("OK", productService.getById(id)));
    }
}
