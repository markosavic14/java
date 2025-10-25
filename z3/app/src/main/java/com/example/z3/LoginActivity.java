package com.example.z3;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.AsyncTask;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextServerIP;
    private EditText editTextPort;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        editTextServerIP = findViewById(R.id.editTextServerIP);
        editTextPort = findViewById(R.id.editTextPort);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonRegister = findViewById(R.id.buttonRegister);

        // Set click listener for register button
        buttonRegister.setOnClickListener(v -> handleRegister());
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
        new RegisterTask().execute(serverIP, String.valueOf(port), username, password);
    }

    private class RegisterTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String serverIP = params[0];
            int port = Integer.parseInt(params[1]);
            String username = params[2];
            String password = params[3];

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
                String response = in.readLine();
                
                // Close connection
                socket.close();
                
                return response;
                
            } catch (IOException e) {
                return "Connection error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Display server response to user
            if (result != null) {
                if (result.equals("REGISTER_SUCCESS")) {
                    Toast.makeText(LoginActivity.this, "Registration successful!", Toast.LENGTH_LONG).show();
                    // Clear the form after successful registration
                    editTextUsername.setText("");
                    editTextPassword.setText("");
                } else if (result.equals("REGISTER_FAILURE")) {
                    Toast.makeText(LoginActivity.this, "Registration failed. User may already exist.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Server response: " + result, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "No response from server", Toast.LENGTH_SHORT).show();
            }
        }
    }
}