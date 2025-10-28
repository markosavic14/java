package com.example.z4.model;

public class Song {
    private int id;
    private String name;
    private int genreId;
    private int artistId;
    private String genreName; // For joined queries
    private String artistName; // For joined queries

    public Song() {
    }

    public Song(int id, String name, int genreId, int artistId) {
        this.id = id;
        this.name = name;
        this.genreId = genreId;
        this.artistId = artistId;
    }

    public Song(String name, int genreId, int artistId) {
        this.name = name;
        this.genreId = genreId;
        this.artistId = artistId;
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

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    @Override
    public String toString() {
        return name + " - " + artistName;
    }
}