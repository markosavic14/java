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
import com.example.z4.UserSession;
import com.example.z4.model.Playlist;
import com.example.z4.model.Song;
import com.example.z4.model.User;

import java.util.ArrayList;
import java.util.List;

public class PlaylistsFragment extends Fragment {

    private SQLiteManager dbManager;
    private ListView listViewPlaylists;
    private ArrayAdapter<Playlist> playlistsAdapter;
    private List<Playlist> playlistsList;

    public PlaylistsFragment() {
        // Required empty public constructor
    }

    public static PlaylistsFragment newInstance() {
        return new PlaylistsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize database manager
        dbManager = SQLiteManager.instanceOfDatabase(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playlists, container, false);

        // Initialize UI components
        listViewPlaylists = view.findViewById(R.id.listViewPlaylists);

        // Setup adapter
        playlistsList = new ArrayList<>();
        playlistsAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, playlistsList);
        listViewPlaylists.setAdapter(playlistsAdapter);

        // Load playlists
        loadPlaylists();

        // Set click listeners
        listViewPlaylists.setOnItemClickListener((parent, view1, position, id) -> {
            Playlist selectedPlaylist = playlistsList.get(position);
            showPlaylistSongs(selectedPlaylist);
        });

        listViewPlaylists.setOnItemLongClickListener((parent, view12, position, id) -> {
            Playlist selectedPlaylist = playlistsList.get(position);
            showPlaylistOptions(selectedPlaylist);
            return true;
        });

        return view;
    }

    private void loadPlaylists() {
        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            playlistsList.clear();
            playlistsList.addAll(dbManager.getPlaylistsByUser(currentUser.getId()));
            playlistsAdapter.notifyDataSetChanged();
        }
    }

    public void showAddDialog() {
        showAddPlaylistDialog();
    }

    private void showAddPlaylistDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_edit_playlist, null);
        
        EditText editTextPlaylistName = dialogView.findViewById(R.id.editTextPlaylistName);

        builder.setView(dialogView)
                .setTitle("Add Playlist")
                .setPositiveButton("Add", (dialog, which) -> {
                    String playlistName = editTextPlaylistName.getText().toString().trim();

                    if (playlistName.isEmpty()) {
                        Toast.makeText(requireContext(), "Enter playlist name", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    User currentUser = UserSession.getInstance().getCurrentUser();
                    if (currentUser == null) {
                        Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Playlist newPlaylist = new Playlist(playlistName, currentUser.getId());
                    long result = dbManager.addPlaylist(newPlaylist);
                    
                    if (result > 0) {
                        Toast.makeText(requireContext(), "Playlist added", Toast.LENGTH_SHORT).show();
                        loadPlaylists();
                    } else {
                        Toast.makeText(requireContext(), "Error adding playlist", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showPlaylistSongs(Playlist playlist) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_playlist_songs, null);
        
        ListView listViewSongs = dialogView.findViewById(R.id.listViewPlaylistSongs);
        Button buttonAddSongToPlaylist = dialogView.findViewById(R.id.buttonAddSongToPlaylist);

        List<Song> playlistSongs = dbManager.getSongsInPlaylist(playlist.getId());
        ArrayAdapter<Song> songsAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, playlistSongs);
        listViewSongs.setAdapter(songsAdapter);

        buttonAddSongToPlaylist.setOnClickListener(v -> showAddSongToPlaylistDialog(playlist, songsAdapter, playlistSongs));

        listViewSongs.setOnItemLongClickListener((parent, view, position, id) -> {
            Song selectedSong = playlistSongs.get(position);
            showRemoveSongConfirmation(playlist, selectedSong, songsAdapter, playlistSongs);
            return true;
        });

        builder.setView(dialogView)
                .setTitle("Songs in playlist: " + playlist.getName())
                .setNegativeButton("Close", null)
                .show();
    }

    private void showAddSongToPlaylistDialog(Playlist playlist, ArrayAdapter<Song> adapter, List<Song> playlistSongs) {
        List<Song> allSongs = dbManager.getAllSongs();
        
        if (allSongs.isEmpty()) {
            Toast.makeText(requireContext(), "No songs available", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] songNames = new String[allSongs.size()];
        for (int i = 0; i < allSongs.size(); i++) {
            songNames[i] = allSongs.get(i).getName();
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Select Song")
                .setItems(songNames, (dialog, which) -> {
                    Song selectedSong = allSongs.get(which);
                    
                    // Check if song is already in playlist
                    if (dbManager.isSongInPlaylist(playlist.getId(), selectedSong.getId())) {
                        Toast.makeText(requireContext(), "Song is already in playlist", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    long result = dbManager.addSongToPlaylist(playlist.getId(), selectedSong.getId());
                    if (result > 0) {
                        playlistSongs.add(selectedSong);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(requireContext(), "Song added to playlist", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Error adding song", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showRemoveSongConfirmation(Playlist playlist, Song song, ArrayAdapter<Song> adapter, List<Song> playlistSongs) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Remove Song")
                .setMessage("Do you want to remove the song '" + song.getName() + "' from the playlist?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    int result = dbManager.removeSongFromPlaylist(playlist.getId(), song.getId());
                    if (result > 0) {
                        playlistSongs.remove(song);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(requireContext(), "Song removed from playlist", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Error removing song", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showPlaylistOptions(Playlist playlist) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Playlist Options")
                .setItems(new String[]{"Edit", "Delete"}, (dialog, which) -> {
                    if (which == 0) {
                        showEditPlaylistDialog(playlist);
                    } else {
                        showDeletePlaylistConfirmation(playlist);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditPlaylistDialog(Playlist playlist) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_edit_playlist, null);
        
        EditText editTextPlaylistName = dialogView.findViewById(R.id.editTextPlaylistName);
        editTextPlaylistName.setText(playlist.getName());

        builder.setView(dialogView)
                .setTitle("Edit Playlist")
                .setPositiveButton("Update", (dialog, which) -> {
                    String newPlaylistName = editTextPlaylistName.getText().toString().trim();

                    if (newPlaylistName.isEmpty()) {
                        Toast.makeText(requireContext(), "Enter playlist name", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    playlist.setName(newPlaylistName);
                    int result = dbManager.updatePlaylist(playlist);
                    
                    if (result > 0) {
                        Toast.makeText(requireContext(), "Playlist updated", Toast.LENGTH_SHORT).show();
                        loadPlaylists();
                    } else {
                        Toast.makeText(requireContext(), "Error updating playlist", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeletePlaylistConfirmation(Playlist playlist) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Playlist")
                .setMessage("Are you sure you want to delete the playlist '" + playlist.getName() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    int result = dbManager.deletePlaylist(playlist.getId());
                    if (result > 0) {
                        Toast.makeText(requireContext(), "Playlist deleted", Toast.LENGTH_SHORT).show();
                        loadPlaylists();
                    } else {
                        Toast.makeText(requireContext(), "Error deleting playlist", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}