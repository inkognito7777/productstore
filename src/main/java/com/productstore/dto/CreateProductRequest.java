package com.productstore.dto;

import lombok.Data;
// То, что приходит от клиента
@Data
public class CreateProductRequest {
    private String name;
}
