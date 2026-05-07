package edu.sjsu.cmpe172.starterdemo.mapper;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import edu.sjsu.cmpe172.starterdemo.model.User;

@Repository
public class UserMapper {

    private final JdbcTemplate jdbcTemplate;

    public UserMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> rowMapper = (rs, rowNum) -> new User(
        rs.getString("email"),
        rs.getString("name"),
        rs.getString("password"),
        rs.getDate("created_at").toLocalDate()
    );

    public int insert(User user) {
        String sql = "INSERT INTO `user` (email, name, password, created_at) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql, user.getEmail(), user.getName(), user.getPassword(), user.getCreated_at());
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM `user` WHERE email = ?";
        List<User> results = jdbcTemplate.query(sql, rowMapper, email);
        return results.stream().findFirst();
    }
}
