package com.example.z4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.z4.model.*;

import java.util.ArrayList;
import java.util.List;

public class SQLiteManager extends SQLiteOpenHelper {
    private static SQLiteManager sqLiteManager;
    private static final String DATABASE_NAME = "z4DB";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_GENRES = "genres";
    private static final String TABLE_ARTISTS = "artists";
    private static final String TABLE_SONGS = "songs";
    private static final String TABLE_PLAYLISTS = "playlists";
    private static final String TABLE_PLAYLIST_SONGS = "playlist_songs";

    // Users table columns
    private static final String USER_ID = "id";
    private static final String USER_NAME = "name";
    private static final String USER_PASSWORD = "password";

    // Genres table columns
    private static final String GENRE_ID = "id";
    private static final String GENRE_NAME = "name";

    // Artists table columns
    private static final String ARTIST_ID = "id";
    private static final String ARTIST_NAME = "name";
    private static final String ARTIST_GENRE_ID = "genre_id";

    // Songs table columns
    private static final String SONG_ID = "id";
    private static final String SONG_NAME = "name";
    private static final String SONG_GENRE_ID = "genre_id";
    private static final String SONG_ARTIST_ID = "artist_id";

    // Playlists table columns
    private static final String PLAYLIST_ID = "id";
    private static final String PLAYLIST_NAME = "name";
    private static final String PLAYLIST_USER_ID = "user_id";

    // Playlist Songs table columns
    private static final String PLAYLIST_SONG_ID = "id";
    private static final String PLAYLIST_SONG_PLAYLIST_ID = "playlist_id";
    private static final String PLAYLIST_SONG_SONG_ID = "song_id";

    public SQLiteManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static SQLiteManager instanceOfDatabase(Context context){
        if(sqLiteManager == null)
            sqLiteManager = new SQLiteManager(context);

        return sqLiteManager;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USER_NAME + " TEXT NOT NULL UNIQUE, " +
                USER_PASSWORD + " TEXT NOT NULL)";

        // Create Genres table
        String createGenresTable = "CREATE TABLE " + TABLE_GENRES + " (" +
                GENRE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                GENRE_NAME + " TEXT NOT NULL UNIQUE)";

        // Create Artists table
        String createArtistsTable = "CREATE TABLE " + TABLE_ARTISTS + " (" +
                ARTIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ARTIST_NAME + " TEXT NOT NULL, " +
                ARTIST_GENRE_ID + " INTEGER, " +
                "FOREIGN KEY(" + ARTIST_GENRE_ID + ") REFERENCES " + TABLE_GENRES + "(" + GENRE_ID + "))";

        // Create Songs table
        String createSongsTable = "CREATE TABLE " + TABLE_SONGS + " (" +
                SONG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SONG_NAME + " TEXT NOT NULL, " +
                SONG_GENRE_ID + " INTEGER, " +
                SONG_ARTIST_ID + " INTEGER, " +
                "FOREIGN KEY(" + SONG_GENRE_ID + ") REFERENCES " + TABLE_GENRES + "(" + GENRE_ID + "), " +
                "FOREIGN KEY(" + SONG_ARTIST_ID + ") REFERENCES " + TABLE_ARTISTS + "(" + ARTIST_ID + "))";

        // Create Playlists table
        String createPlaylistsTable = "CREATE TABLE " + TABLE_PLAYLISTS + " (" +
                PLAYLIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PLAYLIST_NAME + " TEXT NOT NULL, " +
                PLAYLIST_USER_ID + " INTEGER, " +
                "FOREIGN KEY(" + PLAYLIST_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + USER_ID + "))";

        // Create Playlist Songs table
        String createPlaylistSongsTable = "CREATE TABLE " + TABLE_PLAYLIST_SONGS + " (" +
                PLAYLIST_SONG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PLAYLIST_SONG_PLAYLIST_ID + " INTEGER, " +
                PLAYLIST_SONG_SONG_ID + " INTEGER, " +
                "FOREIGN KEY(" + PLAYLIST_SONG_PLAYLIST_ID + ") REFERENCES " + TABLE_PLAYLISTS + "(" + PLAYLIST_ID + "), " +
                "FOREIGN KEY(" + PLAYLIST_SONG_SONG_ID + ") REFERENCES " + TABLE_SONGS + "(" + SONG_ID + "))";

        db.execSQL(createUsersTable);
        db.execSQL(createGenresTable);
        db.execSQL(createArtistsTable);
        db.execSQL(createSongsTable);
        db.execSQL(createPlaylistsTable);
        db.execSQL(createPlaylistSongsTable);

        // Insert some initial data
        insertInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST_SONGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLISTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTISTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GENRES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    private void insertInitialData(SQLiteDatabase db) {
        // Insert some sample genres
        ContentValues values = new ContentValues();
        values.put(GENRE_NAME, "Pop");
        db.insert(TABLE_GENRES, null, values);

        values.clear();
        values.put(GENRE_NAME, "Rock");
        db.insert(TABLE_GENRES, null, values);

        values.clear();
        values.put(GENRE_NAME, "Hip Hop");
        db.insert(TABLE_GENRES, null, values);

        values.clear();
        values.put(GENRE_NAME, "Electronic");
        db.insert(TABLE_GENRES, null, values);
    }

    // User methods
    public long addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_NAME, user.getName());
        values.put(USER_PASSWORD, user.getPassword());
        return db.insert(TABLE_USERS, null, values);
    }

    public User getUserByCredentials(String name, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {USER_ID, USER_NAME, USER_PASSWORD};
        String selection = USER_NAME + " = ? AND " + USER_PASSWORD + " = ?";
        String[] selectionArgs = {name, password};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(USER_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(USER_PASSWORD))
            );
        }
        cursor.close();
        return user;
    }

    public User getUserByUsername(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {USER_ID, USER_NAME, USER_PASSWORD};
        String selection = USER_NAME + " = ?";
        String[] selectionArgs = {name};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(USER_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(USER_PASSWORD))
            );
        }
        cursor.close();
        return user;
    }

    // Genre methods
    public long addGenre(Genre genre) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(GENRE_NAME, genre.getName());
        return db.insert(TABLE_GENRES, null, values);
    }

    public List<Genre> getAllGenres() {
        List<Genre> genres = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_GENRES, null, null, null, null, null, GENRE_NAME);

        while (cursor.moveToNext()) {
            Genre genre = new Genre(
                    cursor.getInt(cursor.getColumnIndexOrThrow(GENRE_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(GENRE_NAME))
            );
            genres.add(genre);
        }
        cursor.close();
        return genres;
    }

    public int updateGenre(Genre genre) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(GENRE_NAME, genre.getName());
        return db.update(TABLE_GENRES, values, GENRE_ID + " = ?", new String[]{String.valueOf(genre.getId())});
    }

    public int deleteGenre(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_GENRES, GENRE_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // Artist methods
    public long addArtist(Artist artist) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ARTIST_NAME, artist.getName());
        values.put(ARTIST_GENRE_ID, artist.getGenreId());
        return db.insert(TABLE_ARTISTS, null, values);
    }

    public List<Artist> getAllArtists() {
        List<Artist> artists = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT a." + ARTIST_ID + ", a." + ARTIST_NAME + ", a." + ARTIST_GENRE_ID + ", g." + GENRE_NAME + 
                       " FROM " + TABLE_ARTISTS + " a LEFT JOIN " + TABLE_GENRES + " g ON a." + ARTIST_GENRE_ID + " = g." + GENRE_ID +
                       " ORDER BY a." + ARTIST_NAME;
        
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            Artist artist = new Artist(
                    cursor.getInt(cursor.getColumnIndexOrThrow(ARTIST_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ARTIST_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(ARTIST_GENRE_ID))
            );
            artist.setGenreName(cursor.getString(cursor.getColumnIndexOrThrow(GENRE_NAME)));
            artists.add(artist);
        }
        cursor.close();
        return artists;
    }

    public List<Artist> getArtistsByGenre(int genreId) {
        List<Artist> artists = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT a." + ARTIST_ID + ", a." + ARTIST_NAME + ", a." + ARTIST_GENRE_ID + ", g." + GENRE_NAME + 
                       " FROM " + TABLE_ARTISTS + " a LEFT JOIN " + TABLE_GENRES + " g ON a." + ARTIST_GENRE_ID + " = g." + GENRE_ID +
                       " WHERE a." + ARTIST_GENRE_ID + " = ? ORDER BY a." + ARTIST_NAME;
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(genreId)});

        while (cursor.moveToNext()) {
            Artist artist = new Artist(
                    cursor.getInt(cursor.getColumnIndexOrThrow(ARTIST_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ARTIST_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(ARTIST_GENRE_ID))
            );
            artist.setGenreName(cursor.getString(cursor.getColumnIndexOrThrow(GENRE_NAME)));
            artists.add(artist);
        }
        cursor.close();
        return artists;
    }

    public int updateArtist(Artist artist) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ARTIST_NAME, artist.getName());
        values.put(ARTIST_GENRE_ID, artist.getGenreId());
        return db.update(TABLE_ARTISTS, values, ARTIST_ID + " = ?", new String[]{String.valueOf(artist.getId())});
    }

    public int deleteArtist(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_ARTISTS, ARTIST_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // Song methods
    public long addSong(Song song) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SONG_NAME, song.getName());
        values.put(SONG_GENRE_ID, song.getGenreId());
        values.put(SONG_ARTIST_ID, song.getArtistId());
        return db.insert(TABLE_SONGS, null, values);
    }

    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT s." + SONG_ID + ", s." + SONG_NAME + ", s." + SONG_GENRE_ID + ", s." + SONG_ARTIST_ID + 
                       ", g." + GENRE_NAME + ", a." + ARTIST_NAME + 
                       " FROM " + TABLE_SONGS + " s" +
                       " LEFT JOIN " + TABLE_GENRES + " g ON s." + SONG_GENRE_ID + " = g." + GENRE_ID +
                       " LEFT JOIN " + TABLE_ARTISTS + " a ON s." + SONG_ARTIST_ID + " = a." + ARTIST_ID +
                       " ORDER BY s." + SONG_NAME;
        
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            Song song = new Song(
                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(SONG_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_GENRE_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_ARTIST_ID))
            );
            song.setGenreName(cursor.getString(cursor.getColumnIndexOrThrow(GENRE_NAME)));
            song.setArtistName(cursor.getString(cursor.getColumnIndexOrThrow(ARTIST_NAME)));
            songs.add(song);
        }
        cursor.close();
        return songs;
    }

    public List<Song> getSongsByGenre(int genreId) {
        List<Song> songs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT s." + SONG_ID + ", s." + SONG_NAME + ", s." + SONG_GENRE_ID + ", s." + SONG_ARTIST_ID + 
                       ", g." + GENRE_NAME + ", a." + ARTIST_NAME + 
                       " FROM " + TABLE_SONGS + " s" +
                       " LEFT JOIN " + TABLE_GENRES + " g ON s." + SONG_GENRE_ID + " = g." + GENRE_ID +
                       " LEFT JOIN " + TABLE_ARTISTS + " a ON s." + SONG_ARTIST_ID + " = a." + ARTIST_ID +
                       " WHERE s." + SONG_GENRE_ID + " = ? ORDER BY s." + SONG_NAME;
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(genreId)});

        while (cursor.moveToNext()) {
            Song song = new Song(
                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(SONG_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_GENRE_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_ARTIST_ID))
            );
            song.setGenreName(cursor.getString(cursor.getColumnIndexOrThrow(GENRE_NAME)));
            song.setArtistName(cursor.getString(cursor.getColumnIndexOrThrow(ARTIST_NAME)));
            songs.add(song);
        }
        cursor.close();
        return songs;
    }

    public List<Song> getSongsByArtist(int artistId) {
        List<Song> songs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT s." + SONG_ID + ", s." + SONG_NAME + ", s." + SONG_GENRE_ID + ", s." + SONG_ARTIST_ID + 
                       ", g." + GENRE_NAME + ", a." + ARTIST_NAME + 
                       " FROM " + TABLE_SONGS + " s" +
                       " LEFT JOIN " + TABLE_GENRES + " g ON s." + SONG_GENRE_ID + " = g." + GENRE_ID +
                       " LEFT JOIN " + TABLE_ARTISTS + " a ON s." + SONG_ARTIST_ID + " = a." + ARTIST_ID +
                       " WHERE s." + SONG_ARTIST_ID + " = ? ORDER BY s." + SONG_NAME;
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(artistId)});

        while (cursor.moveToNext()) {
            Song song = new Song(
                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(SONG_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_GENRE_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_ARTIST_ID))
            );
            song.setGenreName(cursor.getString(cursor.getColumnIndexOrThrow(GENRE_NAME)));
            song.setArtistName(cursor.getString(cursor.getColumnIndexOrThrow(ARTIST_NAME)));
            songs.add(song);
        }
        cursor.close();
        return songs;
    }

    public int updateSong(Song song) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SONG_NAME, song.getName());
        values.put(SONG_GENRE_ID, song.getGenreId());
        values.put(SONG_ARTIST_ID, song.getArtistId());
        return db.update(TABLE_SONGS, values, SONG_ID + " = ?", new String[]{String.valueOf(song.getId())});
    }

    public int deleteSong(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_SONGS, SONG_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // Playlist methods
    public long addPlaylist(Playlist playlist) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PLAYLIST_NAME, playlist.getName());
        values.put(PLAYLIST_USER_ID, playlist.getUserId());
        return db.insert(TABLE_PLAYLISTS, null, values);
    }

    public List<Playlist> getPlaylistsByUser(int userId) {
        List<Playlist> playlists = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selection = PLAYLIST_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        
        Cursor cursor = db.query(TABLE_PLAYLISTS, null, selection, selectionArgs, null, null, PLAYLIST_NAME);

        while (cursor.moveToNext()) {
            Playlist playlist = new Playlist(
                    cursor.getInt(cursor.getColumnIndexOrThrow(PLAYLIST_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PLAYLIST_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(PLAYLIST_USER_ID))
            );
            playlists.add(playlist);
        }
        cursor.close();
        return playlists;
    }

    public int updatePlaylist(Playlist playlist) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PLAYLIST_NAME, playlist.getName());
        return db.update(TABLE_PLAYLISTS, values, PLAYLIST_ID + " = ?", new String[]{String.valueOf(playlist.getId())});
    }

    public int deletePlaylist(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // First delete all playlist songs
        db.delete(TABLE_PLAYLIST_SONGS, PLAYLIST_SONG_PLAYLIST_ID + " = ?", new String[]{String.valueOf(id)});
        // Then delete the playlist
        return db.delete(TABLE_PLAYLISTS, PLAYLIST_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // Playlist Song methods
    public long addSongToPlaylist(int playlistId, int songId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PLAYLIST_SONG_PLAYLIST_ID, playlistId);
        values.put(PLAYLIST_SONG_SONG_ID, songId);
        return db.insert(TABLE_PLAYLIST_SONGS, null, values);
    }

    public List<Song> getSongsInPlaylist(int playlistId) {
        List<Song> songs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT s." + SONG_ID + ", s." + SONG_NAME + ", s." + SONG_GENRE_ID + ", s." + SONG_ARTIST_ID + 
                       ", g." + GENRE_NAME + ", a." + ARTIST_NAME + 
                       " FROM " + TABLE_PLAYLIST_SONGS + " ps" +
                       " JOIN " + TABLE_SONGS + " s ON ps." + PLAYLIST_SONG_SONG_ID + " = s." + SONG_ID +
                       " LEFT JOIN " + TABLE_GENRES + " g ON s." + SONG_GENRE_ID + " = g." + GENRE_ID +
                       " LEFT JOIN " + TABLE_ARTISTS + " a ON s." + SONG_ARTIST_ID + " = a." + ARTIST_ID +
                       " WHERE ps." + PLAYLIST_SONG_PLAYLIST_ID + " = ? ORDER BY s." + SONG_NAME;
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(playlistId)});

        while (cursor.moveToNext()) {
            Song song = new Song(
                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(SONG_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_GENRE_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_ARTIST_ID))
            );
            song.setGenreName(cursor.getString(cursor.getColumnIndexOrThrow(GENRE_NAME)));
            song.setArtistName(cursor.getString(cursor.getColumnIndexOrThrow(ARTIST_NAME)));
            songs.add(song);
        }
        cursor.close();
        return songs;
    }

    public int removeSongFromPlaylist(int playlistId, int songId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = PLAYLIST_SONG_PLAYLIST_ID + " = ? AND " + PLAYLIST_SONG_SONG_ID + " = ?";
        String[] selectionArgs = {String.valueOf(playlistId), String.valueOf(songId)};
        return db.delete(TABLE_PLAYLIST_SONGS, selection, selectionArgs);
    }
}
