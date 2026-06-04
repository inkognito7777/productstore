package com.productstore.repository;

import com.productstore.entity.ProductEntity;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Optional<ProductEntity> findProductById(Long id);

    List<ProductEntity> findAllProducts();

    Long save(ProductEntity product);

    void deleteById(Long id);

    void update(ProductEntity product);

    List<ProductEntity> findByName(String name);
}
