package com.productstore.service;

import com.productstore.dto.CreateProductRequest;
import com.productstore.dto.CreateProductWithMetadataRequest;
import com.productstore.dto.ProductResponse;

import java.util.List;

public interface ProductService {


    ProductResponse getProductById(Long id);

    List<ProductResponse> getAllProducts();

    ProductResponse createProduct(CreateProductRequest request);

    void deleteProductById(Long id);

    ProductResponse updateProduct(Long id, CreateProductRequest request);

    List<ProductResponse> getProductsByName(String name);

    ProductResponse createProductWithMetadata(CreateProductWithMetadataRequest request);
}

