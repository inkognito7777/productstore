package com.productstore.repository;

import com.productstore.entity.ProductMetadataEntity;

import java.util.List;
import java.util.Optional;

public interface ProductMetadataRepository {

    Optional<ProductMetadataEntity> findMetadataByProductId(Long productId);

    List<ProductMetadataEntity> findMetadataByProductIds(List<Long> ids);

    Long save(ProductMetadataEntity entity);
}
