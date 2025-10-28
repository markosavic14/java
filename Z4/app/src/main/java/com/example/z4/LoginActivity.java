package com.example.z4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.z4.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends Activity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button buttonRegister;
    private ExecutorService executorService;
    private Handler mainHandler;
    private SQLiteManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize database manager
        dbManager = SQLiteManager.instanceOfDatabase(this);

        // Initialize executor service and handler for background tasks
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        // Initialize UI components
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);

        // Set click listeners
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });
        
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegister();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    private void handleLogin() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validate input
        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Execute login in background thread
        performLogin(username, password);
    }

    private void handleRegister() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validate input
        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Execute registration in background thread
        performRegistration(username, password);
    }

    private void performLogin(String username, String password) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // Check if user exists with provided credentials
                User user = dbManager.getUserByCredentials(username, password);
                
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (user != null) {
                            // Save user session
                            UserSession.getInstance().setCurrentUser(user);
                            
                            // Navigate to MainActivity
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // Close login activity
                        } else {
                            Toast.makeText(LoginActivity.this, 
                                "Invalid username or password", 
                                Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void performRegistration(String username, String password) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // Check if username already exists
                User existingUser = dbManager.getUserByUsername(username);
                
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (existingUser != null) {
                            Toast.makeText(LoginActivity.this, 
                                "Username already exists", 
                                Toast.LENGTH_SHORT).show();
                        } else {
                            // Create new user
                            User newUser = new User(username, password);
                            long userId = dbManager.addUser(newUser);
                            
                            if (userId > 0) {
                                newUser.setId((int) userId);
                                UserSession.getInstance().setCurrentUser(newUser);
                                
                                Toast.makeText(LoginActivity.this, 
                                    "Successfully registered!", 
                                    Toast.LENGTH_SHORT).show();
                                
                                // Navigate to MainActivity
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish(); // Close login activity
                            } else {
                                Toast.makeText(LoginActivity.this, 
                                    "Registration error", 
                                    Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
    }
}