package com.example.z3;

import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectionManager {
    private static ConnectionManager instance;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private ExecutorService executorService;
    private Handler mainHandler;
    private boolean isConnected = false;
    private String serverIP;
    private int port;
    private String username;
    private ConnectionListener listener;

    // Interface for handling connection events
    public interface ConnectionListener {
        void onMessageReceived(String message);
        void onConnectionLost();
        void onConnectionEstablished();
    }

    private ConnectionManager() {
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public static synchronized ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }

    public void setConnectionListener(ConnectionListener listener) {
        this.listener = listener;
    }

    public boolean isConnected() {
        return isConnected && socket != null && !socket.isClosed();
    }

    public void connect(String serverIP, int port, String username, String password, ConnectionCallback callback) {
        this.serverIP = serverIP;
        this.port = port;
        this.username = username;

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // Create socket connection
                    socket = new Socket(serverIP, port);
                    out = new PrintWriter(socket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    // Send login data
                    String loginData = "LOGIN:" + username + ":" + password;
                    out.println(loginData);

                    // Read server response
                    String response = in.readLine();

                    if ("AUTH_SUCCESS".equals(response)) {
                        isConnected = true;
                        
                        // Start message listener thread
                        Thread listenerThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                listenForMessages();
                            }
                        });
                        listenerThread.start();

                        // Notify success on main thread
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (callback != null) {
                                    callback.onSuccess();
                                }
                                if (listener != null) {
                                    listener.onConnectionEstablished();
                                }
                            }
                        });
                    } else {
                        // Authentication failed
                        closeConnection();
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (callback != null) {
                                    callback.onFailure(response != null ? response : "AUTH_FAILURE");
                                }
                            }
                        });
                    }

                } catch (IOException e) {
                    closeConnection();
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onFailure("Connection error: " + e.getMessage());
                            }
                        }
                    });
                }
            }
        });
    }

    public void reconnect(ConnectionCallback callback) {
        if (serverIP != null && username != null) {
            // Use dummy password for reconnection since we're already authenticated
            connect(serverIP, port, username, "dummy", callback);
        } else {
            if (callback != null) {
                callback.onFailure("No previous connection details available");
            }
        }
    }

    private void listenForMessages() {
        try {
            String message;
            while (isConnected && (message = in.readLine()) != null) {
                final String finalMessage = message;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onMessageReceived(finalMessage);
                        }
                    }
                });
            }
        } catch (IOException e) {
            if (isConnected) {
                isConnected = false;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onConnectionLost();
                        }
                    }
                });
            }
        }
    }

    public void sendMessage(String message) {
        if (isConnected && out != null) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        out.println(message);
                    } catch (Exception e) {
                        // Handle send error
                        isConnected = false;
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null) {
                                    listener.onConnectionLost();
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    public void closeConnection() {
        isConnected = false;
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            // Handle cleanup errors silently
        }
    }

    public void shutdown() {
        closeConnection();
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    // Callback interface for connection operations
    public interface ConnectionCallback {
        void onSuccess();
        void onFailure(String error);
    }

    // Get current connection details
    public String getServerIP() {
        return serverIP;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }
}