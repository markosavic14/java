package com.example.z4.ui.main;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.List;

public class ArtistsFragment extends Fragment {

    private ListView listViewArtists;
    private Button buttonAddArtist;
    private SQLiteManager dbManager;
    private List<Artist> artistsList;
    private ArrayAdapter<Artist> artistsAdapter;
    private List<Genre> genresList;

    public static ArtistsFragment newInstance() {
        return new ArtistsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_artists, container, false);

        dbManager = SQLiteManager.instanceOfDatabase(getContext());
        
        listViewArtists = root.findViewById(R.id.listViewArtists);
        buttonAddArtist = root.findViewById(R.id.buttonAddArtist);

        setupListView();
        loadArtists();
        loadGenres();

        buttonAddArtist.setOnClickListener(v -> showAddArtistDialog());

        listViewArtists.setOnItemClickListener((parent, view, position, id) -> {
            Artist selectedArtist = artistsList.get(position);
            showEditArtistDialog(selectedArtist);
        });

        listViewArtists.setOnItemLongClickListener((parent, view, position, id) -> {
            Artist selectedArtist = artistsList.get(position);
            showDeleteConfirmation(selectedArtist);
            return true;
        });

        return root;
    }

    private void setupListView() {
        artistsList = new ArrayList<>();
        artistsAdapter = new ArrayAdapter<Artist>(getContext(), android.R.layout.simple_list_item_2, android.R.id.text1, artistsList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                Artist artist = artistsList.get(position);
                
                android.widget.TextView text1 = view.findViewById(android.R.id.text1);
                android.widget.TextView text2 = view.findViewById(android.R.id.text2);
                
                text1.setText(artist.getName());
                text2.setText("Genre: " + (artist.getZanrNaziv() != null ? artist.getZanrNaziv() : "Unknown"));
                
                return view;
            }
        };
        listViewArtists.setAdapter(artistsAdapter);
    }

    private void loadArtists() {
        artistsList.clear();
        artistsList.addAll(dbManager.getAllArtists());
        artistsAdapter.notifyDataSetChanged();
    }

    private void loadGenres() {
        genresList = dbManager.getAllGenres();
    }

    private void showAddArtistDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_edit_artist, null);
        
        EditText editTextArtistName = dialogView.findViewById(R.id.editTextArtistName);
        Spinner spinnerGenre = dialogView.findViewById(R.id.spinnerGenre);

        // Setup spinner
        ArrayAdapter<Genre> genreAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, genresList);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(genreAdapter);

        builder.setView(dialogView)
                .setTitle("Add Artist")
                .setPositiveButton("Add", (dialog, which) -> {
                    String artistName = editTextArtistName.getText().toString().trim();
                    Genre selectedGenre = (Genre) spinnerGenre.getSelectedItem();

                    if (artistName.isEmpty()) {
                        Toast.makeText(getContext(), "Enter artist name", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (selectedGenre == null) {
                        Toast.makeText(getContext(), "Select genre", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Artist newArtist = new Artist(artistName, selectedGenre.getId());
                    long result = dbManager.addArtist(newArtist);
                    
                    if (result > 0) {
                        Toast.makeText(getContext(), "Artist added", Toast.LENGTH_SHORT).show();
                        loadArtists();
                    } else {
                        Toast.makeText(getContext(), "Error adding artist", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditArtistDialog(Artist artist) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_edit_artist, null);
        
        EditText editTextArtistName = dialogView.findViewById(R.id.editTextArtistName);
        Spinner spinnerGenre = dialogView.findViewById(R.id.spinnerGenre);

        editTextArtistName.setText(artist.getName());

        // Setup spinner
        ArrayAdapter<Genre> genreAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, genresList);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(genreAdapter);

        // Set selected genre
        for (int i = 0; i < genresList.size(); i++) {
            if (genresList.get(i).getId() == artist.getGenreId()) {
                spinnerGenre.setSelection(i);
                break;
            }
        }

        builder.setView(dialogView)
                .setTitle("Edit Artist")
                .setPositiveButton("Save", (dialog, which) -> {
                    String artistName = editTextArtistName.getText().toString().trim();
                    Genre selectedGenre = (Genre) spinnerGenre.getSelectedItem();

                    if (artistName.isEmpty()) {
                        Toast.makeText(getContext(), "Enter artist name", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (selectedGenre == null) {
                        Toast.makeText(getContext(), "Select genre", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    artist.setName(artistName);
                    artist.setGenreId(selectedGenre.getId());
                    
                    int result = dbManager.updateArtist(artist);
                    
                    if (result > 0) {
                        Toast.makeText(getContext(), "Artist updated", Toast.LENGTH_SHORT).show();
                        loadArtists();
                    } else {
                        Toast.makeText(getContext(), "Error updating artist", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteConfirmation(Artist artist) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Artist")
                .setMessage("Are you sure you want to delete the artist '" + artist.getName() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    int result = dbManager.deleteArtist(artist.getId());
                    if (result > 0) {
                        Toast.makeText(getContext(), "Artist deleted", Toast.LENGTH_SHORT).show();
                        loadArtists();
                    } else {
                        Toast.makeText(getContext(), "Error deleting artist", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}