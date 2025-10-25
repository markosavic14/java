
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
    private boolean running;

    public Server() throws Exception {
        this.users = DataManager.loadUsers();
        
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
                Thread clientThread = new Thread(() -> handleClient(clientSocket));
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

    
    private void handleClient(Socket clientSocket) throws IOException {
        String activeUserUsername = null;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String line;
            System.out.println("Reading data from client...");
            
            while ((line = in.readLine()) != null) {
                System.out.println("Received from " + clientSocket.getInetAddress() + ": " + line);
                

                switch(line){
                    case "REGISTER":
                        // TODO
                        register_user(line);
                        break;

                    case "LOGIN":
                        if(!authentication(line)){
                            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                            out.println("AUTH_FAILURE");
                            // Close connection after failed authentication
                            clientSocket.close();
                        }
                        activeUserUsername = line.split(":")[0];
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                        out.println("AUTH_SUCCESS");

                        break;
                    default:
                        clientSocket.close();
                        System.out.println("Client disconnected: " + clientSocket.getInetAddress());
                        break;
                }
            }
        }
    }
    
    private void register_user(String line) {
        
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
