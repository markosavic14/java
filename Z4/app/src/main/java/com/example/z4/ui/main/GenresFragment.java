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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.z4.R;
import com.example.z4.SQLiteManager;
import com.example.z4.model.Genre;

import java.util.ArrayList;
import java.util.List;

public class GenresFragment extends Fragment {

    private ListView listViewGenres;
    private Button buttonAddGenre;
    private SQLiteManager dbManager;
    private List<Genre> genresList;
    private ArrayAdapter<Genre> genresAdapter;

    public static GenresFragment newInstance() {
        return new GenresFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_genres, container, false);

        dbManager = SQLiteManager.instanceOfDatabase(getContext());
        
        listViewGenres = root.findViewById(R.id.listViewGenres);
        buttonAddGenre = root.findViewById(R.id.buttonAddGenre);

        setupListView();
        loadGenres();

        buttonAddGenre.setOnClickListener(v -> showAddGenreDialog());

        listViewGenres.setOnItemClickListener((parent, view, position, id) -> {
            Genre selectedGenre = genresList.get(position);
            showEditGenreDialog(selectedGenre);
        });

        listViewGenres.setOnItemLongClickListener((parent, view, position, id) -> {
            Genre selectedGenre = genresList.get(position);
            showDeleteConfirmation(selectedGenre);
            return true;
        });

        return root;
    }

    private void setupListView() {
        genresList = new ArrayList<>();
        genresAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, genresList);
        listViewGenres.setAdapter(genresAdapter);
    }

    private void loadGenres() {
        genresList.clear();
        genresList.addAll(dbManager.getAllGenres());
        genresAdapter.notifyDataSetChanged();
    }

    private void showAddGenreDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_edit_genre, null);
        
        EditText editTextGenreName = dialogView.findViewById(R.id.editTextGenreName);

        builder.setView(dialogView)
                .setTitle("Add Genre")
                .setPositiveButton("Add", (dialog, which) -> {
                    String genreName = editTextGenreName.getText().toString().trim();

                    if (genreName.isEmpty()) {
                        Toast.makeText(getContext(), "Enter genre name", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Genre newGenre = new Genre(genreName);
                    long result = dbManager.addGenre(newGenre);
                    
                    if (result > 0) {
                        Toast.makeText(getContext(), "Genre added", Toast.LENGTH_SHORT).show();
                        loadGenres();
                    } else {
                        Toast.makeText(getContext(), "Error adding genre", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditGenreDialog(Genre genre) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_edit_genre, null);
        
        EditText editTextGenreName = dialogView.findViewById(R.id.editTextGenreName);
        editTextGenreName.setText(genre.getName());

        builder.setView(dialogView)
                .setTitle("Edit Genre")
                .setPositiveButton("Save", (dialog, which) -> {
                    String genreName = editTextGenreName.getText().toString().trim();

                    if (genreName.isEmpty()) {
                        Toast.makeText(getContext(), "Enter genre name", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    genre.setName(genreName);
                    
                    int result = dbManager.updateGenre(genre);
                    
                    if (result > 0) {
                        Toast.makeText(getContext(), "Genre updated", Toast.LENGTH_SHORT).show();
                        loadGenres();
                    } else {
                        Toast.makeText(getContext(), "Error updating genre", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteConfirmation(Genre genre) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Genre")
                .setMessage("Are you sure you want to delete the genre '" + genre.getName() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    int result = dbManager.deleteGenre(genre.getId());
                    if (result > 0) {
                        Toast.makeText(getContext(), "Genre deleted", Toast.LENGTH_SHORT).show();
                        loadGenres();
                    } else {
                        Toast.makeText(getContext(), "Error deleting genre", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}