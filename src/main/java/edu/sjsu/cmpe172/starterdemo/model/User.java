package edu.sjsu.cmpe172.starterdemo.model;

import java.time.LocalDate;

public class User {
    private String email;
    private String name;
    private String password;
    private LocalDate created_at;

    public User() {}

    public User(String email, String name, String password, LocalDate created_at) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.created_at = created_at;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LocalDate getCreated_at() { return created_at; }
    public void setCreated_at(LocalDate created_at) { this.created_at = created_at; }
}
