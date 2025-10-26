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
    private Button logoutButton;
    private ExecutorService executorService;
    private Handler mainHandler;
    private ArrayAdapter<String> userAdapter;
    private List<String> activeUsers;
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
        logoutButton = findViewById(R.id.logoutButton);

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

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
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
        // Use the existing connection from ConnectionManager
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        
        if (connectionManager.isConnected()) {
            isConnectedToServer = true;
            
            // Set up message listener for this activity
            connectionManager.setConnectionListener(new ConnectionManager.ConnectionListener() {
                @Override
                public void onMessageReceived(String message) {
                    handleServerMessage(message);
                }
                
                @Override
                public void onConnectionLost() {
                    isConnectedToServer = false;
                    Toast.makeText(UserListActivity.this, "Lost connection to server", Toast.LENGTH_SHORT).show();
                }
                
                @Override
                public void onConnectionEstablished() {
                    isConnectedToServer = true;
                    Toast.makeText(UserListActivity.this, "Connected to server", Toast.LENGTH_SHORT).show();
                }
            });
            
            // Get initial active users list
            refreshActiveUsers();
            
            Toast.makeText(UserListActivity.this, "Connected to server", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(UserListActivity.this, "No connection to server", Toast.LENGTH_SHORT).show();
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
            
        } else if (message.startsWith("GAME_START:")) {
            // Handle multiplayer game start: GAME_START:opponent:roomId:playerNumber
            String[] parts = message.split(":");
            if (parts.length >= 4) {
                String opponent = parts[1];
                String roomId = parts[2];
                String playerNumber = parts[3];
                
                Toast.makeText(this, "Game starting with " + opponent + "!", Toast.LENGTH_SHORT).show();
                navigateToMultiplayerGame(opponent, roomId, playerNumber);
            }
            
        } else if (message.startsWith("CONNECTION_DECLINED:")) {
            // Handle connection decline
            String declinerUsername = message.substring(20); // Remove "CONNECTION_DECLINED:" prefix
            Toast.makeText(this, declinerUsername + " declined your connection request.", Toast.LENGTH_LONG).show();
            
        } else if (message.equals("CONNECTION_SUCCESS")) {
            Toast.makeText(this, "Connection established successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshActiveUsers() {
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        if (connectionManager.isConnected()) {
            connectionManager.sendMessage("GET_ACTIVE_USERS");
        } else {
            Toast.makeText(this, "Not connected to server", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendConnectionRequest(String targetUsername) {
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        if (connectionManager.isConnected()) {
            connectionManager.sendMessage("CONNECT_REQUEST:" + targetUsername);
            Toast.makeText(UserListActivity.this, "Connection request sent to " + targetUsername, Toast.LENGTH_SHORT).show();
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
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        if (connectionManager.isConnected()) {
            if (accept) {
                connectionManager.sendMessage("CONNECT_ACCEPT:" + requesterUsername);
                navigateToGame(requesterUsername);
            } else {
                connectionManager.sendMessage("CONNECT_DECLINE:" + requesterUsername);
            }
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

    private void navigateToMultiplayerGame(String opponentUsername, String roomId, String playerNumber) {
        Intent gameIntent = new Intent(UserListActivity.this, GameActivity.class);
        gameIntent.putExtra("serverIP", serverIP);
        gameIntent.putExtra("port", port);
        gameIntent.putExtra("username", currentUsername);
        gameIntent.putExtra("opponent", opponentUsername);
        gameIntent.putExtra("gameStart", "GAME_START:" + opponentUsername + ":" + roomId + ":" + playerNumber);
        gameIntent.putExtra("isMultiplayer", true);
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
        // Connection is managed by ConnectionManager, no need to close here
    }

    private void logout() {
        // Close connection and return to login screen
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        connectionManager.closeConnection();
        
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        
        // Return to LoginActivity
        Intent loginIntent = new Intent(UserListActivity.this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginIntent);
        finish();
    }
}