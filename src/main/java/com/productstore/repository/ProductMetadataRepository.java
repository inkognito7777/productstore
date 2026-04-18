package com.productstore.repository;

import com.productstore.entity.ProductMetadataEntity;

import java.util.Optional;

public interface ProductMetadataRepository {
    
    Optional<ProductMetadataEntity> findMetadataByProductId(Long productId);
}
