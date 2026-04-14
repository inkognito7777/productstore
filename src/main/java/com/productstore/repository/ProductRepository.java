package com.productstore.repository;

import com.productstore.entity.ProductEntity;

import java.util.Optional;

public interface ProductRepository {

    Optional<ProductEntity> findProductById(Long id);
}
