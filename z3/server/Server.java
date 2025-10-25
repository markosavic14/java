
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
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
    private boolean running;

    public Server() throws Exception {
        this.users = DataManager.loadUsers();
        this.activeUsers = new ArrayList<>(); // Initialize active users list
        
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

    private String handleLogin(String loginData, Socket clientSocket) throws IOException {
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
            
            PrintWriter loginOut = new PrintWriter(clientSocket.getOutputStream(), true);
            loginOut.println("AUTH_SUCCESS");
            return username; // Return the username for tracking
        } else {
            // Failed authentication
            PrintWriter loginOut = new PrintWriter(clientSocket.getOutputStream(), true);
            loginOut.println("AUTH_FAILURE");
            // Close connection after failed authentication
            clientSocket.close();
            return null;
        }
    }

    
    private void handleClient(Socket clientSocket) throws IOException {
        String activeUserUsername = null;
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
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                        out.println("REGISTER_SUCCESS");
                    } else {
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                        out.println("REGISTER_FAILURE");
                    }
                } else if(line.startsWith("LOGIN:")) {
                    // Extract the credentials part after "LOGIN:"
                    String credentials = line.substring(6); // Remove "LOGIN:" prefix
                    activeUserUsername = handleLogin(credentials, clientSocket);
                } else if(line.equals("GET_ACTIVE_USERS")) {
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    // Send the list of active users as a comma-separated string
                    String activeUsersList = String.join(",", activeUsers);
                    out.println("ACTIVE_USERS:" + activeUsersList);
                } else {
                    // Unknown command
                    PrintWriter errorOut = new PrintWriter(clientSocket.getOutputStream(), true);
                    errorOut.println("UNKNOWN_COMMAND");
                    clientSocket.close();
                    System.out.println("Client disconnected: " + clientSocket.getInetAddress());
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
