package com.example.z4;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.z4.model.Artist;
import com.example.z4.model.Genre;
import com.example.z4.model.Playlist;
import com.example.z4.model.Song;

import java.util.ArrayList;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends Activity {

    private RadioGroup radioGroupSearchType;
    private RadioButton radioSongsByName;
    private RadioButton radioSongsByArtist;
    private RadioButton radioSongsByGenre;
    private RadioButton radioSongsInPlaylist;
    private RadioButton radioSearchArtists;
    private RadioButton radioSearchGenres;
    private TextView textViewSongName;
    private TextView textViewGenre;
    private TextView textViewArtist;
    private TextView textViewPlaylist;
    private TextView textViewPlaylistSongName;
    private EditText editTextSongName;
    private EditText editTextGenreName;
    private EditText editTextArtistName;
    private EditText editTextPlaylistName;
    private TextView textViewSearchArtist;
    private TextView textViewSearchGenre;
    private EditText editTextSearchArtistName;
    private EditText editTextSearchGenreName;
    private Spinner spinnerPlaylist;
    private Button buttonSearch;
    private ListView listViewResults;
    private SQLiteManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        dbManager = SQLiteManager.instanceOfDatabase(this);
        
        initializeViews();
        setupListeners();
        
        // Initially show only song name input
        updateVisibility(R.id.radioSongsByName);
    }

    private void initializeViews() {
        radioGroupSearchType = findViewById(R.id.radioGroupSearchType);
        radioSongsByName = findViewById(R.id.radioSongsByName);
        radioSongsByArtist = findViewById(R.id.radioSongsByArtist);
        radioSongsByGenre = findViewById(R.id.radioSongsByGenre);
        radioSongsInPlaylist = findViewById(R.id.radioSongsInPlaylist);
        radioSearchArtists = findViewById(R.id.radioSearchArtists);
        radioSearchGenres = findViewById(R.id.radioSearchGenres);
        
        textViewSongName = findViewById(R.id.textViewSongName);
        textViewGenre = findViewById(R.id.textViewGenre);
        textViewArtist = findViewById(R.id.textViewArtist);
        textViewPlaylist = findViewById(R.id.textViewPlaylist);
        textViewPlaylistSongName = findViewById(R.id.textViewPlaylistSongName);
        textViewSearchArtist = findViewById(R.id.textViewSearchArtist);
        textViewSearchGenre = findViewById(R.id.textViewSearchGenre);
        
        editTextSongName = findViewById(R.id.editTextSongName);
        editTextGenreName = findViewById(R.id.editTextGenreName);
        editTextArtistName = findViewById(R.id.editTextArtistName);
        editTextPlaylistName = findViewById(R.id.editTextPlaylistName);
        editTextSearchArtistName = findViewById(R.id.editTextSearchArtistName);
        editTextSearchGenreName = findViewById(R.id.editTextSearchGenreName);
        spinnerPlaylist = findViewById(R.id.spinnerPlaylist);
        buttonSearch = findViewById(R.id.buttonSearch);
        listViewResults = findViewById(R.id.listViewResults);
    }

    private void setupListeners() {
        radioGroupSearchType.setOnCheckedChangeListener((group, checkedId) -> {
            updateVisibility(checkedId);
        });

        buttonSearch.setOnClickListener(v -> performSearch());
    }

    private void updateVisibility(int checkedId) {
        // Hide all inputs first
        textViewSongName.setVisibility(View.GONE);
        editTextSongName.setVisibility(View.GONE);
        textViewGenre.setVisibility(View.GONE);
        editTextGenreName.setVisibility(View.GONE);
        textViewArtist.setVisibility(View.GONE);
        editTextArtistName.setVisibility(View.GONE);
        textViewPlaylist.setVisibility(View.GONE);
        spinnerPlaylist.setVisibility(View.GONE);
        textViewPlaylistSongName.setVisibility(View.GONE);
        editTextPlaylistName.setVisibility(View.GONE);
        textViewSearchArtist.setVisibility(View.GONE);
        editTextSearchArtistName.setVisibility(View.GONE);
        textViewSearchGenre.setVisibility(View.GONE);
        editTextSearchGenreName.setVisibility(View.GONE);

        if (checkedId == R.id.radioSongsByName) {
            // Show song name input
            textViewSongName.setVisibility(View.VISIBLE);
            editTextSongName.setVisibility(View.VISIBLE);
        } else if (checkedId == R.id.radioSongsByArtist) {
            // Show artist input
            textViewArtist.setVisibility(View.VISIBLE);
            editTextArtistName.setVisibility(View.VISIBLE);
        } else if (checkedId == R.id.radioSongsByGenre) {
            // Show genre input
            textViewGenre.setVisibility(View.VISIBLE);
            editTextGenreName.setVisibility(View.VISIBLE);
        } else if (checkedId == R.id.radioSongsInPlaylist) {
            // Show playlist dropdown and song name input
            textViewPlaylist.setVisibility(View.VISIBLE);
            spinnerPlaylist.setVisibility(View.VISIBLE);
            textViewPlaylistSongName.setVisibility(View.VISIBLE);
            editTextPlaylistName.setVisibility(View.VISIBLE);
            loadUserPlaylists();
        } else if (checkedId == R.id.radioSearchArtists) {
            // Show artist search input
            textViewSearchArtist.setVisibility(View.VISIBLE);
            editTextSearchArtistName.setVisibility(View.VISIBLE);
        } else if (checkedId == R.id.radioSearchGenres) {
            // Show genre search input
            textViewSearchGenre.setVisibility(View.VISIBLE);
            editTextSearchGenreName.setVisibility(View.VISIBLE);
        }
    }

    private void performSearch() {
        int checkedId = radioGroupSearchType.getCheckedRadioButtonId();
        
        if (checkedId == R.id.radioSongsByName) {
            searchSongsByName();
        } else if (checkedId == R.id.radioSongsByArtist) {
            searchSongsByArtist();
        } else if (checkedId == R.id.radioSongsByGenre) {
            searchSongsByGenre();
        } else if (checkedId == R.id.radioSongsInPlaylist) {
            searchSongsInPlaylist();
        } else if (checkedId == R.id.radioSearchArtists) {
            searchArtists();
        } else if (checkedId == R.id.radioSearchGenres) {
            searchGenres();
        } else {
            Toast.makeText(this, "Select search type", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchSongsByName() {
        String songName = editTextSongName.getText().toString().trim();
        
        if (songName.isEmpty()) {
            Toast.makeText(this, "Enter song name", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Song> songs = dbManager.getSongsByName(songName);
        displaySongs(songs);
    }

    private void searchSongsByGenre() {
        String genreName = editTextGenreName.getText().toString().trim();
        
        if (genreName.isEmpty()) {
            Toast.makeText(this, "Enter genre name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Find genres that match the name
        List<Genre> allGenres = dbManager.getAllGenres();
        List<Song> songs = new ArrayList<>();
        
        for (Genre genre : allGenres) {
            if (genre.getName().toLowerCase().contains(genreName.toLowerCase())) {
                songs.addAll(dbManager.getSongsByGenre(genre.getId()));
            }
        }
        
        displaySongs(songs);
    }

    private void searchSongsByArtist() {
        String artistName = editTextArtistName.getText().toString().trim();
        
        if (artistName.isEmpty()) {
            Toast.makeText(this, "Enter artist name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Find artists that match the name
        List<Artist> allArtists = dbManager.getAllArtists();
        List<Song> songs = new ArrayList<>();
        
        for (Artist artist : allArtists) {
            if (artist.getName().toLowerCase().contains(artistName.toLowerCase())) {
                songs.addAll(dbManager.getSongsByArtist(artist.getId()));
            }
        }
        
        displaySongs(songs);
    }

    private void searchSongsInPlaylist() {
        String songName = editTextPlaylistName.getText().toString().trim();
        
        if (songName.isEmpty()) {
            Toast.makeText(this, "Enter song name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the current user's playlists
        if (!UserSession.getInstance().isUserLoggedIn()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Get selected playlist
        if (spinnerPlaylist.getSelectedItem() == null) {
            Toast.makeText(this, "No playlist selected", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Playlist selectedPlaylist = (Playlist) spinnerPlaylist.getSelectedItem();
        List<Song> allSongsInPlaylist = dbManager.getSongsInPlaylist(selectedPlaylist.getId());
        List<Song> matchingSongs = new ArrayList<>();
        
        // Filter songs by name
        for (Song song : allSongsInPlaylist) {
            if (song.getName().toLowerCase().contains(songName.toLowerCase())) {
                matchingSongs.add(song);
            }
        }
        
        displaySongs(matchingSongs);
    }

    private void loadUserPlaylists() {
        if (!UserSession.getInstance().isUserLoggedIn()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        int userId = UserSession.getInstance().getCurrentUser().getId();
        List<Playlist> playlists = dbManager.getPlaylistsByUser(userId);
        
        if (playlists.isEmpty()) {
            Toast.makeText(this, "No playlists found", Toast.LENGTH_SHORT).show();
        }
        
        ArrayAdapter<Playlist> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, playlists);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlaylist.setAdapter(adapter);
    }

    private void displaySongs(List<Song> songs) {
        if (songs.isEmpty()) {
            Toast.makeText(this, "No search results", Toast.LENGTH_SHORT).show();
            listViewResults.setAdapter(null);
            return;
        }

        ArrayAdapter<Song> adapter = new ArrayAdapter<Song>(this, android.R.layout.simple_list_item_2, android.R.id.text1, songs) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                Song song = songs.get(position);
                
                android.widget.TextView text1 = view.findViewById(android.R.id.text1);
                android.widget.TextView text2 = view.findViewById(android.R.id.text2);
                
                text1.setText(song.getName());
                text2.setText("Artist: " + song.getArtistName() + " | Genre: " + song.getGenreName());
                
                return view;
            }
        };
        listViewResults.setAdapter(adapter);
    }

    private void searchArtists() {
        String artistName = editTextSearchArtistName.getText().toString().trim();
        
        if (artistName.isEmpty()) {
            Toast.makeText(this, "Enter artist name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Find artists that match the name
        List<Artist> allArtists = dbManager.getAllArtists();
        List<Artist> matchingArtists = new ArrayList<>();
        
        for (Artist artist : allArtists) {
            if (artist.getName().toLowerCase().contains(artistName.toLowerCase())) {
                matchingArtists.add(artist);
            }
        }
        
        displayArtists(matchingArtists);
    }

    private void searchGenres() {
        String genreName = editTextSearchGenreName.getText().toString().trim();
        
        if (genreName.isEmpty()) {
            Toast.makeText(this, "Enter genre name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Find genres that match the name
        List<Genre> allGenres = dbManager.getAllGenres();
        List<Genre> matchingGenres = new ArrayList<>();
        
        for (Genre genre : allGenres) {
            if (genre.getName().toLowerCase().contains(genreName.toLowerCase())) {
                matchingGenres.add(genre);
            }
        }
        
        displayGenres(matchingGenres);
    }

    private void displayArtists(List<Artist> artists) {
        if (artists.isEmpty()) {
            Toast.makeText(this, "No search results", Toast.LENGTH_SHORT).show();
            listViewResults.setAdapter(null);
            return;
        }

        ArrayAdapter<Artist> adapter = new ArrayAdapter<Artist>(this, android.R.layout.simple_list_item_2, android.R.id.text1, artists) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                Artist artist = artists.get(position);
                
                android.widget.TextView text1 = view.findViewById(android.R.id.text1);
                android.widget.TextView text2 = view.findViewById(android.R.id.text2);
                
                text1.setText(artist.getName());
                text2.setText("Genre: " + artist.getGenreName());
                
                return view;
            }
        };
        listViewResults.setAdapter(adapter);
    }

    private void displayGenres(List<Genre> genres) {
        if (genres.isEmpty()) {
            Toast.makeText(this, "No search results", Toast.LENGTH_SHORT).show();
            listViewResults.setAdapter(null);
            return;
        }

        ArrayAdapter<Genre> adapter = new ArrayAdapter<Genre>(this, android.R.layout.simple_list_item_1, android.R.id.text1, genres) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                Genre genre = genres.get(position);
                
                android.widget.TextView text1 = view.findViewById(android.R.id.text1);
                
                text1.setText(genre.getName());
                
                return view;
            }
        };
        listViewResults.setAdapter(adapter);
    }
}