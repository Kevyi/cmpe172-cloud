package edu.sjsu.cmpe172.starterdemo.mapper;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import edu.sjsu.cmpe172.starterdemo.model.Admin;

@Repository
public class AdminMapper {

    private final JdbcTemplate jdbcTemplate;

    public AdminMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Admin> rowMapper = (rs, rowNum) -> new Admin(
        rs.getString("email"),
        rs.getString("name"),
        rs.getString("password")
    );

    public Optional<Admin> findByEmail(String email) {
        String sql = "SELECT * FROM admin WHERE email = ?";
        List<Admin> results = jdbcTemplate.query(sql, rowMapper, email);
        return results.stream().findFirst();
    }
}
