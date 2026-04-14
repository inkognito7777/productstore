package com.productstore.repository.impl;

import com.productstore.entity.ProductEntity;
import com.productstore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<ProductEntity> findProductById(Long id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        return Optional.of(jdbcTemplate.queryForObject(
                sql,
                getProductEntityRowMapper(),
                id
        ));
    }

    private static RowMapper<ProductEntity> getProductEntityRowMapper() {
        return (rs, rowNum) -> ProductEntity.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build();
    }
}
