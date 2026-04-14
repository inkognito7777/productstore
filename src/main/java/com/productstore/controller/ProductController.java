package com.productstore.controller;

import com.productstore.entity.ProductEntity;
import com.productstore.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductEntity> getProduct(@PathVariable Long id) {
        ProductEntity product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }
}
