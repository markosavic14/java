package com.example.z4.model;

public class Playlist {
    private int id;
    private String name;
    private int userId;

    public Playlist() {
    }

    public Playlist(int id, String name, int userId) {
        this.id = id;
        this.name = name;
        this.userId = userId;
    }

    public Playlist(String name, int userId) {
        this.name = name;
        this.userId = userId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return name;
    }
}