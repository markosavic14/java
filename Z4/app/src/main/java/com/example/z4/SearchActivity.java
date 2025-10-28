package com.example.z4;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.z4.model.Artist;
import com.example.z4.model.Genre;
import com.example.z4.model.Song;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends Activity {

    private RadioGroup radioGroupSearchType;
    private RadioButton radioSongsByGenre;
    private RadioButton radioSongsByArtist;
    private RadioButton radioArtistsByGenre;
    private Spinner spinnerGenre;
    private Spinner spinnerArtist;
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
        loadSpinnerData();
    }

    private void initializeViews() {
        radioGroupSearchType = findViewById(R.id.radioGroupSearchType);
        radioSongsByGenre = findViewById(R.id.radioSongsByGenre);
        radioSongsByArtist = findViewById(R.id.radioSongsByArtist);
        radioArtistsByGenre = findViewById(R.id.radioArtistsByGenre);
        spinnerGenre = findViewById(R.id.spinnerGenre);
        spinnerArtist = findViewById(R.id.spinnerArtist);
        buttonSearch = findViewById(R.id.buttonSearch);
        listViewResults = findViewById(R.id.listViewResults);
    }

    private void setupListeners() {
        radioGroupSearchType.setOnCheckedChangeListener((group, checkedId) -> {
            updateSpinnerVisibility(checkedId);
        });

        buttonSearch.setOnClickListener(v -> performSearch());
    }

    private void loadSpinnerData() {
        // Load genres
        List<Genre> genres = dbManager.getAllGenres();
        ArrayAdapter<Genre> genreAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genres);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(genreAdapter);

        // Load artists
        List<Artist> artists = dbManager.getAllArtists();
        ArrayAdapter<Artist> artistAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, artists);
        artistAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerArtist.setAdapter(artistAdapter);

        // Initially show only genre spinner
        updateSpinnerVisibility(R.id.radioSongsByGenre);
    }

    private void updateSpinnerVisibility(int checkedId) {
        spinnerGenre.setVisibility(View.GONE);
        spinnerArtist.setVisibility(View.GONE);

        if (checkedId == R.id.radioSongsByGenre || checkedId == R.id.radioArtistsByGenre) {
            spinnerGenre.setVisibility(View.VISIBLE);
        } else if (checkedId == R.id.radioSongsByArtist) {
            spinnerArtist.setVisibility(View.VISIBLE);
        }
    }

    private void performSearch() {
        int checkedId = radioGroupSearchType.getCheckedRadioButtonId();
        
        if (checkedId == R.id.radioSongsByGenre) {
            searchSongsByGenre();
        } else if (checkedId == R.id.radioSongsByArtist) {
            searchSongsByArtist();
        } else if (checkedId == R.id.radioArtistsByGenre) {
            searchArtistsByGenre();
        } else {
            Toast.makeText(this, "Select search type", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchSongsByGenre() {
        Genre selectedGenre = (Genre) spinnerGenre.getSelectedItem();
        if (selectedGenre == null) {
            Toast.makeText(this, "Select genre", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Song> songs = dbManager.getSongsByGenre(selectedGenre.getId());
        displaySongs(songs);
    }

    private void searchSongsByArtist() {
        Artist selectedArtist = (Artist) spinnerArtist.getSelectedItem();
        if (selectedArtist == null) {
            Toast.makeText(this, "Select artist", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Song> songs = dbManager.getSongsByArtist(selectedArtist.getId());
        displaySongs(songs);
    }

    private void searchArtistsByGenre() {
        Genre selectedGenre = (Genre) spinnerGenre.getSelectedItem();
        if (selectedGenre == null) {
            Toast.makeText(this, "Select genre", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Artist> artists = dbManager.getArtistsByGenre(selectedGenre.getId());
        displayArtists(artists);
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
                text2.setText("Genre: " + (artist.getGenreName() != null ? artist.getGenreName() : "Unknown"));
                
                return view;
            }
        };
        listViewResults.setAdapter(adapter);
    }
}