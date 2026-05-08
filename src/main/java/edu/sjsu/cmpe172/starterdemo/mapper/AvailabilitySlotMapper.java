package edu.sjsu.cmpe172.starterdemo.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.time.LocalDate;

import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import edu.sjsu.cmpe172.starterdemo.model.Availability_Slot;

@Repository
public class AvailabilitySlotMapper {

    private final JdbcTemplate jdbcTemplate;

    public AvailabilitySlotMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Availability_Slot> rowMapper = new RowMapper<>() {
        @Override
        public Availability_Slot mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Availability_Slot(
                rs.getString("server_id"),
                rs.getDate("date").toLocalDate(),
                rs.getBoolean("status"),
                rs.getTimestamp("start_time").toLocalDateTime(),
                rs.getTimestamp("end_time").toLocalDateTime()
            );
        }
    };

    public int insert(Availability_Slot slot) {
        String sql = """
                INSERT INTO availability_slot (server_id, date, status, start_time, end_time)
                VALUES (?, ?, ?, ?, ?)
                """;
        return jdbcTemplate.update(sql,
                slot.getServer_id(),
                slot.getDate(),
                slot.getStatus(),
                Timestamp.valueOf(slot.getStart_time()),
                Timestamp.valueOf(slot.getEnd_time())
        );
    }

    /**
     * Optimistic concurrency: only updates a row if status is still TRUE (available).
     * Returns 0 if the slot was already booked by someone else.
     */
    public int update(Availability_Slot slot, Boolean status) {
        String sql = """
                UPDATE availability_slot
                SET status   = ?
                WHERE server_id  = ?
                  AND date       = ?
                  AND start_time = ?
                  AND status     = TRUE
                """;
        return jdbcTemplate.update(sql,
                status,
                slot.getServer_id(),
                slot.getDate(),
                Timestamp.valueOf(slot.getStart_time())
        );
    }

    public int delete(String serverId, LocalDate date, java.time.LocalDateTime startTime) {
        String sql = """
                DELETE FROM availability_slot
                WHERE server_id  = ?
                  AND date       = ?
                  AND start_time = ?
                """;
        return jdbcTemplate.update(sql, serverId, date, Timestamp.valueOf(startTime));
    }

    public int deleteAllForServer(String serverId) {
        String sql = "DELETE FROM availability_slot WHERE server_id = ?";
        return jdbcTemplate.update(sql, serverId);
    }

    public List<Availability_Slot> findByServerId(String serverId) {
        String sql = "SELECT * FROM availability_slot WHERE server_id = ?";
        return jdbcTemplate.query(sql, rowMapper, serverId);
    }

    public List<Availability_Slot> findAllAvailable() {
        String sql = "SELECT * FROM availability_slot WHERE status = TRUE AND start_time > NOW()";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<Availability_Slot> findAll() {
        String sql = "SELECT * FROM availability_slot";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public boolean isSlotInFuture(String serverId, java.time.LocalDateTime startTime) {
        String sql = "SELECT COUNT(*) FROM availability_slot WHERE server_id = ? AND start_time = ? AND start_time > NOW()";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, serverId, Timestamp.valueOf(startTime));
        return count != null && count > 0;
    }

    public Availability_Slot findAppointment(String serverId, LocalDate date, java.time.LocalDateTime startTime) {
        String sql = "SELECT * FROM availability_slot WHERE server_id = ? AND date = ? AND start_time = ?";
        return jdbcTemplate.queryForObject(sql, rowMapper, serverId, date, Timestamp.valueOf(startTime));
    }

    public int releaseSlot(String serverId, LocalDate date, java.time.LocalDateTime startTime) {
        String sql = "UPDATE availability_slot SET status = TRUE WHERE server_id = ? AND date = ? AND start_time = ?";
        return jdbcTemplate.update(sql, serverId, date, Timestamp.valueOf(startTime));
    }

    // Removes expired slots that have no referencing appointments (guards against FK violation).
    public int deleteExpiredAvailable() {
        String sql = """
                DELETE FROM availability_slot
                WHERE end_time < NOW()
                AND NOT EXISTS (
                    SELECT 1 FROM appointment
                    WHERE appointment.server_id  = availability_slot.server_id
                      AND appointment.start_time = availability_slot.start_time
                )
                """;
        return jdbcTemplate.update(sql);
    }
}
