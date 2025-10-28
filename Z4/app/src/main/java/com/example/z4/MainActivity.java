package com.example.z4;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.example.z4.ui.main.ArtistsFragment;
import com.example.z4.ui.main.GenresFragment;
import com.example.z4.ui.main.PlaylistsFragment;
import com.example.z4.ui.main.SectionsPagerAdapter;
import com.example.z4.ui.main.SongsFragment;
import com.example.z4.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SectionsPagerAdapter sectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is logged in
        if (!UserSession.getInstance().isUserLoggedIn()) {
            // Redirect to login if not logged in
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = binding.fab;

        // Set up search button click listener
        ImageButton searchButton = binding.searchButton;
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSearchActivity();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleFabClick(viewPager.getCurrentItem());
            }
        });
    }

    private void handleFabClick(int currentTabPosition) {
        // Get the actual fragment from the FragmentManager
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.view_pager + ":" + currentTabPosition);
        
        if (currentFragment instanceof SongsFragment) {
            ((SongsFragment) currentFragment).showAddDialog();
        } else if (currentFragment instanceof ArtistsFragment) {
            ((ArtistsFragment) currentFragment).showAddDialog();
        } else if (currentFragment instanceof GenresFragment) {
            ((GenresFragment) currentFragment).showAddDialog();
        } else if (currentFragment instanceof PlaylistsFragment) {
            ((PlaylistsFragment) currentFragment).showAddDialog();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openSearchActivity() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    private void logout() {
        UserSession.getInstance().logout();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}