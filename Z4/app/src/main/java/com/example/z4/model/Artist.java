package com.example.z4.model;

public class Artist {
    private int id;
    private String name;
    private int genreId;
    private String genreName; // For joined queries

    public Artist() {
    }

    public Artist(int id, String name, int genreId) {
        this.id = id;
        this.name = name;
        this.genreId = genreId;
    }

    public Artist(String name, int genreId) {
        this.name = name;
        this.genreId = genreId;
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

    public int getGenreId() {
        return genreId;
    }

    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    @Override
    public String toString() {
        return name;
    }
}