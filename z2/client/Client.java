import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {
    private String username;
    private String password;
    private String role;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean authValid = false;

    public void runClient() {
        try {
            boolean authenticated = false;
            
            while (!authenticated) {
                // Get login credentials from the login window
                loginWindow login = new loginWindow();
                while (!login.isLoggedIn()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                username = login.getUsername();
                password = login.getPassword();
                role = login.getRole();

                // Connect to the server
                connectToServer();
                
                // Send authentication data
                if (sendAuthenticationData()) {
                    System.out.println("Authentication successful!");
                    authenticated = true;
                    
                    if("admin".equals(role)) {
                        waitForMainWindow(() -> new mainWindowAdmin(out, in));
                    } else if("student".equals(role)) {
                        waitForMainWindow(() -> new mainWindowStudent(out, in));
                    }
                } else {
                    javax.swing.JOptionPane.showMessageDialog(null, "Authentication failed. Please check your credentials.", "Login Failed", javax.swing.JOptionPane.ERROR_MESSAGE);
                    System.out.println("Authentication failed. Asking for credentials again...");
                    
                    // Disconnect current connection before retrying
                    disconnect();
                }
            }
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }
    
    private void connectToServer() throws IOException {
        System.out.println("Connecting to server on localhost:8800...");
        socket = new Socket("127.0.0.1", 8800);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Connected to server successfully!");
    }
    
    private boolean sendAuthenticationData() throws IOException {
        System.out.println("Sending authentication data...");
        
        // Send username:password:role
        out.println(username + ":" + password + ":" + role);
        System.out.println("Sent> " + username + ":" + password + ":" + role);

        // Wait for confirmation from server
        String response = in.readLine();
        System.out.println("Received from server: " + response);
        return "AUTH_SUCCESS".equals(response);
    }
    
    private void waitForMainWindow(Runnable windowCreator) {
        final Object lock = new Object();
        final boolean[] windowClosed = {false};
        
        javax.swing.SwingUtilities.invokeLater(() -> {
            // Create the window
            windowCreator.run();

            // Wait for the EDT to be idle and then
            // periodically check if there are any visible windows
            new Thread(() -> {
                try {
                    // Wait a bit for the window to be created and shown
                    Thread.sleep(500);
                    
                    // Keep checking if any windows are still visible
                    while (true) {
                        boolean hasVisibleWindows = false;
                        
                        // Check all windows
                        java.awt.Window[] windows = java.awt.Window.getWindows();
                        for (java.awt.Window window : windows) {
                            if (window.isVisible() && window.isDisplayable()) {
                                hasVisibleWindows = true;
                                break;
                            }
                        }
                        
                        if (!hasVisibleWindows) {
                            synchronized (lock) {
                                windowClosed[0] = true;
                                lock.notify();
                            }
                            break;
                        }
                        
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    synchronized (lock) {
                        windowClosed[0] = true;
                        lock.notify();
                    }
                }
            }).start();
        });
        
        // Wait for the window to close
        synchronized (lock) {
            while (!windowClosed[0]) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        System.out.println("Main window closed, proceeding with disconnect...");
    }
    
    private void disconnect() {
        try {
            if (out != null) {
                out.println("exit");
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Disconnected from server.");
            }
        } catch (IOException e) {
            System.err.println("Error during disconnect: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.runClient();
    }
}
