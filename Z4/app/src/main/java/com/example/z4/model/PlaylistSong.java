package com.example.z4.model;

public class PlaylistSong {
    private int id;
    private int playlistId;
    private int songId;

    public PlaylistSong() {
    }

    public PlaylistSong(int id, int playlistId, int songId) {
        this.id = id;
        this.playlistId = playlistId;
        this.songId = songId;
    }

    public PlaylistSong(int playlistId, int songId) {
        this.playlistId = playlistId;
        this.songId = songId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }
}