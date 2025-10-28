package com.example.z4;package com.example.z4;package com.example.z4;



import android.content.ContentValues;

import android.content.Context;

import android.database.Cursor;import android.content.ContentValues;import android.content.ContentValues;

import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteOpenHelper;import android.content.Context;import android.content.Context;



import androidx.annotation.Nullable;import android.database.Cursor;import android.database.Cursor;



import com.example.z4.model.Artist;import android.database.sqlite.SQLiteDatabase;import android.database.sqlite.SQLiteDatabase;

import com.example.z4.model.Genre;

import com.example.z4.model.Playlist;import android.database.sqlite.SQLiteOpenHelper;import android.database.sqlite.SQLiteOpenHelper;

import com.example.z4.model.Song;

import com.example.z4.model.User;



import java.util.ArrayList;import androidx.annotation.NonNull;import androidx.annotation.NonNull;

import java.util.List;

import androidx.annotation.Nullable;import androidx.annotation.Nullable;

public class SQLiteManager extends SQLiteOpenHelper {



    private static SQLiteManager sqLiteManager;

import com.example.z4.model.*;import com.example.z4.model.*;

    // Database information

    private static final String DATABASE_NAME = "MusicLibraryDB";

    private static final int DATABASE_VERSION = 1;

import java.util.ArrayList;import java.util.ArrayList;

    // Table Names

    private static final String TABLE_USERS = "users";import java.util.List;import java.util.List;

    private static final String TABLE_GENRES = "genres";

    private static final String TABLE_ARTISTS = "artists";

    private static final String TABLE_SONGS = "songs";

    private static final String TABLE_PLAYLISTS = "playlists";public class SQLiteManager extends SQLiteOpenHelper {public class SQLiteManager extends SQLiteOpenHelper {

    private static final String TABLE_PLAYLIST_SONGS = "playlist_songs";

    private static SQLiteManager sqLiteManager;    private static SQLiteManager sqLiteManager;

    // Users Table Columns

    private static final String USER_ID = "id";    private static final String DATABASE_NAME = "z4DB";    private static final String DATABASE_NAME = "z4DB";

    private static final String USER_NAME = "name";

    private static final String USER_PASSWORD = "password";    private static final int DATABASE_VERSION = 1;    private static final int DATABASE_VERSION = 1;



    // Genres Table Columns

    private static final String GENRE_ID = "id";

    private static final String GENRE_NAME = "name";    // Table names    // Table names



    // Artists Table Columns    private static final String TABLE_USERS = "users";    private static final String TABLE_USERS = "users";

    private static final String ARTIST_ID = "id";

    private static final String ARTIST_NAME = "name";    private static final String TABLE_GENRES = "genres";    private static final String TABLE_GENRES = "genres";



    // Songs Table Columns    private static final String TABLE_ARTISTS = "artists";    private static final String TABLE_ARTISTS = "artists";

    private static final String SONG_ID = "id";

    private static final String SONG_NAME = "name";    private static final String TABLE_SONGS = "songs";    private static final String TABLE_SONGS = "songs";

    private static final String SONG_GENRE_ID = "genre_id";

    private static final String SONG_ARTIST_ID = "artist_id";    private static final String TABLE_PLAYLISTS = "playlists";    private static final String TABLE_PLAYLISTS = "playlists";



    // Playlists Table Columns    private static final String TABLE_PLAYLIST_SONGS = "playlist_songs";    private static final String TABLE_PLAYLIST_SONGS = "playlist_songs";

    private static final String PLAYLIST_ID = "id";

    private static final String PLAYLIST_NAME = "name";

    private static final String PLAYLIST_USER_ID = "user_id";

    // Users table columns    // Users table columns

    // Playlist_Songs Table Columns

    private static final String PLAYLIST_SONG_ID = "id";    private static final String USER_ID = "id";    private static final String USER_ID = "id";

    private static final String PLAYLIST_SONG_PLAYLIST_ID = "playlist_id";

    private static final String PLAYLIST_SONG_SONG_ID = "song_id";    private static final String USER_NAME = "name";    private static final String USER_NAME = "name";



    public SQLiteManager(@Nullable Context context) {    private static final String USER_PASSWORD = "password";    private static final String USER_PASSWORD = "password";

        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }



    public static SQLiteManager instanceOfDatabase(Context context) {    // Genres table columns    // Genres table columns

        if (sqLiteManager == null)

            sqLiteManager = new SQLiteManager(context);    private static final String GENRE_ID = "id";    private static final String GENRE_ID = "id";



        return sqLiteManager;    private static final String GENRE_NAME = "name";    private static final String GENRE_NAME = "name";

    }



    @Override

    public void onCreate(SQLiteDatabase db) {    // Artists table columns    // Artists table columns

        StringBuilder sql;

    private static final String ARTIST_ID = "id";    private static final String ARTIST_ID = "id";

        // Create Users table

        sql = new StringBuilder()    private static final String ARTIST_NAME = "name";    private static final String ARTIST_NAME = "name";

                .append("CREATE TABLE ")

                .append(TABLE_USERS)    private static final String ARTIST_GENRE_ID = "genre_id";    private static final String ARTIST_GENRE_ID = "genre_id";

                .append("(")

                .append(USER_ID)

                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")

                .append(USER_NAME)    // Songs table columns    // Songs table columns

                .append(" TEXT NOT NULL UNIQUE, ")

                .append(USER_PASSWORD)    private static final String SONG_ID = "id";    private static final String SONG_ID = "id";

                .append(" TEXT NOT NULL")

                .append(")");    private static final String SONG_NAME = "name";    private static final String SONG_NAME = "name";



        db.execSQL(sql.toString());    private static final String SONG_GENRE_ID = "genre_id";    private static final String SONG_GENRE_ID = "genre_id";



        // Create Genres table    private static final String SONG_ARTIST_ID = "artist_id";    private static final String SONG_ARTIST_ID = "artist_id";

        sql = new StringBuilder()

                .append("CREATE TABLE ")

                .append(TABLE_GENRES)

                .append("(")    // Playlists table columns    // Playlists table columns

                .append(GENRE_ID)

                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")    private static final String PLAYLIST_ID = "id";    private static final String PLAYLIST_ID = "id";

                .append(GENRE_NAME)

                .append(" TEXT NOT NULL UNIQUE")    private static final String PLAYLIST_NAME = "name";    private static final String PLAYLIST_NAME = "name";

                .append(")");

    private static final String PLAYLIST_USER_ID = "user_id";    private static final String PLAYLIST_USER_ID = "user_id";

        db.execSQL(sql.toString());



        // Create Artists table

        sql = new StringBuilder()    // Playlist Songs table columns    // Playlist Songs table columns

                .append("CREATE TABLE ")

                .append(TABLE_ARTISTS)    private static final String PLAYLIST_SONG_ID = "id";    private static final String PLAYLIST_SONG_ID = "id";

                .append("(")

                .append(ARTIST_ID)    private static final String PLAYLIST_SONG_PLAYLIST_ID = "playlist_id";    private static final String PLAYLIST_SONG_PLAYLIST_ID = "playlist_id";

                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")

                .append(ARTIST_NAME)    private static final String PLAYLIST_SONG_SONG_ID = "song_id";    private static final String PLAYLIST_SONG_SONG_ID = "song_id";

                .append(" TEXT NOT NULL UNIQUE")

                .append(")");



        db.execSQL(sql.toString());    public SQLiteManager(Context context) {    public SQLiteManager(Context context) {



        // Create Songs table        super(context, DATABASE_NAME, null, DATABASE_VERSION);        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        sql = new StringBuilder()

                .append("CREATE TABLE ")    }    }

                .append(TABLE_SONGS)

                .append("(")

                .append(SONG_ID)

                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")    public static SQLiteManager instanceOfDatabase(Context context) {    public static SQLiteManager instanceOfDatabase(Context context){

                .append(SONG_NAME)

                .append(" TEXT NOT NULL, ")        if(sqLiteManager == null)        if(sqLiteManager == null)

                .append(SONG_GENRE_ID)

                .append(" INTEGER, ")            sqLiteManager = new SQLiteManager(context);            sqLiteManager = new SQLiteManager(context);

                .append(SONG_ARTIST_ID)

                .append(" INTEGER, ")

                .append("FOREIGN KEY(")

                .append(SONG_GENRE_ID)        return sqLiteManager;        return sqLiteManager;

                .append(") REFERENCES ")

                .append(TABLE_GENRES)    }    }

                .append("(")

                .append(GENRE_ID)

                .append("), ")

                .append("FOREIGN KEY(")    @Override    @Override

                .append(SONG_ARTIST_ID)

                .append(") REFERENCES ")    public void onCreate(SQLiteDatabase db) {    public void onCreate(SQLiteDatabase db) {

                .append(TABLE_ARTISTS)

                .append("(")        // Create Users table        // Create Users table

                .append(ARTIST_ID)

                .append(")")        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +

                .append(")");

                USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +                USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

        db.execSQL(sql.toString());

                USER_NAME + " TEXT NOT NULL UNIQUE, " +                USER_NAME + " TEXT NOT NULL UNIQUE, " +

        // Create Playlists table

        sql = new StringBuilder()                USER_PASSWORD + " TEXT NOT NULL)";                USER_PASSWORD + " TEXT NOT NULL)";

                .append("CREATE TABLE ")

                .append(TABLE_PLAYLISTS)

                .append("(")

                .append(PLAYLIST_ID)        // Create Genres table        // Create Genres table

                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")

                .append(PLAYLIST_NAME)        String createGenresTable = "CREATE TABLE " + TABLE_GENRES + " (" +        String createGenresTable = "CREATE TABLE " + TABLE_GENRES + " (" +

                .append(" TEXT NOT NULL, ")

                .append(PLAYLIST_USER_ID)                GENRE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +                GENRE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                .append(" INTEGER, ")

                .append("FOREIGN KEY(")                GENRE_NAME + " TEXT NOT NULL UNIQUE)";                GENRE_NAME + " TEXT NOT NULL UNIQUE)";

                .append(PLAYLIST_USER_ID)

                .append(") REFERENCES ")

                .append(TABLE_USERS)

                .append("(")        // Create Artists table        // Create Artists table

                .append(USER_ID)

                .append(")")        String createArtistsTable = "CREATE TABLE " + TABLE_ARTISTS + " (" +        String createArtistsTable = "CREATE TABLE " + TABLE_ARTISTS + " (" +

                .append(")");

                ARTIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +                ARTIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

        db.execSQL(sql.toString());

                ARTIST_NAME + " TEXT NOT NULL, " +                ARTIST_NAME + " TEXT NOT NULL, " +

        // Create Playlist_Songs table

        sql = new StringBuilder()                ARTIST_GENRE_ID + " INTEGER, " +                ARTIST_GENRE_ID + " INTEGER, " +

                .append("CREATE TABLE ")

                .append(TABLE_PLAYLIST_SONGS)                "FOREIGN KEY(" + ARTIST_GENRE_ID + ") REFERENCES " + TABLE_GENRES + "(" + GENRE_ID + "))";                "FOREIGN KEY(" + ARTIST_GENRE_ID + ") REFERENCES " + TABLE_GENRES + "(" + GENRE_ID + "))";

                .append("(")

                .append(PLAYLIST_SONG_ID)

                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")

                .append(PLAYLIST_SONG_PLAYLIST_ID)        // Create Songs table        // Create Songs table

                .append(" INTEGER, ")

                .append(PLAYLIST_SONG_SONG_ID)        String createSongsTable = "CREATE TABLE " + TABLE_SONGS + " (" +        String createSongsTable = "CREATE TABLE " + TABLE_SONGS + " (" +

                .append(" INTEGER, ")

                .append("FOREIGN KEY(")                SONG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +                SONG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                .append(PLAYLIST_SONG_PLAYLIST_ID)

                .append(") REFERENCES ")                SONG_NAME + " TEXT NOT NULL, " +                SONG_NAME + " TEXT NOT NULL, " +

                .append(TABLE_PLAYLISTS)

                .append("(")                SONG_GENRE_ID + " INTEGER, " +                SONG_GENRE_ID + " INTEGER, " +

                .append(PLAYLIST_ID)

                .append("), ")                SONG_ARTIST_ID + " INTEGER, " +                SONG_ARTIST_ID + " INTEGER, " +

                .append("FOREIGN KEY(")

                .append(PLAYLIST_SONG_SONG_ID)                "FOREIGN KEY(" + SONG_GENRE_ID + ") REFERENCES " + TABLE_GENRES + "(" + GENRE_ID + "), " +                "FOREIGN KEY(" + SONG_GENRE_ID + ") REFERENCES " + TABLE_GENRES + "(" + GENRE_ID + "), " +

                .append(") REFERENCES ")

                .append(TABLE_SONGS)                "FOREIGN KEY(" + SONG_ARTIST_ID + ") REFERENCES " + TABLE_ARTISTS + "(" + ARTIST_ID + "))";                "FOREIGN KEY(" + SONG_ARTIST_ID + ") REFERENCES " + TABLE_ARTISTS + "(" + ARTIST_ID + "))";

                .append("(")

                .append(SONG_ID)

                .append(")")

                .append(")");        // Create Playlists table        // Create Playlists table



        db.execSQL(sql.toString());        String createPlaylistsTable = "CREATE TABLE " + TABLE_PLAYLISTS + " (" +        String createPlaylistsTable = "CREATE TABLE " + TABLE_PLAYLISTS + " (" +

    }

                PLAYLIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +                PLAYLIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

    @Override

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {                PLAYLIST_NAME + " TEXT NOT NULL, " +                PLAYLIST_NAME + " TEXT NOT NULL, " +

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST_SONGS);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLISTS);                PLAYLIST_USER_ID + " INTEGER, " +                PLAYLIST_USER_ID + " INTEGER, " +

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTISTS);                "FOREIGN KEY(" + PLAYLIST_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + USER_ID + "))";                "FOREIGN KEY(" + PLAYLIST_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + USER_ID + "))";

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GENRES);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        onCreate(db);

    }        // Create Playlist Songs table        // Create Playlist Songs table



    // User methods        String createPlaylistSongsTable = "CREATE TABLE " + TABLE_PLAYLIST_SONGS + " (" +        String createPlaylistSongsTable = "CREATE TABLE " + TABLE_PLAYLIST_SONGS + " (" +

    public long addUser(User user) {

        SQLiteDatabase db = this.getWritableDatabase();                PLAYLIST_SONG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +                PLAYLIST_SONG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

        ContentValues values = new ContentValues();

        values.put(USER_NAME, user.getName());                PLAYLIST_SONG_PLAYLIST_ID + " INTEGER, " +                PLAYLIST_SONG_PLAYLIST_ID + " INTEGER, " +

        values.put(USER_PASSWORD, user.getPassword());

        return db.insert(TABLE_USERS, null, values);                PLAYLIST_SONG_SONG_ID + " INTEGER, " +                PLAYLIST_SONG_SONG_ID + " INTEGER, " +

    }

                "FOREIGN KEY(" + PLAYLIST_SONG_PLAYLIST_ID + ") REFERENCES " + TABLE_PLAYLISTS + "(" + PLAYLIST_ID + "), " +                "FOREIGN KEY(" + PLAYLIST_SONG_PLAYLIST_ID + ") REFERENCES " + TABLE_PLAYLISTS + "(" + PLAYLIST_ID + "), " +

    public User getUserByCredentials(String name, String password) {

        SQLiteDatabase db = this.getReadableDatabase();                "FOREIGN KEY(" + PLAYLIST_SONG_SONG_ID + ") REFERENCES " + TABLE_SONGS + "(" + SONG_ID + "))";                "FOREIGN KEY(" + PLAYLIST_SONG_SONG_ID + ") REFERENCES " + TABLE_SONGS + "(" + SONG_ID + "))";

        String selection = USER_NAME + " = ? AND " + USER_PASSWORD + " = ?";

        String[] selectionArgs = {name, password};

        

        Cursor cursor = db.query(TABLE_USERS, null, selection, selectionArgs, null, null, null);        db.execSQL(createUsersTable);        db.execSQL(createUsersTable);

        

        if (cursor.moveToFirst()) {        db.execSQL(createGenresTable);        db.execSQL(createGenresTable);

            User user = new User(

                cursor.getInt(cursor.getColumnIndexOrThrow(USER_ID)),        db.execSQL(createArtistsTable);        db.execSQL(createArtistsTable);

                cursor.getString(cursor.getColumnIndexOrThrow(USER_NAME)),

                cursor.getString(cursor.getColumnIndexOrThrow(USER_PASSWORD))        db.execSQL(createSongsTable);        db.execSQL(createSongsTable);

            );

            cursor.close();        db.execSQL(createPlaylistsTable);        db.execSQL(createPlaylistsTable);

            return user;

        }        db.execSQL(createPlaylistSongsTable);        db.execSQL(createPlaylistSongsTable);

        

        cursor.close();

        return null;

    }        // Enable foreign key constraints        // Insert some initial data



    public User getUserByUsername(String name) {        db.execSQL("PRAGMA foreign_keys = ON");        insertInitialData(db);

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = USER_NAME + " = ?";    }

        String[] selectionArgs = {name};

                insertInitialData(db);

        Cursor cursor = db.query(TABLE_USERS, null, selection, selectionArgs, null, null, null);

            }    @Override

        if (cursor.moveToFirst()) {

            User user = new User(    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

                cursor.getInt(cursor.getColumnIndexOrThrow(USER_ID)),

                cursor.getString(cursor.getColumnIndexOrThrow(USER_NAME)),    @Override        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST_SONGS);

                cursor.getString(cursor.getColumnIndexOrThrow(USER_PASSWORD))

            );    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLISTS);

            cursor.close();

            return user;        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST_SONGS);        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);

        }

                db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLISTS);        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTISTS);

        cursor.close();

        return null;        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GENRES);

    }

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTISTS);        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

    // Genre methods

    public long addGenre(Genre genre) {        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GENRES);        onCreate(db);

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);    }

        values.put(GENRE_NAME, genre.getName());

        return db.insert(TABLE_GENRES, null, values);        onCreate(db);

    }

    }    private void insertInitialData(SQLiteDatabase db) {

    public List<Genre> getAllGenres() {

        List<Genre> genres = new ArrayList<>();        // Insert some sample genres

        SQLiteDatabase db = this.getReadableDatabase();

            private void insertInitialData(SQLiteDatabase db) {        ContentValues values = new ContentValues();

        Cursor cursor = db.query(TABLE_GENRES, null, null, null, null, null, GENRE_NAME);

                // Insert some sample genres        values.put(GENRE_NAME, "Pop");

        if (cursor.moveToFirst()) {

            do {        ContentValues values = new ContentValues();        db.insert(TABLE_GENRES, null, values);

                Genre genre = new Genre(

                    cursor.getInt(cursor.getColumnIndexOrThrow(GENRE_ID)),        values.put(GENRE_NAME, "Pop");

                    cursor.getString(cursor.getColumnIndexOrThrow(GENRE_NAME))

                );        db.insert(TABLE_GENRES, null, values);        values.clear();

                genres.add(genre);

            } while (cursor.moveToNext());        values.put(GENRE_NAME, "Rock");

        }

                values.clear();        db.insert(TABLE_GENRES, null, values);

        cursor.close();

        return genres;        values.put(GENRE_NAME, "Rock");

    }

        db.insert(TABLE_GENRES, null, values);        values.clear();

    public int updateGenre(Genre genre) {

        SQLiteDatabase db = this.getWritableDatabase();        values.put(GENRE_NAME, "Hip Hop");

        ContentValues values = new ContentValues();

        values.put(GENRE_NAME, genre.getName());        values.clear();        db.insert(TABLE_GENRES, null, values);

        

        String whereClause = GENRE_ID + " = ?";        values.put(GENRE_NAME, "Hip Hop");

        String[] whereArgs = {String.valueOf(genre.getId())};

                db.insert(TABLE_GENRES, null, values);        values.clear();

        return db.update(TABLE_GENRES, values, whereClause, whereArgs);

    }        values.put(GENRE_NAME, "Electronic");



    public int deleteGenre(int genreId) {        values.clear();        db.insert(TABLE_GENRES, null, values);

        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = GENRE_ID + " = ?";        values.put(GENRE_NAME, "Electronic");    }

        String[] whereArgs = {String.valueOf(genreId)};

        return db.delete(TABLE_GENRES, whereClause, whereArgs);        db.insert(TABLE_GENRES, null, values);

    }

    }    // User methods

    // Artist methods

    public long addArtist(Artist artist) {    public long addUser(User user) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();    // User methods        SQLiteDatabase db = this.getWritableDatabase();

        values.put(ARTIST_NAME, artist.getName());

        return db.insert(TABLE_ARTISTS, null, values);    public long addUser(User user) {        ContentValues values = new ContentValues();

    }

        SQLiteDatabase db = this.getWritableDatabase();        values.put(USER_NAME, user.getName());

    public List<Artist> getAllArtists() {

        List<Artist> artists = new ArrayList<>();        ContentValues values = new ContentValues();        values.put(USER_PASSWORD, user.getPassword());

        SQLiteDatabase db = this.getReadableDatabase();

                values.put(USER_NAME, user.getName());        return db.insert(TABLE_USERS, null, values);

        Cursor cursor = db.query(TABLE_ARTISTS, null, null, null, null, null, ARTIST_NAME);

                values.put(USER_PASSWORD, user.getPassword());    }

        if (cursor.moveToFirst()) {

            do {        return db.insert(TABLE_USERS, null, values);

                Artist artist = new Artist(

                    cursor.getInt(cursor.getColumnIndexOrThrow(ARTIST_ID)),    }    public User getUserByCredentials(String name, String password) {

                    cursor.getString(cursor.getColumnIndexOrThrow(ARTIST_NAME))

                );        SQLiteDatabase db = this.getReadableDatabase();

                artists.add(artist);

            } while (cursor.moveToNext());    public User getUserByCredentials(String name, String password) {        String[] columns = {USER_ID, USER_NAME, USER_PASSWORD};

        }

                SQLiteDatabase db = this.getReadableDatabase();        String selection = USER_NAME + " = ? AND " + USER_PASSWORD + " = ?";

        cursor.close();

        return artists;        String[] columns = {USER_ID, USER_NAME, USER_PASSWORD};        String[] selectionArgs = {name, password};

    }

        String selection = USER_NAME + " = ? AND " + USER_PASSWORD + " = ?";

    public int updateArtist(Artist artist) {

        SQLiteDatabase db = this.getWritableDatabase();        String[] selectionArgs = {name, password};        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        ContentValues values = new ContentValues();

        values.put(ARTIST_NAME, artist.getName());

        

        String whereClause = ARTIST_ID + " = ?";        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);        User user = null;

        String[] whereArgs = {String.valueOf(artist.getId())};

                if (cursor.moveToFirst()) {

        return db.update(TABLE_ARTISTS, values, whereClause, whereArgs);

    }        User user = null;            user = new User(



    public int deleteArtist(int artistId) {        if (cursor.moveToFirst()) {                    cursor.getInt(cursor.getColumnIndexOrThrow(USER_ID)),

        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = ARTIST_ID + " = ?";            user = new User(                    cursor.getString(cursor.getColumnIndexOrThrow(USER_NAME)),

        String[] whereArgs = {String.valueOf(artistId)};

        return db.delete(TABLE_ARTISTS, whereClause, whereArgs);                    cursor.getInt(cursor.getColumnIndexOrThrow(USER_ID)),                    cursor.getString(cursor.getColumnIndexOrThrow(USER_PASSWORD))

    }

                    cursor.getString(cursor.getColumnIndexOrThrow(USER_NAME)),            );

    // Song methods

    public long addSong(Song song) {                    cursor.getString(cursor.getColumnIndexOrThrow(USER_PASSWORD))        }

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();            );        cursor.close();

        values.put(SONG_NAME, song.getName());

        values.put(SONG_GENRE_ID, song.getGenreId());        }        return user;

        values.put(SONG_ARTIST_ID, song.getArtistId());

        return db.insert(TABLE_SONGS, null, values);        cursor.close();    }

    }

        return user;

    public List<Song> getAllSongs() {

        List<Song> songs = new ArrayList<>();    }    public User getUserByUsername(String name) {

        SQLiteDatabase db = this.getReadableDatabase();

                SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT s." + SONG_ID + ", s." + SONG_NAME + ", s." + SONG_GENRE_ID + ", s." + SONG_ARTIST_ID + 

                       ", g." + GENRE_NAME + " as genre_name" +    public User getUserByUsername(String name) {        String[] columns = {USER_ID, USER_NAME, USER_PASSWORD};

                       ", a." + ARTIST_NAME + " as artist_name" +

                       " FROM " + TABLE_SONGS + " s" +        SQLiteDatabase db = this.getReadableDatabase();        String selection = USER_NAME + " = ?";

                       " LEFT JOIN " + TABLE_GENRES + " g ON s." + SONG_GENRE_ID + " = g." + GENRE_ID +

                       " LEFT JOIN " + TABLE_ARTISTS + " a ON s." + SONG_ARTIST_ID + " = a." + ARTIST_ID +        String[] columns = {USER_ID, USER_NAME, USER_PASSWORD};        String[] selectionArgs = {name};

                       " ORDER BY s." + SONG_NAME;

                String selection = USER_NAME + " = ?";

        Cursor cursor = db.rawQuery(query, null);

                String[] selectionArgs = {name};        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {

            do {

                Song song = new Song(

                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_ID)),        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);        User user = null;

                    cursor.getString(cursor.getColumnIndexOrThrow(SONG_NAME)),

                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_GENRE_ID)),        if (cursor.moveToFirst()) {

                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_ARTIST_ID))

                );        User user = null;            user = new User(

                songs.add(song);

            } while (cursor.moveToNext());        if (cursor.moveToFirst()) {                    cursor.getInt(cursor.getColumnIndexOrThrow(USER_ID)),

        }

                    user = new User(                    cursor.getString(cursor.getColumnIndexOrThrow(USER_NAME)),

        cursor.close();

        return songs;                    cursor.getInt(cursor.getColumnIndexOrThrow(USER_ID)),                    cursor.getString(cursor.getColumnIndexOrThrow(USER_PASSWORD))

    }

                    cursor.getString(cursor.getColumnIndexOrThrow(USER_NAME)),            );

    public int updateSong(Song song) {

        SQLiteDatabase db = this.getWritableDatabase();                    cursor.getString(cursor.getColumnIndexOrThrow(USER_PASSWORD))        }

        ContentValues values = new ContentValues();

        values.put(SONG_NAME, song.getName());            );        cursor.close();

        values.put(SONG_GENRE_ID, song.getGenreId());

        values.put(SONG_ARTIST_ID, song.getArtistId());        }        return user;

        

        String whereClause = SONG_ID + " = ?";        cursor.close();    }

        String[] whereArgs = {String.valueOf(song.getId())};

                return user;

        return db.update(TABLE_SONGS, values, whereClause, whereArgs);

    }    }    // Genre methods



    public int deleteSong(int songId) {    public long addGenre(Genre genre) {

        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = SONG_ID + " = ?";    // Genre methods        SQLiteDatabase db = this.getWritableDatabase();

        String[] whereArgs = {String.valueOf(songId)};

        return db.delete(TABLE_SONGS, whereClause, whereArgs);    public long addGenre(Genre genre) {        ContentValues values = new ContentValues();

    }

        SQLiteDatabase db = this.getWritableDatabase();        values.put(GENRE_NAME, genre.getName());

    // Playlist methods

    public long addPlaylist(Playlist playlist) {        ContentValues values = new ContentValues();        return db.insert(TABLE_GENRES, null, values);

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();        values.put(GENRE_NAME, genre.getName());    }

        values.put(PLAYLIST_NAME, playlist.getName());

        values.put(PLAYLIST_USER_ID, playlist.getUserId());        return db.insert(TABLE_GENRES, null, values);

        return db.insert(TABLE_PLAYLISTS, null, values);

    }    }    public List<Genre> getAllGenres() {



    public List<Playlist> getPlaylistsByUser(int userId) {        List<Genre> genres = new ArrayList<>();

        List<Playlist> playlists = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();    public List<Genre> getAllGenres() {        SQLiteDatabase db = this.getReadableDatabase();

        

        String selection = PLAYLIST_USER_ID + " = ?";        List<Genre> genres = new ArrayList<>();        Cursor cursor = db.query(TABLE_GENRES, null, null, null, null, null, GENRE_NAME);

        String[] selectionArgs = {String.valueOf(userId)};

                SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PLAYLISTS, null, selection, selectionArgs, null, null, PLAYLIST_NAME);

                Cursor cursor = db.query(TABLE_GENRES, null, null, null, null, null, GENRE_NAME);        while (cursor.moveToNext()) {

        if (cursor.moveToFirst()) {

            do {            Genre genre = new Genre(

                Playlist playlist = new Playlist(

                    cursor.getInt(cursor.getColumnIndexOrThrow(PLAYLIST_ID)),        while (cursor.moveToNext()) {                    cursor.getInt(cursor.getColumnIndexOrThrow(GENRE_ID)),

                    cursor.getString(cursor.getColumnIndexOrThrow(PLAYLIST_NAME)),

                    cursor.getInt(cursor.getColumnIndexOrThrow(PLAYLIST_USER_ID))            Genre genre = new Genre(                    cursor.getString(cursor.getColumnIndexOrThrow(GENRE_NAME))

                );

                playlists.add(playlist);                    cursor.getInt(cursor.getColumnIndexOrThrow(GENRE_ID)),            );

            } while (cursor.moveToNext());

        }                    cursor.getString(cursor.getColumnIndexOrThrow(GENRE_NAME))            genres.add(genre);

        

        cursor.close();            );        }

        return playlists;

    }            genres.add(genre);        cursor.close();



    public int updatePlaylist(Playlist playlist) {        }        return genres;

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();        cursor.close();    }

        values.put(PLAYLIST_NAME, playlist.getName());

        values.put(PLAYLIST_USER_ID, playlist.getUserId());        return genres;

        

        String whereClause = PLAYLIST_ID + " = ?";    }    public int updateGenre(Genre genre) {

        String[] whereArgs = {String.valueOf(playlist.getId())};

                SQLiteDatabase db = this.getWritableDatabase();

        return db.update(TABLE_PLAYLISTS, values, whereClause, whereArgs);

    }    public int updateGenre(Genre genre) {        ContentValues values = new ContentValues();



    public int deletePlaylist(int playlistId) {        SQLiteDatabase db = this.getWritableDatabase();        values.put(GENRE_NAME, genre.getName());

        SQLiteDatabase db = this.getWritableDatabase();

                ContentValues values = new ContentValues();        return db.update(TABLE_GENRES, values, GENRE_ID + " = ?", new String[]{String.valueOf(genre.getId())});

        // First delete all songs from this playlist

        String whereClause1 = PLAYLIST_SONG_PLAYLIST_ID + " = ?";        values.put(GENRE_NAME, genre.getName());    }

        String[] whereArgs1 = {String.valueOf(playlistId)};

        db.delete(TABLE_PLAYLIST_SONGS, whereClause1, whereArgs1);        String whereClause = GENRE_ID + " = ?";

        

        // Then delete the playlist itself        String[] whereArgs = {String.valueOf(genre.getId())};    public int deleteGenre(int id) {

        String whereClause2 = PLAYLIST_ID + " = ?";

        String[] whereArgs2 = {String.valueOf(playlistId)};        return db.update(TABLE_GENRES, values, whereClause, whereArgs);        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(TABLE_PLAYLISTS, whereClause2, whereArgs2);

    }    }        return db.delete(TABLE_GENRES, GENRE_ID + " = ?", new String[]{String.valueOf(id)});



    // Playlist-Song relationship methods    }

    public long addSongToPlaylist(int playlistId, int songId) {

        SQLiteDatabase db = this.getWritableDatabase();    public int deleteGenre(int genreId) {

        ContentValues values = new ContentValues();

        values.put(PLAYLIST_SONG_PLAYLIST_ID, playlistId);        SQLiteDatabase db = this.getWritableDatabase();    // Artist methods

        values.put(PLAYLIST_SONG_SONG_ID, songId);

        return db.insert(TABLE_PLAYLIST_SONGS, null, values);        String whereClause = GENRE_ID + " = ?";    public long addArtist(Artist artist) {

    }

        String[] whereArgs = {String.valueOf(genreId)};        SQLiteDatabase db = this.getWritableDatabase();

    public List<Song> getSongsInPlaylist(int playlistId) {

        List<Song> songs = new ArrayList<>();        return db.delete(TABLE_GENRES, whereClause, whereArgs);        ContentValues values = new ContentValues();

        SQLiteDatabase db = this.getReadableDatabase();

            }        values.put(ARTIST_NAME, artist.getName());

        String query = "SELECT s." + SONG_ID + ", s." + SONG_NAME + ", s." + SONG_GENRE_ID + ", s." + SONG_ARTIST_ID +

                       " FROM " + TABLE_PLAYLIST_SONGS + " ps" +        values.put(ARTIST_GENRE_ID, artist.getGenreId());

                       " JOIN " + TABLE_SONGS + " s ON ps." + PLAYLIST_SONG_SONG_ID + " = s." + SONG_ID +

                       " WHERE ps." + PLAYLIST_SONG_PLAYLIST_ID + " = ?" +    // Artist methods        return db.insert(TABLE_ARTISTS, null, values);

                       " ORDER BY s." + SONG_NAME;

            public long addArtist(Artist artist) {    }

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(playlistId)});

                SQLiteDatabase db = this.getWritableDatabase();

        if (cursor.moveToFirst()) {

            do {        ContentValues values = new ContentValues();    public List<Artist> getAllArtists() {

                Song song = new Song(

                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_ID)),        values.put(ARTIST_NAME, artist.getName());        List<Artist> artists = new ArrayList<>();

                    cursor.getString(cursor.getColumnIndexOrThrow(SONG_NAME)),

                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_GENRE_ID)),        values.put(ARTIST_GENRE_ID, artist.getGenreId());        SQLiteDatabase db = this.getReadableDatabase();

                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_ARTIST_ID))

                );        return db.insert(TABLE_ARTISTS, null, values);        

                songs.add(song);

            } while (cursor.moveToNext());    }        String query = "SELECT a." + ARTIST_ID + ", a." + ARTIST_NAME + ", a." + ARTIST_GENRE_ID + ", g." + GENRE_NAME + 

        }

                               " FROM " + TABLE_ARTISTS + " a LEFT JOIN " + TABLE_GENRES + " g ON a." + ARTIST_GENRE_ID + " = g." + GENRE_ID +

        cursor.close();

        return songs;    public List<Artist> getAllArtists() {                       " ORDER BY a." + ARTIST_NAME;

    }

        List<Artist> artists = new ArrayList<>();        

    public boolean isSongInPlaylist(int playlistId, int songId) {

        SQLiteDatabase db = this.getReadableDatabase();        SQLiteDatabase db = this.getReadableDatabase();        Cursor cursor = db.rawQuery(query, null);

        String selection = PLAYLIST_SONG_PLAYLIST_ID + " = ? AND " + PLAYLIST_SONG_SONG_ID + " = ?";

        String[] selectionArgs = {String.valueOf(playlistId), String.valueOf(songId)};        

        

        Cursor cursor = db.query(TABLE_PLAYLIST_SONGS, null, selection, selectionArgs, null, null, null);        String query = "SELECT a." + ARTIST_ID + ", a." + ARTIST_NAME + ", a." + ARTIST_GENRE_ID + ", g." + GENRE_NAME +         while (cursor.moveToNext()) {

        boolean exists = cursor.getCount() > 0;

        cursor.close();                       " FROM " + TABLE_ARTISTS + " a LEFT JOIN " + TABLE_GENRES + " g ON a." + ARTIST_GENRE_ID + " = g." + GENRE_ID +            Artist artist = new Artist(

        return exists;

    }                       " ORDER BY a." + ARTIST_NAME;                    cursor.getInt(cursor.getColumnIndexOrThrow(ARTIST_ID)),



    public int removeSongFromPlaylist(int playlistId, int songId) {                            cursor.getString(cursor.getColumnIndexOrThrow(ARTIST_NAME)),

        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = PLAYLIST_SONG_PLAYLIST_ID + " = ? AND " + PLAYLIST_SONG_SONG_ID + " = ?";        Cursor cursor = db.rawQuery(query, null);                    cursor.getInt(cursor.getColumnIndexOrThrow(ARTIST_GENRE_ID))

        String[] whereArgs = {String.valueOf(playlistId), String.valueOf(songId)};

        return db.delete(TABLE_PLAYLIST_SONGS, whereClause, whereArgs);            );

    }

}        while (cursor.moveToNext()) {            artist.setGenreName(cursor.getString(cursor.getColumnIndexOrThrow(GENRE_NAME)));

            Artist artist = new Artist(            artists.add(artist);

                    cursor.getInt(cursor.getColumnIndexOrThrow(ARTIST_ID)),        }

                    cursor.getString(cursor.getColumnIndexOrThrow(ARTIST_NAME)),        cursor.close();

                    cursor.getInt(cursor.getColumnIndexOrThrow(ARTIST_GENRE_ID))        return artists;

            );    }

            artist.setGenreName(cursor.getString(cursor.getColumnIndexOrThrow(GENRE_NAME)));

            artists.add(artist);    public List<Artist> getArtistsByGenre(int genreId) {

        }        List<Artist> artists = new ArrayList<>();

        cursor.close();        SQLiteDatabase db = this.getReadableDatabase();

        return artists;        

    }        String query = "SELECT a." + ARTIST_ID + ", a." + ARTIST_NAZIV + ", a." + ARTIST_ZANR_ID + ", g." + GENRE_NAZIV + 

                       " FROM " + TABLE_ARTISTS + " a LEFT JOIN " + TABLE_GENRES + " g ON a." + ARTIST_ZANR_ID + " = g." + GENRE_ID +

    public List<Artist> getArtistsByGenre(int genreId) {                       " WHERE a." + ARTIST_ZANR_ID + " = ? ORDER BY a." + ARTIST_NAZIV;

        List<Artist> artists = new ArrayList<>();        

        SQLiteDatabase db = this.getReadableDatabase();        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(genreId)});

        

        String query = "SELECT a." + ARTIST_ID + ", a." + ARTIST_NAME + ", a." + ARTIST_GENRE_ID + ", g." + GENRE_NAME +         while (cursor.moveToNext()) {

                       " FROM " + TABLE_ARTISTS + " a LEFT JOIN " + TABLE_GENRES + " g ON a." + ARTIST_GENRE_ID + " = g." + GENRE_ID +            Artist artist = new Artist(

                       " WHERE a." + ARTIST_GENRE_ID + " = ? ORDER BY a." + ARTIST_NAME;                    cursor.getInt(cursor.getColumnIndexOrThrow(ARTIST_ID)),

                            cursor.getString(cursor.getColumnIndexOrThrow(ARTIST_NAZIV)),

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(genreId)});                    cursor.getInt(cursor.getColumnIndexOrThrow(ARTIST_ZANR_ID))

            );

        while (cursor.moveToNext()) {            artist.setZanrNaziv(cursor.getString(cursor.getColumnIndexOrThrow(GENRE_NAZIV)));

            Artist artist = new Artist(            artists.add(artist);

                    cursor.getInt(cursor.getColumnIndexOrThrow(ARTIST_ID)),        }

                    cursor.getString(cursor.getColumnIndexOrThrow(ARTIST_NAME)),        cursor.close();

                    cursor.getInt(cursor.getColumnIndexOrThrow(ARTIST_GENRE_ID))        return artists;

            );    }

            artist.setGenreName(cursor.getString(cursor.getColumnIndexOrThrow(GENRE_NAME)));

            artists.add(artist);    public int updateArtist(Artist artist) {

        }        SQLiteDatabase db = this.getWritableDatabase();

        cursor.close();        ContentValues values = new ContentValues();

        return artists;        values.put(ARTIST_NAZIV, artist.getNaziv());

    }        values.put(ARTIST_ZANR_ID, artist.getZanrId());

        return db.update(TABLE_ARTISTS, values, ARTIST_ID + " = ?", new String[]{String.valueOf(artist.getId())});

    public int updateArtist(Artist artist) {    }

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();    public int deleteArtist(int id) {

        values.put(ARTIST_NAME, artist.getName());        SQLiteDatabase db = this.getWritableDatabase();

        values.put(ARTIST_GENRE_ID, artist.getGenreId());        return db.delete(TABLE_ARTISTS, ARTIST_ID + " = ?", new String[]{String.valueOf(id)});

        String whereClause = ARTIST_ID + " = ?";    }

        String[] whereArgs = {String.valueOf(artist.getId())};

        return db.update(TABLE_ARTISTS, values, whereClause, whereArgs);    // Song methods

    }    public long addSong(Song song) {

        SQLiteDatabase db = this.getWritableDatabase();

    public int deleteArtist(int artistId) {        ContentValues values = new ContentValues();

        SQLiteDatabase db = this.getWritableDatabase();        values.put(SONG_NAZIV, song.getNaziv());

        String whereClause = ARTIST_ID + " = ?";        values.put(SONG_ZANR_ID, song.getZanrId());

        String[] whereArgs = {String.valueOf(artistId)};        values.put(SONG_IZVODJAC_ID, song.getIzvodjacId());

        return db.delete(TABLE_ARTISTS, whereClause, whereArgs);        return db.insert(TABLE_SONGS, null, values);

    }    }



    // Song methods    public List<Song> getAllSongs() {

    public long addSong(Song song) {        List<Song> songs = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();        

        values.put(SONG_NAME, song.getName());        String query = "SELECT s." + SONG_ID + ", s." + SONG_NAZIV + ", s." + SONG_ZANR_ID + ", s." + SONG_IZVODJAC_ID + 

        values.put(SONG_GENRE_ID, song.getGenreId());                       ", g." + GENRE_NAZIV + ", a." + ARTIST_NAZIV + 

        values.put(SONG_ARTIST_ID, song.getArtistId());                       " FROM " + TABLE_SONGS + " s" +

        return db.insert(TABLE_SONGS, null, values);                       " LEFT JOIN " + TABLE_GENRES + " g ON s." + SONG_ZANR_ID + " = g." + GENRE_ID +

    }                       " LEFT JOIN " + TABLE_ARTISTS + " a ON s." + SONG_IZVODJAC_ID + " = a." + ARTIST_ID +

                       " ORDER BY s." + SONG_NAZIV;

    public List<Song> getAllSongs() {        

        List<Song> songs = new ArrayList<>();        Cursor cursor = db.rawQuery(query, null);

        SQLiteDatabase db = this.getReadableDatabase();

                while (cursor.moveToNext()) {

        String query = "SELECT s." + SONG_ID + ", s." + SONG_NAME + ", s." + SONG_GENRE_ID + ", s." + SONG_ARTIST_ID +            Song song = new Song(

                       ", g." + GENRE_NAME + ", a." + ARTIST_NAME +                     cursor.getInt(cursor.getColumnIndexOrThrow(SONG_ID)),

                       " FROM " + TABLE_SONGS + " s" +                    cursor.getString(cursor.getColumnIndexOrThrow(SONG_NAZIV)),

                       " LEFT JOIN " + TABLE_GENRES + " g ON s." + SONG_GENRE_ID + " = g." + GENRE_ID +                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_ZANR_ID)),

                       " LEFT JOIN " + TABLE_ARTISTS + " a ON s." + SONG_ARTIST_ID + " = a." + ARTIST_ID +                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_IZVODJAC_ID))

                       " ORDER BY s." + SONG_NAME;            );

            song.setZanrNaziv(cursor.getString(cursor.getColumnIndexOrThrow(GENRE_NAZIV)));

        Cursor cursor = db.rawQuery(query, null);            song.setIzvodjacNaziv(cursor.getString(cursor.getColumnIndexOrThrow(ARTIST_NAZIV)));

            songs.add(song);

        while (cursor.moveToNext()) {        }

            Song song = new Song(        cursor.close();

                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_ID)),        return songs;

                    cursor.getString(cursor.getColumnIndexOrThrow(SONG_NAME)),    }

                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_GENRE_ID)),

                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_ARTIST_ID))    public List<Song> getSongsByGenre(int genreId) {

            );        List<Song> songs = new ArrayList<>();

            song.setGenreName(cursor.getString(cursor.getColumnIndexOrThrow(GENRE_NAME)));        SQLiteDatabase db = this.getReadableDatabase();

            song.setArtistName(cursor.getString(cursor.getColumnIndexOrThrow(ARTIST_NAME)));        

            songs.add(song);        String query = "SELECT s." + SONG_ID + ", s." + SONG_NAZIV + ", s." + SONG_ZANR_ID + ", s." + SONG_IZVODJAC_ID + 

        }                       ", g." + GENRE_NAZIV + ", a." + ARTIST_NAZIV + 

        cursor.close();                       " FROM " + TABLE_SONGS + " s" +

        return songs;                       " LEFT JOIN " + TABLE_GENRES + " g ON s." + SONG_ZANR_ID + " = g." + GENRE_ID +

    }                       " LEFT JOIN " + TABLE_ARTISTS + " a ON s." + SONG_IZVODJAC_ID + " = a." + ARTIST_ID +

                       " WHERE s." + SONG_ZANR_ID + " = ? ORDER BY s." + SONG_NAZIV;

    public List<Song> getSongsByGenre(int genreId) {        

        List<Song> songs = new ArrayList<>();        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(genreId)});

        SQLiteDatabase db = this.getReadableDatabase();

                while (cursor.moveToNext()) {

        String query = "SELECT s." + SONG_ID + ", s." + SONG_NAME + ", s." + SONG_GENRE_ID + ", s." + SONG_ARTIST_ID +            Song song = new Song(

                       ", g." + GENRE_NAME + ", a." + ARTIST_NAME +                     cursor.getInt(cursor.getColumnIndexOrThrow(SONG_ID)),

                       " FROM " + TABLE_SONGS + " s" +                    cursor.getString(cursor.getColumnIndexOrThrow(SONG_NAZIV)),

                       " LEFT JOIN " + TABLE_GENRES + " g ON s." + SONG_GENRE_ID + " = g." + GENRE_ID +                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_ZANR_ID)),

                       " LEFT JOIN " + TABLE_ARTISTS + " a ON s." + SONG_ARTIST_ID + " = a." + ARTIST_ID +                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_IZVODJAC_ID))

                       " WHERE s." + SONG_GENRE_ID + " = ? ORDER BY s." + SONG_NAME;            );

            song.setZanrNaziv(cursor.getString(cursor.getColumnIndexOrThrow(GENRE_NAZIV)));

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(genreId)});            song.setIzvodjacNaziv(cursor.getString(cursor.getColumnIndexOrThrow(ARTIST_NAZIV)));

            songs.add(song);

        while (cursor.moveToNext()) {        }

            Song song = new Song(        cursor.close();

                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_ID)),        return songs;

                    cursor.getString(cursor.getColumnIndexOrThrow(SONG_NAME)),    }

                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_GENRE_ID)),

                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_ARTIST_ID))    public List<Song> getSongsByArtist(int artistId) {

            );        List<Song> songs = new ArrayList<>();

            song.setGenreName(cursor.getString(cursor.getColumnIndexOrThrow(GENRE_NAME)));        SQLiteDatabase db = this.getReadableDatabase();

            song.setArtistName(cursor.getString(cursor.getColumnIndexOrThrow(ARTIST_NAME)));        

            songs.add(song);        String query = "SELECT s." + SONG_ID + ", s." + SONG_NAZIV + ", s." + SONG_ZANR_ID + ", s." + SONG_IZVODJAC_ID + 

        }                       ", g." + GENRE_NAZIV + ", a." + ARTIST_NAZIV + 

        cursor.close();                       " FROM " + TABLE_SONGS + " s" +

        return songs;                       " LEFT JOIN " + TABLE_GENRES + " g ON s." + SONG_ZANR_ID + " = g." + GENRE_ID +

    }                       " LEFT JOIN " + TABLE_ARTISTS + " a ON s." + SONG_IZVODJAC_ID + " = a." + ARTIST_ID +

                       " WHERE s." + SONG_IZVODJAC_ID + " = ? ORDER BY s." + SONG_NAZIV;

    public List<Song> getSongsByArtist(int artistId) {        

        List<Song> songs = new ArrayList<>();        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(artistId)});

        SQLiteDatabase db = this.getReadableDatabase();

                while (cursor.moveToNext()) {

        String query = "SELECT s." + SONG_ID + ", s." + SONG_NAME + ", s." + SONG_GENRE_ID + ", s." + SONG_ARTIST_ID +            Song song = new Song(

                       ", g." + GENRE_NAME + ", a." + ARTIST_NAME +                     cursor.getInt(cursor.getColumnIndexOrThrow(SONG_ID)),

                       " FROM " + TABLE_SONGS + " s" +                    cursor.getString(cursor.getColumnIndexOrThrow(SONG_NAZIV)),

                       " LEFT JOIN " + TABLE_GENRES + " g ON s." + SONG_GENRE_ID + " = g." + GENRE_ID +                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_ZANR_ID)),

                       " LEFT JOIN " + TABLE_ARTISTS + " a ON s." + SONG_ARTIST_ID + " = a." + ARTIST_ID +                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_IZVODJAC_ID))

                       " WHERE s." + SONG_ARTIST_ID + " = ? ORDER BY s." + SONG_NAME;            );

            song.setZanrNaziv(cursor.getString(cursor.getColumnIndexOrThrow(GENRE_NAZIV)));

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(artistId)});            song.setIzvodjacNaziv(cursor.getString(cursor.getColumnIndexOrThrow(ARTIST_NAZIV)));

            songs.add(song);

        while (cursor.moveToNext()) {        }

            Song song = new Song(        cursor.close();

                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_ID)),        return songs;

                    cursor.getString(cursor.getColumnIndexOrThrow(SONG_NAME)),    }

                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_GENRE_ID)),

                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_ARTIST_ID))    public int updateSong(Song song) {

            );        SQLiteDatabase db = this.getWritableDatabase();

            song.setGenreName(cursor.getString(cursor.getColumnIndexOrThrow(GENRE_NAME)));        ContentValues values = new ContentValues();

            song.setArtistName(cursor.getString(cursor.getColumnIndexOrThrow(ARTIST_NAME)));        values.put(SONG_NAZIV, song.getNaziv());

            songs.add(song);        values.put(SONG_ZANR_ID, song.getZanrId());

        }        values.put(SONG_IZVODJAC_ID, song.getIzvodjacId());

        cursor.close();        return db.update(TABLE_SONGS, values, SONG_ID + " = ?", new String[]{String.valueOf(song.getId())});

        return songs;    }

    }

    public int deleteSong(int id) {

    public int updateSong(Song song) {        SQLiteDatabase db = this.getWritableDatabase();

        SQLiteDatabase db = this.getWritableDatabase();        return db.delete(TABLE_SONGS, SONG_ID + " = ?", new String[]{String.valueOf(id)});

        ContentValues values = new ContentValues();    }

        values.put(SONG_NAME, song.getName());

        values.put(SONG_GENRE_ID, song.getGenreId());    // Playlist methods

        values.put(SONG_ARTIST_ID, song.getArtistId());    public long addPlaylist(Playlist playlist) {

        String whereClause = SONG_ID + " = ?";        SQLiteDatabase db = this.getWritableDatabase();

        String[] whereArgs = {String.valueOf(song.getId())};        ContentValues values = new ContentValues();

        return db.update(TABLE_SONGS, values, whereClause, whereArgs);        values.put(PLAYLIST_NAZIV, playlist.getNaziv());

    }        values.put(PLAYLIST_KORISNIK_ID, playlist.getKorisnikId());

        return db.insert(TABLE_PLAYLISTS, null, values);

    public int deleteSong(int songId) {    }

        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = SONG_ID + " = ?";    public List<Playlist> getPlaylistsByUser(int userId) {

        String[] whereArgs = {String.valueOf(songId)};        List<Playlist> playlists = new ArrayList<>();

        return db.delete(TABLE_SONGS, whereClause, whereArgs);        SQLiteDatabase db = this.getReadableDatabase();

    }        

        String selection = PLAYLIST_KORISNIK_ID + " = ?";

    // Playlist methods        String[] selectionArgs = {String.valueOf(userId)};

    public long addPlaylist(Playlist playlist) {        

        SQLiteDatabase db = this.getWritableDatabase();        Cursor cursor = db.query(TABLE_PLAYLISTS, null, selection, selectionArgs, null, null, PLAYLIST_NAZIV);

        ContentValues values = new ContentValues();

        values.put(PLAYLIST_NAME, playlist.getName());        while (cursor.moveToNext()) {

        values.put(PLAYLIST_USER_ID, playlist.getUserId());            Playlist playlist = new Playlist(

        return db.insert(TABLE_PLAYLISTS, null, values);                    cursor.getInt(cursor.getColumnIndexOrThrow(PLAYLIST_ID)),

    }                    cursor.getString(cursor.getColumnIndexOrThrow(PLAYLIST_NAZIV)),

                    cursor.getInt(cursor.getColumnIndexOrThrow(PLAYLIST_KORISNIK_ID))

    public List<Playlist> getPlaylistsByUser(int userId) {            );

        List<Playlist> playlists = new ArrayList<>();            playlists.add(playlist);

        SQLiteDatabase db = this.getReadableDatabase();        }

                cursor.close();

        String selection = PLAYLIST_USER_ID + " = ?";        return playlists;

        String[] selectionArgs = {String.valueOf(userId)};    }

        

        Cursor cursor = db.query(TABLE_PLAYLISTS, null, selection, selectionArgs, null, null, PLAYLIST_NAME);    public int updatePlaylist(Playlist playlist) {

        SQLiteDatabase db = this.getWritableDatabase();

        while (cursor.moveToNext()) {        ContentValues values = new ContentValues();

            Playlist playlist = new Playlist(        values.put(PLAYLIST_NAZIV, playlist.getNaziv());

                    cursor.getInt(cursor.getColumnIndexOrThrow(PLAYLIST_ID)),        return db.update(TABLE_PLAYLISTS, values, PLAYLIST_ID + " = ?", new String[]{String.valueOf(playlist.getId())});

                    cursor.getString(cursor.getColumnIndexOrThrow(PLAYLIST_NAME)),    }

                    cursor.getInt(cursor.getColumnIndexOrThrow(PLAYLIST_USER_ID))

            );    public int deletePlaylist(int id) {

            playlists.add(playlist);        SQLiteDatabase db = this.getWritableDatabase();

        }        // First delete all playlist songs

        cursor.close();        db.delete(TABLE_PLAYLIST_SONGS, PLAYLIST_SONG_PLAYLIST_ID + " = ?", new String[]{String.valueOf(id)});

        return playlists;        // Then delete the playlist

    }        return db.delete(TABLE_PLAYLISTS, PLAYLIST_ID + " = ?", new String[]{String.valueOf(id)});

    }

    public int updatePlaylist(Playlist playlist) {

        SQLiteDatabase db = this.getWritableDatabase();    // Playlist Song methods

        ContentValues values = new ContentValues();    public long addSongToPlaylist(int playlistId, int songId) {

        values.put(PLAYLIST_NAME, playlist.getName());        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = PLAYLIST_ID + " = ?";        ContentValues values = new ContentValues();

        String[] whereArgs = {String.valueOf(playlist.getId())};        values.put(PLAYLIST_SONG_PLAYLIST_ID, playlistId);

        return db.update(TABLE_PLAYLISTS, values, whereClause, whereArgs);        values.put(PLAYLIST_SONG_SONG_ID, songId);

    }        return db.insert(TABLE_PLAYLIST_SONGS, null, values);

    }

    public int deletePlaylist(int playlistId) {

        SQLiteDatabase db = this.getWritableDatabase();    public List<Song> getSongsInPlaylist(int playlistId) {

                List<Song> songs = new ArrayList<>();

        // First delete all songs in this playlist        SQLiteDatabase db = this.getReadableDatabase();

        String whereClause1 = PLAYLIST_SONG_PLAYLIST_ID + " = ?";        

        String[] whereArgs1 = {String.valueOf(playlistId)};        String query = "SELECT s." + SONG_ID + ", s." + SONG_NAZIV + ", s." + SONG_ZANR_ID + ", s." + SONG_IZVODJAC_ID + 

        db.delete(TABLE_PLAYLIST_SONGS, whereClause1, whereArgs1);                       ", g." + GENRE_NAZIV + ", a." + ARTIST_NAZIV + 

                               " FROM " + TABLE_PLAYLIST_SONGS + " ps" +

        // Then delete the playlist itself                       " JOIN " + TABLE_SONGS + " s ON ps." + PLAYLIST_SONG_SONG_ID + " = s." + SONG_ID +

        String whereClause2 = PLAYLIST_ID + " = ?";                       " LEFT JOIN " + TABLE_GENRES + " g ON s." + SONG_ZANR_ID + " = g." + GENRE_ID +

        String[] whereArgs2 = {String.valueOf(playlistId)};                       " LEFT JOIN " + TABLE_ARTISTS + " a ON s." + SONG_IZVODJAC_ID + " = a." + ARTIST_ID +

        return db.delete(TABLE_PLAYLISTS, whereClause2, whereArgs2);                       " WHERE ps." + PLAYLIST_SONG_PLAYLIST_ID + " = ? ORDER BY s." + SONG_NAZIV;

    }        

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(playlistId)});

    // Playlist Songs methods

    public long addSongToPlaylist(int playlistId, int songId) {        while (cursor.moveToNext()) {

        SQLiteDatabase db = this.getWritableDatabase();            Song song = new Song(

        ContentValues values = new ContentValues();                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_ID)),

        values.put(PLAYLIST_SONG_PLAYLIST_ID, playlistId);                    cursor.getString(cursor.getColumnIndexOrThrow(SONG_NAZIV)),

        values.put(PLAYLIST_SONG_SONG_ID, songId);                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_ZANR_ID)),

        return db.insert(TABLE_PLAYLIST_SONGS, null, values);                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_IZVODJAC_ID))

    }            );

            song.setZanrNaziv(cursor.getString(cursor.getColumnIndexOrThrow(GENRE_NAZIV)));

    public List<Song> getSongsInPlaylist(int playlistId) {            song.setIzvodjacNaziv(cursor.getString(cursor.getColumnIndexOrThrow(ARTIST_NAZIV)));

        List<Song> songs = new ArrayList<>();            songs.add(song);

        SQLiteDatabase db = this.getReadableDatabase();        }

                cursor.close();

        String query = "SELECT s." + SONG_ID + ", s." + SONG_NAME + ", s." + SONG_GENRE_ID + ", s." + SONG_ARTIST_ID +        return songs;

                       ", g." + GENRE_NAME + ", a." + ARTIST_NAME +     }

                       " FROM " + TABLE_PLAYLIST_SONGS + " ps" +

                       " JOIN " + TABLE_SONGS + " s ON ps." + PLAYLIST_SONG_SONG_ID + " = s." + SONG_ID +    public int removeSongFromPlaylist(int playlistId, int songId) {

                       " LEFT JOIN " + TABLE_GENRES + " g ON s." + SONG_GENRE_ID + " = g." + GENRE_ID +        SQLiteDatabase db = this.getWritableDatabase();

                       " LEFT JOIN " + TABLE_ARTISTS + " a ON s." + SONG_ARTIST_ID + " = a." + ARTIST_ID +        String selection = PLAYLIST_SONG_PLAYLIST_ID + " = ? AND " + PLAYLIST_SONG_SONG_ID + " = ?";

                       " WHERE ps." + PLAYLIST_SONG_PLAYLIST_ID + " = ?" +        String[] selectionArgs = {String.valueOf(playlistId), String.valueOf(songId)};

                       " ORDER BY s." + SONG_NAME;        return db.delete(TABLE_PLAYLIST_SONGS, selection, selectionArgs);

    }

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(playlistId)});}


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
        String whereClause = PLAYLIST_SONG_PLAYLIST_ID + " = ? AND " + PLAYLIST_SONG_SONG_ID + " = ?";
        String[] whereArgs = {String.valueOf(playlistId), String.valueOf(songId)};
        return db.delete(TABLE_PLAYLIST_SONGS, whereClause, whereArgs);
    }
}