package com.productstore.dto;

import lombok.Builder;
import lombok.Data;
// То, что приходит от клиента
@Data
@Builder
public class CreateProductRequest {
    private String name;
}
