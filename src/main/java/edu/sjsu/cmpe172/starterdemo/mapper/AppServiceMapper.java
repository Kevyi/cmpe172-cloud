package edu.sjsu.cmpe172.starterdemo.mapper;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import edu.sjsu.cmpe172.starterdemo.model.AppService;

@Repository
public class AppServiceMapper {

    private final JdbcTemplate jdbcTemplate;

    public AppServiceMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<AppService> rowMapper = (rs, rowNum) -> new AppService(
        rs.getInt("service_id"),
        rs.getString("name"),
        rs.getString("description"),
        rs.getString("docker_image")
    );

    public List<AppService> findAll() {
        return jdbcTemplate.query("SELECT * FROM app_service", rowMapper);
    }

    public Optional<AppService> findById(int serviceId) {
        String sql = "SELECT * FROM app_service WHERE service_id = ?";
        List<AppService> results = jdbcTemplate.query(sql, rowMapper, serviceId);
        return results.stream().findFirst();
    }
}
