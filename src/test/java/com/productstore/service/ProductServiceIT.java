package com.productstore.service;

import com.productstore.dto.CreateProductRequest;
import com.productstore.dto.CreateProductWithMetadataRequest;
import com.productstore.dto.ProductResponse;
import com.productstore.entity.ProductEntity;
import com.productstore.entity.ProductMetadataEntity;
import com.productstore.repository.ProductMetadataRepository;
import com.productstore.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Transactional
public class ProductServiceIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("product_store")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMetadataRepository productMetadataRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DROP SCHEMA IF EXISTS product_store CASCADE");
        jdbcTemplate.execute("CREATE SCHEMA product_store");
        jdbcTemplate.execute("""
                CREATE TABLE product_store.products (
                    id BIGSERIAL PRIMARY KEY,
                    name VARCHAR(255) NOT NULL
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE product_store.product_metadata (
                    id BIGSERIAL PRIMARY KEY,
                    product_id BIGINT NOT NULL REFERENCES product_store.products(id) ON DELETE CASCADE,
                    firm VARCHAR(255),
                    description TEXT
                )
                """);
    }

    @Test
    void createProduct_ShouldSaveAndReturnProduct() {
        CreateProductRequest request = CreateProductRequest.builder()
                .name("Test Product")
                .build();

        ProductResponse response = productService.createProduct(request);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getName()).isEqualTo("Test Product");
        assertThat(response.getFirm()).isNull();
        assertThat(response.getDescription()).isNull();

        ProductEntity saved = productRepository.findProductById(response.getId()).orElseThrow();
        assertThat(saved.getName()).isEqualTo("Test Product");
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProductWithMetadata() {
        ProductEntity product = ProductEntity.builder().name("Laptop").build();
        Long productId = productRepository.save(product);

        ProductMetadataEntity metadata = ProductMetadataEntity.builder()
                .productId(productId)
                .firm("Apple")
                .description("MacBook Pro")
                .build();
        productMetadataRepository.save(metadata);

        ProductResponse response = productService.getProductById(productId);

        assertThat(response.getId()).isEqualTo(productId);
        assertThat(response.getName()).isEqualTo("Laptop");
        assertThat(response.getFirm()).isEqualTo("Apple");
        assertThat(response.getDescription()).isEqualTo("MacBook Pro");
    }

    @Test
    void getProductById_WhenProductNotFound_ShouldThrowException() {
        assertThatThrownBy(() -> productService.getProductById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Не найден продукт: 999");
    }

    @Test
    void getProductById_WhenMetadataNotFound_ShouldThrowException() {
        ProductEntity product = ProductEntity.builder().name("Orphan Product").build();
        Long productId = productRepository.save(product);

        assertThatThrownBy(() -> productService.getProductById(productId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Метадата не найдена для продукта: " + productId);
    }

    @Test
    void getAllProducts_ShouldReturnAllProductsWithMetadata() {
        ProductEntity product1 = ProductEntity.builder().name("Product A").build();
        ProductEntity product2 = ProductEntity.builder().name("Product B").build();
        Long id1 = productRepository.save(product1);
        Long id2 = productRepository.save(product2);

        ProductMetadataEntity meta1 = ProductMetadataEntity.builder()
                .productId(id1).firm("Firm A").description("Desc A").build();
        ProductMetadataEntity meta2 = ProductMetadataEntity.builder()
                .productId(id2).firm("Firm B").description("Desc B").build();
        productMetadataRepository.save(meta1);
        productMetadataRepository.save(meta2);

        List<ProductResponse> responses = productService.getAllProducts();

        assertThat(responses).hasSize(2);
        assertThat(responses).extracting(ProductResponse::getName)
                .containsExactlyInAnyOrder("Product A", "Product B");
        assertThat(responses).extracting(ProductResponse::getFirm)
                .containsExactlyInAnyOrder("Firm A", "Firm B");
    }

    @Test
    void updateProduct_ShouldUpdateName() {
        ProductEntity product = ProductEntity.builder().name("Old Name").build();
        Long id = productRepository.save(product);

        CreateProductRequest updateRequest = CreateProductRequest.builder()
                .name("New Name")
                .build();

        ProductResponse response = productService.updateProduct(id, updateRequest);

        assertThat(response.getName()).isEqualTo("New Name");

        ProductEntity updated = productRepository.findProductById(id).orElseThrow();
        assertThat(updated.getName()).isEqualTo("New Name");
    }

    @Test
    void deleteProductById_ShouldRemoveProduct() {
        ProductEntity product = ProductEntity.builder().name("To Delete").build();
        Long id = productRepository.save(product);

        productService.deleteProductById(id);

        assertThat(productRepository.findProductById(id)).isEmpty();
    }

    @Test
    void getProductsByName_ShouldReturnMatchingProducts() {
        productRepository.save(ProductEntity.builder().name("Apple MacBook").build());
        productRepository.save(ProductEntity.builder().name("Apple iPhone").build());
        productRepository.save(ProductEntity.builder().name("Samsung Galaxy").build());

        List<ProductResponse> responses = productService.getProductsByName("Apple MacBook");

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getName()).isEqualTo("Apple MacBook");
    }

    @Test
    void createProductWithMetadata_ShouldSaveBothInTransaction() {
        // Given
        CreateProductWithMetadataRequest request = new CreateProductWithMetadataRequest();
        request.setName("Premium Laptop");
        request.setFirm("Dell");
        request.setDescription("XPS 15");

        // When
        ProductResponse response = productService.createProductWithMetadata(request);

        // Then
        assertThat(response.getId()).isNotNull();
        assertThat(response.getName()).isEqualTo("Premium Laptop");
        assertThat(response.getFirm()).isEqualTo("Dell");
        assertThat(response.getDescription()).isEqualTo("XPS 15");

        // Verify both records exist
        ProductEntity savedProduct = productRepository.findProductById(response.getId()).orElseThrow();
        ProductMetadataEntity savedMetadata = productMetadataRepository.findMetadataByProductId(response.getId()).orElseThrow();

        assertThat(savedProduct.getName()).isEqualTo("Premium Laptop");
        assertThat(savedMetadata.getFirm()).isEqualTo("Dell");
        assertThat(savedMetadata.getDescription()).isEqualTo("XPS 15");
    }
}