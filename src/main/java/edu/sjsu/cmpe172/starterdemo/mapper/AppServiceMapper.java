package edu.sjsu.cmpe172.starterdemo.mapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
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

    public int insert(AppService svc) {
        String sql = "INSERT INTO app_service (name, description, docker_image) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, svc.getName());
            ps.setString(2, svc.getDescription());
            ps.setString(3, svc.getDocker_image());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }
}
