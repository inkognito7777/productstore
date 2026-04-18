package com.productstore.repository.impl;

import com.productstore.entity.ProductMetadataEntity;
import com.productstore.repository.ProductMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductMetadataRepositoryImpl implements ProductMetadataRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<ProductMetadataEntity> findMetadataByProductId(Long productId) {
        String sql = "SELECT* from product_metadata WHERE product_id=?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper(), productId));
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
