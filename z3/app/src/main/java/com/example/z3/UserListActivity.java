package com.example.z3;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserListActivity extends Activity {
    
    private String serverIP;
    private int port;
    private String currentUsername;
    private ListView userListView;
    private TextView welcomeText;
    private Button refreshButton;
    private Button startGameButton;
    private ExecutorService executorService;
    private Handler mainHandler;
    private ArrayAdapter<String> userAdapter;
    private List<String> activeUsers;
    private Socket persistentSocket;
    private PrintWriter socketOut;
    private BufferedReader socketIn;
    private boolean isConnectedToServer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        // Get connection details from intent
        Intent intent = getIntent();
        serverIP = intent.getStringExtra("serverIP");
        port = intent.getIntExtra("port", 8800);
        currentUsername = intent.getStringExtra("username");

        // Initialize executor service and handler
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        // Initialize UI components
        welcomeText = findViewById(R.id.welcomeText);
        userListView = findViewById(R.id.userListView);
        refreshButton = findViewById(R.id.refreshButton);
        startGameButton = findViewById(R.id.startGameButton);

        // Set welcome message
        welcomeText.setText("Welcome, " + currentUsername + "!");

        // Initialize user list
        activeUsers = new ArrayList<>();
        userAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, activeUsers);
        userListView.setAdapter(userAdapter);

        // Set click listeners
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshActiveUsers();
            }
        });

        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedUser = activeUsers.get(position);
                if (!selectedUser.equals(currentUsername)) {
                    sendConnectionRequest(selectedUser);
                } else {
                    Toast.makeText(UserListActivity.this, "You cannot connect to yourself", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Establish persistent connection and get initial user list
        establishPersistentConnection();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
        closePersistentConnection();
    }

    private void establishPersistentConnection() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // Create persistent connection to server
                    persistentSocket = new Socket(serverIP, port);
                    socketOut = new PrintWriter(persistentSocket.getOutputStream(), true);
                    socketIn = new BufferedReader(new InputStreamReader(persistentSocket.getInputStream()));
                    
                    // Re-authenticate to maintain session
                    socketOut.println("LOGIN:" + currentUsername + ":dummy"); // Password not needed for reconnection
                    String authResponse = socketIn.readLine();
                    
                    if ("AUTH_SUCCESS".equals(authResponse)) {
                        isConnectedToServer = true;
                        
                        // Start listening for incoming messages in a separate thread
                        Thread listenerThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                listenForServerMessages();
                            }
                        });
                        listenerThread.start();
                        
                        // Get initial active users list
                        refreshActiveUsers();
                        
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UserListActivity.this, "Connected to server", Toast.LENGTH_SHORT).show();
                            }
                        });
                        
                    } else {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UserListActivity.this, "Failed to connect to server", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    
                } catch (IOException e) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UserListActivity.this, "Connection error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void listenForServerMessages() {
        try {
            String message;
            while (isConnectedToServer && (message = socketIn.readLine()) != null) {
                final String finalMessage = message;
                
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        handleServerMessage(finalMessage);
                    }
                });
            }
        } catch (IOException e) {
            if (isConnectedToServer) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UserListActivity.this, "Lost connection to server", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void handleServerMessage(String message) {
        if (message.startsWith("ACTIVE_USERS:")) {
            // Update active users list
            String userListStr = message.substring(13); // Remove "ACTIVE_USERS:" prefix
            if (!userListStr.isEmpty()) {
                String[] users = userListStr.split(",");
                activeUsers.clear();
                activeUsers.addAll(Arrays.asList(users));
            } else {
                activeUsers.clear();
            }
            userAdapter.notifyDataSetChanged();
            
        } else if (message.startsWith("CONNECTION_REQUEST:")) {
            // Handle incoming connection request
            String requesterUsername = message.substring(19); // Remove "CONNECTION_REQUEST:" prefix
            showConnectionRequestDialog(requesterUsername);
            
        } else if (message.startsWith("CONNECTION_ACCEPTED:")) {
            // Handle connection acceptance
            String accepterUsername = message.substring(20); // Remove "CONNECTION_ACCEPTED:" prefix
            Toast.makeText(this, accepterUsername + " accepted your connection request!", Toast.LENGTH_LONG).show();
            // Navigate to game
            navigateToGame(accepterUsername);
            
        } else if (message.startsWith("CONNECTION_DECLINED:")) {
            // Handle connection decline
            String declinerUsername = message.substring(20); // Remove "CONNECTION_DECLINED:" prefix
            Toast.makeText(this, declinerUsername + " declined your connection request.", Toast.LENGTH_LONG).show();
            
        } else if (message.equals("CONNECTION_SUCCESS")) {
            Toast.makeText(this, "Connection established successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshActiveUsers() {
        if (isConnectedToServer && socketOut != null) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        socketOut.println("GET_ACTIVE_USERS");
                    } catch (Exception e) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UserListActivity.this, "Error refreshing user list", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        } else {
            Toast.makeText(this, "Not connected to server", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendConnectionRequest(String targetUsername) {
        if (isConnectedToServer && socketOut != null) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        socketOut.println("CONNECT_REQUEST:" + targetUsername);
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UserListActivity.this, "Connection request sent to " + targetUsername, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UserListActivity.this, "Error sending connection request", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        } else {
            Toast.makeText(this, "Not connected to server", Toast.LENGTH_SHORT).show();
        }
    }

    private void showConnectionRequestDialog(String requesterUsername) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Connection Request");
        builder.setMessage(requesterUsername + " wants to connect with you for a game. Do you accept?");
        
        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                respondToConnectionRequest(requesterUsername, true);
                dialog.dismiss();
            }
        });
        
        builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                respondToConnectionRequest(requesterUsername, false);
                dialog.dismiss();
            }
        });
        
        builder.setCancelable(false);
        builder.show();
    }

    private void respondToConnectionRequest(String requesterUsername, boolean accept) {
        if (isConnectedToServer && socketOut != null) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (accept) {
                            socketOut.println("CONNECT_ACCEPT:" + requesterUsername);
                            // Navigate to game
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    navigateToGame(requesterUsername);
                                }
                            });
                        } else {
                            socketOut.println("CONNECT_DECLINE:" + requesterUsername);
                        }
                    } catch (Exception e) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UserListActivity.this, "Error responding to connection request", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }

    private void navigateToGame(String opponentUsername) {
        Intent gameIntent = new Intent(UserListActivity.this, GameActivity.class);
        gameIntent.putExtra("serverIP", serverIP);
        gameIntent.putExtra("port", port);
        gameIntent.putExtra("username", currentUsername);
        gameIntent.putExtra("opponent", opponentUsername);
        startActivity(gameIntent);
        finish();
    }

    private void startGame() {
        // Start game without opponent (single player or practice mode)
        Intent gameIntent = new Intent(UserListActivity.this, GameActivity.class);
        gameIntent.putExtra("serverIP", serverIP);
        gameIntent.putExtra("port", port);
        gameIntent.putExtra("username", currentUsername);
        startActivity(gameIntent);
        finish();
    }

    private void closePersistentConnection() {
        isConnectedToServer = false;
        try {
            if (socketOut != null) {
                socketOut.close();
            }
            if (socketIn != null) {
                socketIn.close();
            }
            if (persistentSocket != null && !persistentSocket.isClosed()) {
                persistentSocket.close();
            }
        } catch (IOException e) {
            // Handle cleanup errors silently
        }
    }
}