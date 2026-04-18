package com.productstore.repository.impl;

import com.productstore.entity.ProductEntity;
import com.productstore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<ProductEntity> findProductById(Long id) {
        String sql = "SELECT * FROM product_store.products WHERE id = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(
                sql,
                getProductEntityRowMapper(),
                id
        ));
    }

    @Override
    public List<ProductEntity> findAllProducts() {
        String sql = "SELECT* FROM product_store.products";
        return jdbcTemplate.query(
                sql,
                getProductEntityRowMapper());
    }

    @Override
    public void save(ProductEntity product) {
        String sql = "INSERT INTO product_store.products (name) VALUES (?)";
        jdbcTemplate.update(sql,
                product.getName());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE from product_store.products WHERE id= ?";
        jdbcTemplate.update(sql,
                id);
    }

    @Override
    public void update(ProductEntity product) {
        String sql = "UPDATE  product_store.products SET name =? WHERE id= ?";
        jdbcTemplate.update(sql,
                product.getName(),
                product.getId());
    }

    @Override
    public List<ProductEntity> findByName(String name) {
        String sql = "SELECT * FROM product_store.products WHERE name= ?";
        return jdbcTemplate.query(sql,
                getProductEntityRowMapper(),
                name);
    }


    private static RowMapper<ProductEntity> getProductEntityRowMapper() {
        return (rs, rowNum) -> ProductEntity.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build();
    }
}
