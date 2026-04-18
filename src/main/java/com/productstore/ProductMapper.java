package com.productstore;

import com.productstore.dto.CreateProductRequest;
import com.productstore.dto.ProductResponse;
import com.productstore.entity.ProductEntity;
import com.productstore.entity.ProductMetadataEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductEntity toEntity(CreateProductRequest request) {
        return ProductEntity.builder()
                .name(request.getName())
                .build();
    }

    public ProductResponse toResponse(ProductEntity entity) {
        return ProductResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public ProductResponse toResponse(ProductEntity entity, ProductMetadataEntity metadataEntity) {
        return ProductResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .firm(metadataEntity.getFirm())
                .description(metadataEntity.getDescription())
                .build();
    }
}
