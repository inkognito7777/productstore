package com.productstore.service.impl;

import com.productstore.entity.ProductEntity;
import com.productstore.repository.ProductRepository;
import com.productstore.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public ProductEntity getProductById(Long id) {
        return productRepository.findProductById(id)
                .orElseThrow(() -> new RuntimeException("Не удалось найти продукт с id: " + id));
    }
}
