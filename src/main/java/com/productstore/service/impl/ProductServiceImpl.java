package com.productstore.service.impl;

import com.productstore.ProductMapper;
import com.productstore.dto.CreateProductRequest;
import com.productstore.dto.ProductResponse;
import com.productstore.entity.ProductEntity;
import com.productstore.entity.ProductMetadataEntity;
import com.productstore.repository.ProductMetadataRepository;
import com.productstore.repository.ProductRepository;
import com.productstore.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Правильно: в репозитории не должно быть логики,
 * для этого как раз есть сервис.
 * И в нем писать логику типа .orElseThrow(() -> new RuntimeException("Не найден"));
 * или if и так далее - правильно.
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;
    private final ProductMetadataRepository productMetadataRepository;

    @Override
    public ProductResponse getProductById(Long id) {
        ProductEntity product = productRepository.findProductById(id)
                .orElseThrow(() -> new RuntimeException("Не найден продукт: %d".formatted(id)));
        ProductMetadataEntity metaData = productMetadataRepository.findMetadataByProductId(id)
                .orElseThrow(() -> new RuntimeException("Метадата не найдена для продукта: %d".formatted(id)));

        return productMapper.toResponse(product, metaData);

    }

    @Override

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAllProducts()
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    public ProductResponse createProduct(CreateProductRequest request) {
        ProductEntity entity = productMapper.toEntity(request);

        productRepository.save(entity);

        return productMapper.toResponse(entity);
    }

    @Override
    public void deleteProductById(Long id) {
        ProductEntity product = productRepository.findProductById(id)
                .orElseThrow(() -> new RuntimeException("Продукт не найден: " + id));

        productRepository.deleteById(id);
    }

    @Override
    public ProductResponse updateProduct(Long id, CreateProductRequest request) {
        ProductEntity existing = productRepository.findProductById(id)
                .orElseThrow(() -> new RuntimeException("Продукт не найден"));

        existing.setName(request.getName());

        productRepository.update(existing);

        return productMapper.toResponse(existing);
    }

    @Override
    public List<ProductResponse> getProductsByName(String name) {
        return productRepository.findByName(name)
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }
}
