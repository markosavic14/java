package com.example.z4.ui.main;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.z4.R;
import com.example.z4.SQLiteManager;
import com.example.z4.model.Artist;
import com.example.z4.model.Genre;
import com.example.z4.model.Song;

import java.util.ArrayList;
import java.util.List;

public class SongsFragment extends Fragment {

    private ListView listViewSongs;
    private Button buttonAddSong;
    private SQLiteManager dbManager;
    private List<Song> songsList;
    private ArrayAdapter<Song> songsAdapter;
    private List<Genre> genresList;
    private List<Artist> artistsList;

    public static SongsFragment newInstance() {
        return new SongsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_songs, container, false);

        dbManager = SQLiteManager.instanceOfDatabase(getContext());
        
        listViewSongs = root.findViewById(R.id.listViewSongs);
        buttonAddSong = root.findViewById(R.id.buttonAddSong);

        setupListView();
        loadSongs();
        loadGenresAndArtists();

        buttonAddSong.setOnClickListener(v -> showAddSongDialog());

        listViewSongs.setOnItemClickListener((parent, view, position, id) -> {
            Song selectedSong = songsList.get(position);
            showEditSongDialog(selectedSong);
        });

        listViewSongs.setOnItemLongClickListener((parent, view, position, id) -> {
            Song selectedSong = songsList.get(position);
            showDeleteConfirmation(selectedSong);
            return true;
        });

        return root;
    }

    private void setupListView() {
        songsList = new ArrayList<>();
        songsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, songsList);
        listViewSongs.setAdapter(songsAdapter);
    }

    private void loadSongs() {
        songsList.clear();
        songsList.addAll(dbManager.getAllSongs());
        songsAdapter.notifyDataSetChanged();
    }

    private void loadGenresAndArtists() {
        genresList = dbManager.getAllGenres();
        artistsList = dbManager.getAllArtists();
    }

    private void showAddSongDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_edit_song, null);
        
        EditText editTextSongName = dialogView.findViewById(R.id.editTextSongName);
        Spinner spinnerGenre = dialogView.findViewById(R.id.spinnerGenre);
        Spinner spinnerArtist = dialogView.findViewById(R.id.spinnerArtist);

        // Setup spinners
        ArrayAdapter<Genre> genreAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, genresList);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(genreAdapter);

        ArrayAdapter<Artist> artistAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, artistsList);
        artistAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerArtist.setAdapter(artistAdapter);

        builder.setView(dialogView)
                .setTitle("Add Song")
                .setPositiveButton("Add", (dialog, which) -> {
                    String songName = editTextSongName.getText().toString().trim();
                    Genre selectedGenre = (Genre) spinnerGenre.getSelectedItem();
                    Artist selectedArtist = (Artist) spinnerArtist.getSelectedItem();

                    if (songName.isEmpty()) {
                        Toast.makeText(getContext(), "Enter song name", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (selectedGenre == null || selectedArtist == null) {
                        Toast.makeText(getContext(), "Select genre and artist", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Song newSong = new Song(songName, selectedGenre.getId(), selectedArtist.getId());
                    long result = dbManager.addSong(newSong);
                    
                    if (result > 0) {
                        Toast.makeText(getContext(), "Song added", Toast.LENGTH_SHORT).show();
                        loadSongs();
                    } else {
                        Toast.makeText(getContext(), "Error adding song", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditSongDialog(Song song) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_edit_song, null);
        
        EditText editTextSongName = dialogView.findViewById(R.id.editTextSongName);
        Spinner spinnerGenre = dialogView.findViewById(R.id.spinnerGenre);
        Spinner spinnerArtist = dialogView.findViewById(R.id.spinnerArtist);

        editTextSongName.setText(song.getName());

        // Setup spinners
        ArrayAdapter<Genre> genreAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, genresList);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(genreAdapter);

        ArrayAdapter<Artist> artistAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, artistsList);
        artistAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerArtist.setAdapter(artistAdapter);

        // Set selected items
        for (int i = 0; i < genresList.size(); i++) {
            if (genresList.get(i).getId() == song.getGenreId()) {
                spinnerGenre.setSelection(i);
                break;
            }
        }

        for (int i = 0; i < artistsList.size(); i++) {
            if (artistsList.get(i).getId() == song.getArtistId()) {
                spinnerArtist.setSelection(i);
                break;
            }
        }

        builder.setView(dialogView)
                .setTitle("Edit Song")
                .setPositiveButton("Save", (dialog, which) -> {
                    String songName = editTextSongName.getText().toString().trim();
                    Genre selectedGenre = (Genre) spinnerGenre.getSelectedItem();
                    Artist selectedArtist = (Artist) spinnerArtist.getSelectedItem();

                    if (songName.isEmpty()) {
                        Toast.makeText(getContext(), "Enter song name", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (selectedGenre == null || selectedArtist == null) {
                        Toast.makeText(getContext(), "Select genre and artist", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    song.setName(songName);
                    song.setGenreId(selectedGenre.getId());
                    song.setArtistId(selectedArtist.getId());
                    
                    int result = dbManager.updateSong(song);
                    
                    if (result > 0) {
                        Toast.makeText(getContext(), "Song updated", Toast.LENGTH_SHORT).show();
                        loadSongs();
                    } else {
                        Toast.makeText(getContext(), "Error updating song", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteConfirmation(Song song) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Song")
                .setMessage("Are you sure you want to delete the song '" + song.getName() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    int result = dbManager.deleteSong(song.getId());
                    if (result > 0) {
                        Toast.makeText(getContext(), "Song deleted", Toast.LENGTH_SHORT).show();
                        loadSongs();
                    } else {
                        Toast.makeText(getContext(), "Error deleting song", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}