package com.productstore.dto;

import lombok.Builder;
import lombok.Data;

// То, что даем клиенту
@Data
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String firm;
    private String description;
}
