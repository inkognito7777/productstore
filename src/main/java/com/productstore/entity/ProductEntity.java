package com.productstore.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductEntity {
    private Long id;
    private String name;
}
