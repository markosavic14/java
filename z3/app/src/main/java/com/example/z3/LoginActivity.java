package com.example.z3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends Activity {

    private EditText editTextServerIP;
    private EditText editTextPort;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button buttonRegister;
    private ExecutorService executorService;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize executor service and handler for background tasks
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        // Initialize UI components
        editTextServerIP = findViewById(R.id.editTextServerIP);
        editTextPort = findViewById(R.id.editTextPort);
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
        String serverIP = editTextServerIP.getText().toString().trim();
        String portStr = editTextPort.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validate input
        if (serverIP.isEmpty()) {
            Toast.makeText(this, "Please enter server IP", Toast.LENGTH_SHORT).show();
            return;
        }

        if (portStr.isEmpty()) {
            Toast.makeText(this, "Please enter port", Toast.LENGTH_SHORT).show();
            return;
        }

        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid port number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Execute login in background thread
        performLogin(serverIP, port, username, password);
    }

    private void handleRegister() {
        String serverIP = editTextServerIP.getText().toString().trim();
        String portStr = editTextPort.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validate input
        if (serverIP.isEmpty()) {
            Toast.makeText(this, "Please enter server IP", Toast.LENGTH_SHORT).show();
            return;
        }

        if (portStr.isEmpty()) {
            Toast.makeText(this, "Please enter port", Toast.LENGTH_SHORT).show();
            return;
        }

        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid port number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Execute registration in background thread
        performRegistration(serverIP, port, username, password);
    }

    private void performLogin(String serverIP, int port, String username, String password) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                String result = null;
                try {
                    // Connect to server
                    Socket socket = new Socket(serverIP, port);
                    
                    // Set up communication streams
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    
                    // Send login data
                    // Format: LOGIN:username:password
                    String loginData = "LOGIN:" + username + ":" + password;
                    out.println(loginData);
                    
                    // Read server response
                    result = in.readLine();
                    
                    // Close connection
                    socket.close();
                    
                } catch (IOException e) {
                    result = "Connection error: " + e.getMessage();
                }

                // Handle response on main thread
                final String finalResult = result;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        handleLoginResponse(finalResult, username);
                    }
                });
            }
        });
    }

    private void performRegistration(String serverIP, int port, String username, String password) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                String result = null;
                try {
                    // Connect to server
                    Socket socket = new Socket(serverIP, port);
                    
                    // Set up communication streams
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    
                    // Send registration data
                    // Format: REGISTER:username:password
                    String registrationData = "REGISTER:" + username + ":" + password;
                    out.println(registrationData);
                    
                    // Read server response
                    result = in.readLine();
                    
                    // Close connection
                    socket.close();
                    
                } catch (IOException e) {
                    result = "Connection error: " + e.getMessage();
                }

                // Handle response on main thread
                final String finalResult = result;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        handleRegistrationResponse(finalResult);
                    }
                });
            }
        });
    }

    private void handleLoginResponse(String result, String username) {
        if (result != null) {
            if (result.equals("AUTH_SUCCESS")) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_LONG).show();
                
                // Navigate to GameActivity
                Intent intent = new Intent(LoginActivity.this, GameActivity.class);
                // Pass server connection details to GameActivity if needed
                intent.putExtra("serverIP", editTextServerIP.getText().toString().trim());
                intent.putExtra("port", editTextPort.getText().toString().trim());
                intent.putExtra("username", username);
                startActivity(intent);
                
                // Optional: finish this activity so user can't go back to login
                finish();
                
            } else if (result.equals("AUTH_FAILURE")) {
                Toast.makeText(this, "Login failed. Invalid username or password.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Server response: " + result, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "No response from server", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleRegistrationResponse(String result) {
        if (result != null) {
            if (result.equals("REGISTER_SUCCESS")) {
                Toast.makeText(this, "Registration successful! You can now login.", Toast.LENGTH_LONG).show();
                // Clear the form after successful registration
                editTextUsername.setText("");
                editTextPassword.setText("");
            } else if (result.equals("REGISTER_FAILURE")) {
                Toast.makeText(this, "Registration failed. User may already exist.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Server response: " + result, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "No response from server", Toast.LENGTH_SHORT).show();
        }
    }
}