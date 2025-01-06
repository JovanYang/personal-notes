package com.notes.model;

import java.sql.Timestamp;

public class User {
    private int id;
    private String username;
    private String password;
    private Timestamp createdAt;

    // 构造函数
    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter 和 Setter 方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
} 