package edu.sjsu.cmpe172.starterdemo.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import edu.sjsu.cmpe172.starterdemo.model.Appointment;

@Repository
public class AppointmentMapper {

    private final JdbcTemplate jdbcTemplate;

    public AppointmentMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Appointment> rowMapper = new RowMapper<Appointment>() {
        @Override
        public Appointment mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Appointment(
                rs.getString("app_id"),
                rs.getString("server_id"),
                rs.getString("service_id"),
                rs.getString("email"),
                rs.getString("status"),
                rs.getDate("created_at").toLocalDate(),
                rs.getTimestamp("start_time").toLocalDateTime(),
                rs.getTimestamp("end_time").toLocalDateTime()
            );
        }
    };

    public int insert(Appointment appointment) {
        String sql = """
                INSERT INTO appointment (app_id, server_id, service_id, email, status, created_at, start_time, end_time)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        return jdbcTemplate.update(sql,
                appointment.getApp_id(),
                appointment.getServer_id(),
                appointment.getService_id(),
                appointment.getEmail(),
                appointment.getStatus(),
                appointment.getCreated_at(),
                Timestamp.valueOf(appointment.getStart_time()),
                Timestamp.valueOf(appointment.getEnd_time())
        );
    }

    public int update(Appointment appointment) {
        String sql = """
                UPDATE appointment
                SET server_id  = ?,
                    service_id = ?,
                    email      = ?,
                    status     = ?,
                    created_at = ?,
                    start_time = ?,
                    end_time   = ?
                WHERE app_id = ?
                """;
        return jdbcTemplate.update(sql,
                appointment.getServer_id(),
                appointment.getService_id(),
                appointment.getEmail(),
                appointment.getStatus(),
                appointment.getCreated_at(),
                Timestamp.valueOf(appointment.getStart_time()),
                Timestamp.valueOf(appointment.getEnd_time()),
                appointment.getApp_id()
        );
    }

    public int delete(String appId) {
        String sql = "DELETE FROM appointment WHERE app_id = ?";
        return jdbcTemplate.update(sql, appId);
    }

    public Optional<Appointment> findById(String appId) {
        String sql = "SELECT * FROM appointment WHERE app_id = ?";
        List<Appointment> results = jdbcTemplate.query(sql, rowMapper, appId);
        return results.stream().findFirst();
    }

    public List<Appointment> findAll() {
        String sql = "SELECT * FROM appointment";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<Appointment> findByEmail(String email) {
        String sql = "SELECT * FROM appointment WHERE email = ?";
        return jdbcTemplate.query(sql, rowMapper, email);
    }

    public List<Appointment> findByServerId(String serverId) {
        String sql = "SELECT * FROM appointment WHERE server_id = ?";
        return jdbcTemplate.query(sql, rowMapper, serverId);
    }
}
