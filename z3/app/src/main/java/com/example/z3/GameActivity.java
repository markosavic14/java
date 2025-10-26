package com.example.z3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GameActivity extends AppCompatActivity {

    // Game constants
    private static final int ROWS = 6;
    private static final int COLS = 7;
    private static final int EMPTY = 0;
    private static final int PLAYER1 = 1; // Red
    private static final int PLAYER2 = 2; // Yellow
    
    // Game state
    private int[][] gameBoard = new int[ROWS][COLS];
    private int currentPlayer = PLAYER1;
    private boolean gameOver = false;
    private String player1Name = "Player 1";
    private String player2Name = "Player 2";
    private boolean isMultiplayer = false;
    private boolean isMyTurn = true;
    private String myPlayerNumber = "PLAYER1"; // PLAYER1 or PLAYER2
    private String opponentName = "";
    private String gameRoomId = "";
    
    // UI elements
    private Button[] columnButtons = new Button[COLS];
    private ImageView[][] gameGrid = new ImageView[ROWS][COLS];
    private TextView gameStatus;
    private Button resetButton;
    private Button forfeitButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get player information from intent
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String opponent = intent.getStringExtra("opponent");
        
        if (username != null) {
            player1Name = username;
        }
        if (opponent != null) {
            player2Name = opponent;
            isMultiplayer = true;
            opponentName = opponent;
        }

        // Check if this is a multiplayer game start
        String gameStart = intent.getStringExtra("gameStart");
        if (gameStart != null && gameStart.startsWith("GAME_START:")) {
            // Parse: GAME_START:opponent:roomId:playerNumber
            String[] parts = gameStart.split(":");
            if (parts.length >= 4) {
                opponentName = parts[1];
                gameRoomId = parts[2];
                myPlayerNumber = parts[3];
                isMultiplayer = true;
                
                if (myPlayerNumber.equals("PLAYER1")) {
                    player1Name = username;
                    player2Name = opponentName;
                    isMyTurn = true;
                } else {
                    player1Name = opponentName;
                    player2Name = username;
                    isMyTurn = false;
                }
            }
        }

        initializeUI();
        initializeGame();
        
        // Set up multiplayer communication if needed
        if (isMultiplayer) {
            setupMultiplayerConnection();
        }
    }

    private void initializeUI() {
        // Initialize column buttons
        for (int col = 0; col < COLS; col++) {
            final int columnIndex = col;
            String buttonId = "button" + col;
            int resId = getResources().getIdentifier(buttonId, "id", getPackageName());
            columnButtons[col] = findViewById(resId);
            
            columnButtons[col].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!gameOver) {
                        makeMove(columnIndex);
                    }
                }
            });
        }

        // Initialize game grid ImageViews
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                String imageViewId = "iv" + col + row;
                int resId = getResources().getIdentifier(imageViewId, "id", getPackageName());
                gameGrid[row][col] = findViewById(resId);
                
                if (gameGrid[row][col] != null) {
                    // Set initial empty state
                    gameGrid[row][col].setColorFilter(getResources().getColor(R.color.gray));
                }
            }
        }

        // Add status text and reset button to the layout if they exist
        // These would need to be added to the XML layout
        try {
            gameStatus = findViewById(R.id.gameStatus);
            resetButton = findViewById(R.id.resetButton);
            forfeitButton = findViewById(R.id.forfeitButton);
            
            if (resetButton != null) {
                resetButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resetGame();
                    }
                });
            }
            
            if (forfeitButton != null) {
                forfeitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        forfeitGame();
                    }
                });
            }
        } catch (Exception e) {
            // Status text and reset button not found in layout, continue without them
        }
    }

    private void initializeGame() {
        // Initialize empty board
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                gameBoard[row][col] = EMPTY;
            }
        }
        
        currentPlayer = PLAYER1;
        gameOver = false;
        updateGameStatus();
    }

    private void makeMove(int col) {
        if (gameOver) {
            return;
        }
        
        // Check if it's multiplayer and if it's the player's turn
        if (isMultiplayer && !isMyTurn) {
            Toast.makeText(this, "It's not your turn!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!isValidMove(col)) {
            return;
        }

        if (isMultiplayer) {
            // Send move to server
            ConnectionManager connectionManager = ConnectionManager.getInstance();
            if (connectionManager.isConnected()) {
                connectionManager.sendMessage("MAKE_MOVE:" + col);
            } else {
                Toast.makeText(this, "Connection lost!", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Local game logic
            processMove(col, currentPlayer);
        }
    }
    
    private void processMove(int col, int player) {
        // Find the lowest empty row in the selected column
        int row = getLowestEmptyRow(col);
        if (row == -1) {
            Toast.makeText(this, "Column is full!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Place the piece
        gameBoard[row][col] = player;
        updateGamePiece(row, col, player);

        // Check for win or draw
        if (checkWin(row, col)) {
            gameOver = true;
            String winner = (player == PLAYER1) ? player1Name : player2Name;
            showWinDialog(winner);
        } else if (isBoardFull()) {
            gameOver = true;
            showDrawDialog();
        } else {
            // Switch players (for local game)
            if (!isMultiplayer) {
                currentPlayer = (currentPlayer == PLAYER1) ? PLAYER2 : PLAYER1;
            }
            updateGameStatus();
        }
    }

    private boolean isValidMove(int col) {
        return col >= 0 && col < COLS && gameBoard[0][col] == EMPTY;
    }

    private int getLowestEmptyRow(int col) {
        for (int row = ROWS - 1; row >= 0; row--) {
            if (gameBoard[row][col] == EMPTY) {
                return row;
            }
        }
        return -1; // Column is full
    }

    private void updateGamePiece(int row, int col, int player) {
        if (gameGrid[row][col] != null) {
            int color = (player == PLAYER1) ? R.color.red : R.color.yellow;
            gameGrid[row][col].setColorFilter(getResources().getColor(color));
        }
    }

    private boolean checkWin(int row, int col) {
        int player = gameBoard[row][col];
        
        // Check horizontal
        if (checkDirection(row, col, 0, 1, player) + checkDirection(row, col, 0, -1, player) + 1 >= 4) {
            return true;
        }
        
        // Check vertical
        if (checkDirection(row, col, 1, 0, player) + checkDirection(row, col, -1, 0, player) + 1 >= 4) {
            return true;
        }
        
        // Check diagonal (top-left to bottom-right)
        if (checkDirection(row, col, 1, 1, player) + checkDirection(row, col, -1, -1, player) + 1 >= 4) {
            return true;
        }
        
        // Check diagonal (top-right to bottom-left)
        if (checkDirection(row, col, 1, -1, player) + checkDirection(row, col, -1, 1, player) + 1 >= 4) {
            return true;
        }
        
        return false;
    }

    private int checkDirection(int row, int col, int deltaRow, int deltaCol, int player) {
        int count = 0;
        int r = row + deltaRow;
        int c = col + deltaCol;
        
        while (r >= 0 && r < ROWS && c >= 0 && c < COLS && gameBoard[r][c] == player) {
            count++;
            r += deltaRow;
            c += deltaCol;
        }
        
        return count;
    }

    private boolean isBoardFull() {
        for (int col = 0; col < COLS; col++) {
            if (gameBoard[0][col] == EMPTY) {
                return false;
            }
        }
        return true;
    }

    private void updateGameStatus() {
        if (gameStatus != null) {
            String status;
            if (isMultiplayer) {
                if (isMyTurn) {
                    status = "Your turn";
                } else {
                    status = opponentName + "'s turn";
                }
            } else {
                String currentPlayerName = (currentPlayer == PLAYER1) ? player1Name : player2Name;
                status = currentPlayerName + "'s turn";
            }
            gameStatus.setText(status);
        }
    }

    private void setupMultiplayerConnection() {
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        
        if (connectionManager.isConnected()) {
            connectionManager.setConnectionListener(new ConnectionManager.ConnectionListener() {
                @Override
                public void onMessageReceived(String message) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handleServerMessage(message);
                        }
                    });
                }
                
                @Override
                public void onConnectionLost() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GameActivity.this, "Lost connection to server", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                
                @Override
                public void onConnectionEstablished() {
                    // Connection restored
                }
            });
        }
    }

    private void handleServerMessage(String message) {
        if (message.startsWith("MOVE_SUCCESS:")) {
            // Parse: MOVE_SUCCESS:playerName:row,col:NEXT:nextPlayerName
            String[] parts = message.split(":");
            if (parts.length >= 5) {
                String playerName = parts[1];
                String[] coords = parts[2].split(",");
                String nextPlayer = parts[4];
                
                int row = Integer.parseInt(coords[0]);
                int col = Integer.parseInt(coords[1]);
                
                // Determine which player made the move
                int player = playerName.equals(player1Name) ? PLAYER1 : PLAYER2;
                
                // Update the board
                gameBoard[row][col] = player;
                updateGamePiece(row, col, player);
                
                // Update turn
                isMyTurn = nextPlayer.equals(player1Name) && myPlayerNumber.equals("PLAYER1") ||
                          nextPlayer.equals(player2Name) && myPlayerNumber.equals("PLAYER2");
                
                updateGameStatus();
            }
        } else if (message.startsWith("GAME_OVER:WINNER:")) {
            // Parse: GAME_OVER:WINNER:playerName:MOVE:row,col
            String[] parts = message.split(":");
            if (parts.length >= 4) {
                String winner = parts[2];
                String[] coords = parts[4].split(",");
                
                int row = Integer.parseInt(coords[0]);
                int col = Integer.parseInt(coords[1]);
                
                // Update the board with the winning move
                int player = winner.equals(player1Name) ? PLAYER1 : PLAYER2;
                gameBoard[row][col] = player;
                updateGamePiece(row, col, player);
                
                gameOver = true;
                showWinDialog(winner);
            }
        } else if (message.startsWith("GAME_DRAW:")) {
            // Parse: GAME_DRAW:MOVE:row,col
            String[] parts = message.split(":");
            if (parts.length >= 3) {
                String[] coords = parts[2].split(",");
                
                int row = Integer.parseInt(coords[0]);
                int col = Integer.parseInt(coords[1]);
                
                // Update the board with the final move
                int player = isMyTurn ? (myPlayerNumber.equals("PLAYER1") ? PLAYER1 : PLAYER2) : 
                             (myPlayerNumber.equals("PLAYER1") ? PLAYER2 : PLAYER1);
                gameBoard[row][col] = player;
                updateGamePiece(row, col, player);
                
                gameOver = true;
                showDrawDialog();
            }
        } else if (message.startsWith("OPPONENT_LEFT:")) {
            String opponentName = message.substring(14);
            Toast.makeText(this, opponentName + " left the game", Toast.LENGTH_LONG).show();
            gameOver = true;
            showGameOverDialog("Opponent disconnected. You win!");
        } else if (message.equals("NOT_YOUR_TURN")) {
            Toast.makeText(this, "It's not your turn!", Toast.LENGTH_SHORT).show();
        } else if (message.startsWith("INVALID_MOVE:")) {
            String reason = message.substring(13);
            Toast.makeText(this, "Invalid move: " + reason, Toast.LENGTH_SHORT).show();
        }
    }

    private void showWinDialog(String winner) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Over!");
        builder.setMessage(winner + " wins! 🎉");
        
        if (isMultiplayer) {
            // In multiplayer, show both "Play Another Game" and "Return to Lobby" options
            builder.setPositiveButton("Play Another Game", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    returnToUserList();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Exit Game", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        } else {
            // In single player, show both options
            builder.setPositiveButton("Play Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    resetGame();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        }
        
        builder.setCancelable(false);
        builder.show();
        
        updateGameStatus();
    }

    private void showDrawDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Over!");
        builder.setMessage("It's a draw! 🤝");
        
        if (isMultiplayer) {
            // In multiplayer, show both "Play Another Game" and "Exit Game" options
            builder.setPositiveButton("Play Another Game", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    returnToUserList();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Exit Game", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        } else {
            // In single player, show both options
            builder.setPositiveButton("Play Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    resetGame();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        }
        
        builder.setCancelable(false);
        builder.show();
    }

    private void returnToUserList() {
        // Notify server that we're leaving the current game
        if (isMultiplayer) {
            ConnectionManager connectionManager = ConnectionManager.getInstance();
            if (connectionManager.isConnected()) {
                connectionManager.sendMessage("LEAVE_GAME");
            }
        }
        
        // Return to UserListActivity
        Intent userListIntent = new Intent(GameActivity.this, UserListActivity.class);
        userListIntent.putExtra("serverIP", getIntent().getStringExtra("serverIP"));
        userListIntent.putExtra("port", getIntent().getIntExtra("port", 8800));
        userListIntent.putExtra("username", getIntent().getStringExtra("username"));
        startActivity(userListIntent);
        finish();
    }

    private void showGameOverDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Over!");
        builder.setMessage(message);
        
        if (isMultiplayer) {
            // In multiplayer, show both "Play Another Game" and "Exit Game" options
            builder.setPositiveButton("Play Another Game", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    returnToUserList();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Exit Game", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        } else {
            // In single player, show both options
            builder.setPositiveButton("Play Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    resetGame();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        }
        
        builder.setCancelable(false);
        builder.show();
    }

    private void resetGame() {
        if (isMultiplayer) {
            // In multiplayer mode, return to user list instead of resetting locally
            Toast.makeText(this, "Returning to user list to start a new game...", Toast.LENGTH_SHORT).show();
            
            // Notify server that we're leaving the current game
            ConnectionManager connectionManager = ConnectionManager.getInstance();
            if (connectionManager.isConnected()) {
                connectionManager.sendMessage("LEAVE_GAME");
            }
            
            // Return to UserListActivity
            Intent userListIntent = new Intent(GameActivity.this, UserListActivity.class);
            userListIntent.putExtra("serverIP", getIntent().getStringExtra("serverIP"));
            userListIntent.putExtra("port", getIntent().getIntExtra("port", 8800));
            userListIntent.putExtra("username", getIntent().getStringExtra("username"));
            startActivity(userListIntent);
            finish();
        } else {
            // Local game reset
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    gameBoard[row][col] = EMPTY;
                    if (gameGrid[row][col] != null) {
                        gameGrid[row][col].setColorFilter(getResources().getColor(R.color.gray));
                    }
                }
            }
            
            currentPlayer = PLAYER1;
            gameOver = false;
            isMyTurn = true; // Reset turn for local game
            updateGameStatus();
            
            Toast.makeText(this, "Game reset! " + player1Name + " starts.", Toast.LENGTH_SHORT).show();
        }
    }

    private void forfeitGame() {
        if (gameOver) {
            Toast.makeText(this, "Game is already over!", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forfeit Game");
        
        String currentPlayerName = (currentPlayer == PLAYER1) ? player1Name : player2Name;
        String otherPlayerName = (currentPlayer == PLAYER1) ? player2Name : player1Name;
        
        builder.setMessage("Are you sure you want to forfeit the game? " + otherPlayerName + " will win.");
        
        builder.setPositiveButton("Yes, Forfeit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // End the game with opponent as winner
                gameOver = true;
                String winnerName = (currentPlayer == PLAYER1) ? player2Name : player1Name;
                
                if (gameStatus != null) {
                    gameStatus.setText(currentPlayerName + " forfeited! " + winnerName + " wins!");
                }
                
                Toast.makeText(GameActivity.this, currentPlayerName + " forfeited the game!", Toast.LENGTH_LONG).show();
                
                if (isMultiplayer) {
                    // Notify server about forfeit and leave game
                    ConnectionManager connectionManager = ConnectionManager.getInstance();
                    if (connectionManager.isConnected()) {
                        connectionManager.sendMessage("LEAVE_GAME");
                    }
                    
                    // Return to user list
                    Intent userListIntent = new Intent(GameActivity.this, UserListActivity.class);
                    userListIntent.putExtra("serverIP", getIntent().getStringExtra("serverIP"));
                    userListIntent.putExtra("port", getIntent().getIntExtra("port", 8800));
                    userListIntent.putExtra("username", getIntent().getStringExtra("username"));
                    startActivity(userListIntent);
                    finish();
                } else {
                    // Show game over dialog for local game
                    showGameOverDialog(winnerName + " wins by forfeit!");
                }
            }
        });
        
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        
        builder.show();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit Game");
        builder.setMessage("Are you sure you want to exit the game?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}