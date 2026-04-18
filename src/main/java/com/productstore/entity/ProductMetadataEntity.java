package com.productstore.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductMetadataEntity {
    private Long id;
    private Long productId;
    private String firm;
    private String description;
}
