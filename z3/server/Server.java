
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.BufferedWriter;

public class Server {
    private ServerSocket serverSocket;
    private ArrayList<User> users;
    private ArrayList<String> activeUsers; // Track currently logged in users
    private Map<String, PrintWriter> userConnections; // Map username to their PrintWriter for communication
    private Map<String, GameRoom> gameRooms; // Map game room ID to game room
    private Map<String, String> playerToRoom; // Map player username to their current room ID
    private int gameRoomCounter = 1; // Counter for generating unique room IDs
    private boolean running;

    public Server() throws Exception {
        this.users = DataManager.loadUsers();
        this.activeUsers = new ArrayList<>(); // Initialize active users list
        this.userConnections = new HashMap<>(); // Initialize user connections map
        this.gameRooms = new ConcurrentHashMap<>(); // Initialize game rooms map
        this.playerToRoom = new ConcurrentHashMap<>(); // Initialize player to room map
        
        // If no serialized data exists, import from text files
        if (this.users.isEmpty()) {
            this.users = getUsers();
            DataManager.saveUsers(this.users);
        }
        
        this.running = false;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(8800);
        running = true;
        System.out.println("Server started on port 8800");
        System.out.println("Waiting for client connections...");
        
        while (running) {
            try {
                // Wait for a client connection
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected from: " + clientSocket.getInetAddress());
                
                // Handle the client in a separate thread to allow multiple connections
                Thread clientThread = new Thread(() -> {
                    try {
                        handleClient(clientSocket);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                });
                clientThread.start();
                
            } catch (IOException e) {
                if (running) {
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }
        }
    }

    private ArrayList<User> getUsers() throws IOException{
        ArrayList<User> newUsers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/users.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("User data: " + line);
                // TODO Parse and store user data as needed here
                String[] parts = line.split(":");
                if (parts.length == 3) {
                    String username = parts[0];
                    String password = parts[1];
                    newUsers.add(new User(username, password));
                }
            }
        } catch (IOException | NullPointerException e) {
            System.err.println("Error reading users.txt: " + e.getMessage());
        }
        return newUsers;
    }

    private boolean checkCredentials(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) &&
                user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    private boolean authentication(String data) {
        // TODO Expected format: username:password
        String[] parts = data.split(":");
        if (parts.length == 2) {
            String username = parts[0];
            String password = parts[1];
            System.out.println("Received authentication data:");
            System.out.println("Username: " + username);
            System.out.println("Password: " + password);
            return checkCredentials(username, password);
        } else {
            System.out.println("Invalid authentication data format.");
            return false;
        }
    }

    private String handleLogin(String loginData, Socket clientSocket, PrintWriter out) throws IOException {
        if(authentication(loginData)){
            // Extract username from login data
            String username = loginData.split(":")[0];
            
            // Add user to active users list if not already present
            synchronized(activeUsers) {
                if (!activeUsers.contains(username)) {
                    activeUsers.add(username);
                    System.out.println("User " + username + " added to active users list");
                }
            }
            
            // Store the connection for this user
            synchronized(userConnections) {
                userConnections.put(username, out);
                System.out.println("Stored connection for user: " + username);
            }
            
            out.println("AUTH_SUCCESS");
            return username; // Return the username for tracking
        } else {
            // Failed authentication
            out.println("AUTH_FAILURE");
            // Don't close connection here - let it be handled by the caller
            return null;
        }
    }

    
    private void handleClient(Socket clientSocket) throws IOException {
        String activeUserUsername = null;
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String line;
            System.out.println("Reading data from client...");
            
            while ((line = in.readLine()) != null) {
                System.out.println("Received from " + clientSocket.getInetAddress() + ": " + line);
                
                // Check if it's a registration attempt (format: REGISTER:username:password)
                if(line.startsWith("REGISTER:")) {
                    // Extract the credentials part after "REGISTER:"
                    String credentials = line.substring(9); // Remove "REGISTER:" prefix
                    if(register_user(credentials)){
                        out.println("REGISTER_SUCCESS");
                    } else {
                        out.println("REGISTER_FAILURE");
                    }
                    // Close connection after registration attempt
                    clientSocket.close();
                    return;
                    
                } else if(line.startsWith("LOGIN:")) {
                    // Extract the credentials part after "LOGIN:"
                    String credentials = line.substring(6); // Remove "LOGIN:" prefix
                    activeUserUsername = handleLogin(credentials, clientSocket, out);
                    if (activeUserUsername == null) {
                        // Login failed, close connection
                        clientSocket.close();
                        return;
                    }
                    // Login successful, continue to handle other commands
                    
                } else if(line.equals("GET_ACTIVE_USERS")) {
                    // Send the list of active users with their status (available/in_game)
                    StringBuilder userListBuilder = new StringBuilder();
                    synchronized(activeUsers) {
                        for (int i = 0; i < activeUsers.size(); i++) {
                            String username = activeUsers.get(i);
                            boolean inGame = playerToRoom.containsKey(username);
                            userListBuilder.append(username);
                            if (inGame) {
                                userListBuilder.append("(in_game)");
                            }
                            if (i < activeUsers.size() - 1) {
                                userListBuilder.append(",");
                            }
                        }
                    }
                    out.println("ACTIVE_USERS:" + userListBuilder.toString());
                    
                } else if(line.startsWith("CONNECT_REQUEST:")) {
                    // Handle connection request (format: CONNECT_REQUEST:targetUsername)
                    String targetUsername = line.substring(16); // Remove "CONNECT_REQUEST:" prefix
                    handleConnectionRequest(activeUserUsername, targetUsername);
                    
                } else if(line.startsWith("CONNECT_ACCEPT:")) {
                    // Handle connection acceptance (format: CONNECT_ACCEPT:requesterUsername)
                    String requesterUsername = line.substring(15); // Remove "CONNECT_ACCEPT:" prefix
                    handleConnectionResponse(requesterUsername, activeUserUsername, true);
                    
                } else if(line.startsWith("CONNECT_DECLINE:")) {
                    // Handle connection decline (format: CONNECT_DECLINE:requesterUsername)
                    String requesterUsername = line.substring(16); // Remove "CONNECT_DECLINE:" prefix
                    handleConnectionResponse(requesterUsername, activeUserUsername, false);
                    
                } else if(line.startsWith("MAKE_MOVE:")) {
                    // Handle game move (format: MAKE_MOVE:column)
                    String columnStr = line.substring(10); // Remove "MAKE_MOVE:" prefix
                    try {
                        int column = Integer.parseInt(columnStr);
                        handleGameMove(activeUserUsername, column);
                    } catch (NumberFormatException e) {
                        out.println("INVALID_MOVE");
                    }
                    
                } else if(line.equals("LEAVE_GAME")) {
                    // Handle player leaving game
                    handleLeaveGame(activeUserUsername);
                    
                } else if(line.equals("PLAY_AGAIN_REQUEST")) {
                    // Handle play again request
                    handlePlayAgainRequest(activeUserUsername);
                    
                } else {
                    // Unknown command
                    out.println("UNKNOWN_COMMAND");
                    System.out.println("Unknown command from " + clientSocket.getInetAddress() + ": " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            // Remove user from active users list when they disconnect
            if (activeUserUsername != null) {
                synchronized(activeUsers) {
                    activeUsers.remove(activeUserUsername);
                    System.out.println("User " + activeUserUsername + " removed from active users list");
                }
                synchronized(userConnections) {
                    userConnections.remove(activeUserUsername);
                    System.out.println("Removed connection for user: " + activeUserUsername);
                }
            }
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }

    private boolean register_user(String line) {
        // Parse the registration data (expected format: username:password)
        String[] parts = line.split(":");
        if (parts.length >= 2) {
            String username = parts[0];
            String password = parts[1];
            
            // Check if user already exists
            boolean userExists = false;
            for (User user : users) {
                if (user.getUsername().equals(username)) {
                    userExists = true;
                    break;
                }
            }
            
            if (!userExists) {
                // Create new user and add to the list
                User newUser = new User(username, password);
                users.add(newUser);
                
                // Save to file
                saveUserToFile(username, password);
                
                // Save updated users list
                try {
                    DataManager.saveUsers(users);
                    System.out.println("User registered successfully: " + username);
                    return true;
                } catch (Exception e) {
                    System.err.println("Error saving user data: " + e.getMessage());
                    return false;
                }
            } else {
                System.out.println("User already exists: " + username);
                return false;
            }
        } else {
            System.out.println("Invalid registration data format.");
            return false;
        }
    }

    private void saveUserToFile(String username, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("server/users.txt", true))) {
            // Format: username:password:role
            String userLine = String.format("%s:%s%n", username, password);
            writer.write(userLine);
            System.out.println("User credentials saved to users.txt");
        } catch (IOException e) {
            System.err.println("Error saving user to file: " + e.getMessage());
        }
    }

    private void handleConnectionRequest(String requesterUsername, String targetUsername) {
        System.out.println("Connection request from " + requesterUsername + " to " + targetUsername);
        
        // Check if target user is online
        synchronized(userConnections) {
            PrintWriter targetConnection = userConnections.get(targetUsername);
            if (targetConnection != null) {
                // Send connection request to target user
                targetConnection.println("CONNECTION_REQUEST:" + requesterUsername);
                System.out.println("Sent connection request to " + targetUsername);
            } else {
                // Target user is not online, notify requester
                PrintWriter requesterConnection = userConnections.get(requesterUsername);
                if (requesterConnection != null) {
                    requesterConnection.println("USER_OFFLINE:" + targetUsername);
                }
                System.out.println("Target user " + targetUsername + " is not online");
            }
        }
    }

    private void handleConnectionResponse(String requesterUsername, String responderUsername, boolean accepted) {
        System.out.println("Connection response from " + responderUsername + " to " + requesterUsername + ": " + (accepted ? "ACCEPTED" : "DECLINED"));
        
        synchronized(userConnections) {
            PrintWriter requesterConnection = userConnections.get(requesterUsername);
            if (requesterConnection != null) {
                if (accepted) {
                    // Create a new game room for these two players
                    String roomId = "room_" + gameRoomCounter++;
                    GameRoom gameRoom = new GameRoom(roomId, requesterUsername, responderUsername);
                    gameRooms.put(roomId, gameRoom);
                    playerToRoom.put(requesterUsername, roomId);
                    playerToRoom.put(responderUsername, roomId);
                    
                    // Notify both players about the game start
                    requesterConnection.println("GAME_START:" + responderUsername + ":" + roomId + ":PLAYER1");
                    PrintWriter responderConnection = userConnections.get(responderUsername);
                    if (responderConnection != null) {
                        responderConnection.println("GAME_START:" + requesterUsername + ":" + roomId + ":PLAYER2");
                    }
                    
                    System.out.println("Created game room " + roomId + " for " + requesterUsername + " vs " + responderUsername);
                } else {
                    requesterConnection.println("CONNECTION_DECLINED:" + responderUsername);
                }
            } else {
                System.out.println("Requester " + requesterUsername + " is no longer online");
            }
        }
    }

    // Method to get the current list of active users
    public ArrayList<String> getActiveUsers() {
        synchronized(activeUsers) {
            return new ArrayList<>(activeUsers);
        }
    }

    public void stop() throws IOException {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
            System.out.println("Server stopped.");
        }
    }

    private void handleGameMove(String playerUsername, int column) {
        String roomId = playerToRoom.get(playerUsername);
        if (roomId == null) {
            // Player is not in a game
            PrintWriter playerConnection = userConnections.get(playerUsername);
            if (playerConnection != null) {
                playerConnection.println("NOT_IN_GAME");
            }
            return;
        }

        GameRoom gameRoom = gameRooms.get(roomId);
        if (gameRoom == null) {
            System.err.println("Game room " + roomId + " not found");
            return;
        }

        // Process the move
        String result = gameRoom.makeMove(playerUsername, column);
        
        // Send the result to both players
        PrintWriter player1Connection = userConnections.get(gameRoom.getPlayer1());
        PrintWriter player2Connection = userConnections.get(gameRoom.getPlayer2());
        
        if (player1Connection != null) {
            player1Connection.println(result);
        }
        if (player2Connection != null) {
            player2Connection.println(result);
        }

        // Check if game is over
        if (result.startsWith("GAME_OVER:") || result.startsWith("GAME_DRAW")) {
            // Remove the game room and player mappings
            gameRooms.remove(roomId);
            playerToRoom.remove(gameRoom.getPlayer1());
            playerToRoom.remove(gameRoom.getPlayer2());
            System.out.println("Game " + roomId + " ended");
        }
    }

    private void handleLeaveGame(String playerUsername) {
        String roomId = playerToRoom.get(playerUsername);
        if (roomId == null) {
            return; // Player is not in a game
        }

        GameRoom gameRoom = gameRooms.get(roomId);
        if (gameRoom == null) {
            return;
        }

        // Notify the other player that opponent left
        String opponentUsername = gameRoom.getPlayer1().equals(playerUsername) 
                                ? gameRoom.getPlayer2() 
                                : gameRoom.getPlayer1();
        
        PrintWriter opponentConnection = userConnections.get(opponentUsername);
        if (opponentConnection != null) {
            opponentConnection.println("OPPONENT_LEFT:" + playerUsername);
        }

        // Clean up the game room
        gameRooms.remove(roomId);
        playerToRoom.remove(gameRoom.getPlayer1());
        playerToRoom.remove(gameRoom.getPlayer2());
        
        System.out.println("Player " + playerUsername + " left game " + roomId);
    }

    private void handlePlayAgainRequest(String playerUsername) {
        String roomId = playerToRoom.get(playerUsername);
        if (roomId == null) {
            return; // Player is not in a game
        }

        GameRoom gameRoom = gameRooms.get(roomId);
        if (gameRoom == null) {
            return;
        }

        // Check if this player wants to play again
        boolean bothWantToPlayAgain = gameRoom.setPlayAgainRequest(playerUsername);
        
        if (bothWantToPlayAgain) {
            // Both players want to play again, start a new game in the same room
            gameRoom.resetGame();
            
            // Notify both players that a new game is starting
            PrintWriter player1Connection = userConnections.get(gameRoom.getPlayer1());
            PrintWriter player2Connection = userConnections.get(gameRoom.getPlayer2());
            
            String newGameMessage = "NEW_GAME_START:" + gameRoom.getPlayer1() + ":" + gameRoom.getPlayer2() + ":" + roomId;
            
            if (player1Connection != null) {
                player1Connection.println(newGameMessage + ":PLAYER1");
            }
            if (player2Connection != null) {
                player2Connection.println(newGameMessage + ":PLAYER2");
            }
            
            System.out.println("New game started in room " + roomId + " between " + gameRoom.getPlayer1() + " and " + gameRoom.getPlayer2());
        } else {
            // Only one player wants to play again so far, notify the other player
            String otherPlayerUsername = gameRoom.getPlayer1().equals(playerUsername) 
                                       ? gameRoom.getPlayer2() 
                                       : gameRoom.getPlayer1();
            
            PrintWriter otherPlayerConnection = userConnections.get(otherPlayerUsername);
            if (otherPlayerConnection != null) {
                otherPlayerConnection.println("OPPONENT_WANTS_REMATCH:" + playerUsername);
            }
            
            // Notify the requesting player that we're waiting for opponent
            PrintWriter playerConnection = userConnections.get(playerUsername);
            if (playerConnection != null) {
                playerConnection.println("WAITING_FOR_OPPONENT_REMATCH");
            }
            
            System.out.println("Player " + playerUsername + " wants to play again, waiting for " + otherPlayerUsername);
        }
    }

    // Inner class for managing game rooms
    private static class GameRoom {
        private static final int ROWS = 6;
        private static final int COLS = 7;
        private static final int EMPTY = 0;
        private static final int PLAYER1 = 1;
        private static final int PLAYER2 = 2;

        private String roomId;
        private String player1Username;
        private String player2Username;
        private String currentPlayerUsername;
        private int[][] gameBoard;
        private boolean gameOver;
        private boolean player1WantsRematch;
        private boolean player2WantsRematch;

        public GameRoom(String roomId, String player1, String player2) {
            this.roomId = roomId;
            this.player1Username = player1;
            this.player2Username = player2;
            this.currentPlayerUsername = player1; // Player 1 starts
            this.gameBoard = new int[ROWS][COLS];
            this.gameOver = false;
            this.player1WantsRematch = false;
            this.player2WantsRematch = false;
            
            // Initialize empty board
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    gameBoard[row][col] = EMPTY;
                }
            }
        }

        public String makeMove(String playerUsername, int column) {
            if (gameOver) {
                return "GAME_ALREADY_OVER";
            }

            // Check if it's the player's turn
            if (!playerUsername.equals(currentPlayerUsername)) {
                return "NOT_YOUR_TURN";
            }

            // Validate column
            if (column < 0 || column >= COLS || gameBoard[0][column] != EMPTY) {
                return "INVALID_MOVE:Column " + column + " is invalid or full";
            }

            // Find the lowest empty row in the column
            int row = -1;
            for (int r = ROWS - 1; r >= 0; r--) {
                if (gameBoard[r][column] == EMPTY) {
                    row = r;
                    break;
                }
            }

            if (row == -1) {
                return "INVALID_MOVE:Column is full";
            }

            // Make the move
            int playerNumber = playerUsername.equals(player1Username) ? PLAYER1 : PLAYER2;
            gameBoard[row][column] = playerNumber;

            // Check for win
            if (checkWin(row, column, playerNumber)) {
                gameOver = true;
                return "GAME_OVER:WINNER:" + playerUsername + ":MOVE:" + row + "," + column;
            }

            // Check for draw
            if (isBoardFull()) {
                gameOver = true;
                return "GAME_DRAW:MOVE:" + row + "," + column;
            }

            // Switch turns
            currentPlayerUsername = playerUsername.equals(player1Username) ? player2Username : player1Username;

            // Return the move result
            return "MOVE_SUCCESS:" + playerUsername + ":" + row + "," + column + ":NEXT:" + currentPlayerUsername;
        }

        private boolean checkWin(int row, int col, int player) {
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

        public String getPlayer1() {
            return player1Username;
        }

        public String getPlayer2() {
            return player2Username;
        }

        public String getCurrentPlayer() {
            return currentPlayerUsername;
        }

        public String getRoomId() {
            return roomId;
        }

        public boolean setPlayAgainRequest(String playerUsername) {
            if (playerUsername.equals(player1Username)) {
                player1WantsRematch = true;
            } else if (playerUsername.equals(player2Username)) {
                player2WantsRematch = true;
            }
            
            // Return true if both players want to play again
            return player1WantsRematch && player2WantsRematch;
        }

        public void resetGame() {
            // Reset the game board
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    gameBoard[row][col] = EMPTY;
                }
            }
            
            // Reset game state
            this.gameOver = false;
            this.currentPlayerUsername = player1Username; // Player 1 starts again
            this.player1WantsRematch = false;
            this.player2WantsRematch = false;
        }
    }

    public static void main(String[] args) {
        try {
            Server server = new Server();
            
            // Add shutdown hook to gracefully stop the server
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    server.stop();
                } catch (IOException e) {
                    System.err.println("Error stopping server: " + e.getMessage());
                }
            }));
            
            server.start();
        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
