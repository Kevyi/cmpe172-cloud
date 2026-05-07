package edu.sjsu.cmpe172.starterdemo.mapper;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import edu.sjsu.cmpe172.starterdemo.model.Server;

@Repository
public class ServerMapper {

    private final JdbcTemplate jdbcTemplate;

    public ServerMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Server> rowMapper = (rs, rowNum) -> new Server(
        rs.getString("server_id"),
        rs.getString("name"),
        rs.getString("ip_address"),
        rs.getString("status")
    );

    public List<Server> findAll() {
        return jdbcTemplate.query("SELECT * FROM server", rowMapper);
    }

    public Optional<Server> findById(String serverId) {
        String sql = "SELECT * FROM server WHERE server_id = ?";
        List<Server> results = jdbcTemplate.query(sql, rowMapper, serverId);
        return results.stream().findFirst();
    }

    public int insert(Server server) {
        String sql = "INSERT INTO server (server_id, name, ip_address, status) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql, server.getServer_id(), server.getName(), server.getIp_address(), server.getStatus());
    }

    public int updateStatus(String serverId, String status) {
        String sql = "UPDATE server SET status = ? WHERE server_id = ?";
        return jdbcTemplate.update(sql, status, serverId);
    }
}
