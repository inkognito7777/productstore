package com.productstore.repository.impl;

import com.productstore.entity.ProductMetadataEntity;
import com.productstore.repository.ProductMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductMetadataRepositoryImpl implements ProductMetadataRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<ProductMetadataEntity> findMetadataByProductId(Long productId) {
        String sql = "SELECT * FROM product_store.product_metadata WHERE product_id = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper(), productId));
    }

    @Override
    public List<ProductMetadataEntity> findMetadataByProductIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        String inSql = String.join(",", ids.stream().map(id -> "?").toList());
        String sql = "SELECT * FROM product_store.product_metadata WHERE product_id IN (" + inSql + ")";
        return jdbcTemplate.query(sql, rowMapper(), ids.toArray());
    }

    @Override
    public Long save(ProductMetadataEntity entity) {
        String sql = "INSERT INTO product_store.product_metadata (product_id, firm, description) VALUES (?, ?, ?) RETURNING id";
        Long id = jdbcTemplate.queryForObject(sql, Long.class,
                entity.getProductId(),
                entity.getFirm(),
                entity.getDescription());
        entity.setId(id);
        return id;
    }

    private static RowMapper<ProductMetadataEntity> rowMapper() {
        return (rs, rowNum) -> ProductMetadataEntity.builder()
                .id(rs.getLong("id"))
                .productId(rs.getLong("product_id"))
                .firm(rs.getString("firm"))
                .description(rs.getString("description"))
                .build();
    }
}