package com.productstore;

import com.productstore.dto.CreateProductRequest;
import com.productstore.dto.CreateProductWithMetadataRequest;
import com.productstore.dto.ProductResponse;
import com.productstore.entity.ProductEntity;
import com.productstore.entity.ProductMetadataEntity;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public ProductResponse toResponse(ProductEntity product, ProductMetadataEntity metadataEntity) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .firm(metadataEntity.getFirm())
                .description(metadataEntity.getDescription())
                .build();
    }

    public List<ProductResponse> toResponse(
            List<ProductEntity> productList,
            List<ProductMetadataEntity> metadataList
    ) {

        return productList.stream()
                .map(product -> {

                    ProductMetadataEntity metadata = metadataList.stream()
                            .filter(m -> m.getProductId().equals(product.getId()))
                            .findFirst()
                            .orElse(null);

                    return ProductResponse.builder()
                            .id(product.getId())
                            .name(product.getName())
                            .firm(metadata != null ? metadata.getFirm() : null)
                            .description(metadata != null ? metadata.getDescription() : null)
                            .build();
                })
                .toList();
    }

    public ProductMetadataEntity toMetaDataEntity(CreateProductWithMetadataRequest request, Long productId) {
        return ProductMetadataEntity.builder()
                .productId(productId)
                .firm(request.getFirm())
                .description(request.getDescription())
                .build();
    }

}
