
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.lang.reflect.Array;

public class Server {
    private ServerSocket serverSocket;
    private ArrayList<User> users;
    private boolean running;

    public Server() throws Exception {
        this.users = getUsers();
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

    private ArrayList<Course> getCourses(String indexNum) {
        ArrayList<Course> courses = new ArrayList<>();
        ArrayList<Student> students = getStudents();
        for (Student student : students) {
            if (student.getIndexNum().equals(indexNum)) {
                courses = student.getCourses();
                break;
            }
        }
        return courses;
    }

    private ArrayList<Student> getStudents() {
        ArrayList<Student> students = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/students.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Student data: " + line);
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String username = parts[0].trim();
                    String name = parts[1].trim();
                    String lastName = parts[2].trim();
                    String indexNum = parts[3].trim();
                    String jmbg = parts[4].trim();
                    try {
                        Student student = new Student(username, name, lastName, indexNum, jmbg);
                        students.add(student);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error creating student: " + e.getMessage());
                    }
                }
            }
        } catch (IOException | NullPointerException e) {
            System.err.println("Error reading students.txt: " + e.getMessage());
        }
        return students;
    }

    private ArrayList<User> getUsers() throws IOException{
        ArrayList<User> newUsers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/users.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("User data: " + line);
                // You can parse and store user data as needed here
                String[] parts = line.split(":");
                if (parts.length == 3) {
                    String username = parts[0];
                    String password = parts[1];
                    String role = parts[2];
                    newUsers.add(new User(username, password, role));
                }
            }
        } catch (IOException | NullPointerException e) {
            System.err.println("Error reading users.txt: " + e.getMessage());
        }
        return newUsers;
    }

    private boolean checkCredentials(String username, String password, String role) {
        for (User user : users) {
            if (user.getUsername().equals(username) &&
                user.getPassword().equals(password) &&
                user.getRole().equals(role)) {
                return true;
            }
        }
        return false;
    }

    private boolean authentication(String data) {
        // Expected format: username:password:role
        String[] parts = data.split(":");
        if (parts.length == 3) {
            String username = parts[0];
            String password = parts[1];
            String role = parts[2];
            System.out.println("Received authentication data:");
            System.out.println("Username: " + username);
            System.out.println("Password: " + password);
            System.out.println("Role: " + role);
            return checkCredentials(username, password, role);
        } else {
            System.out.println("Invalid authentication data format.");
            return false;
        }
    }

    private String getIndexForUsername(String username) {
        ArrayList<Student> students = getStudents();
        for (Student student : students) {
            if (student.getUsername().equals(username)) {
                return student.getIndexNum();
            }
        }
        return null; // Return null if username not found
    }
    
    private void handleClient(Socket clientSocket) {
        String activeUserUsername = null;
        String activeUserIndexNum = null;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String line;
            System.out.println("Reading data from client...");
            
            while ((line = in.readLine()) != null) {
                System.out.println("Received from " + clientSocket.getInetAddress() + ": " + line);
                
                //Read authentication data
                // Expected format: username:password:role
                if (authentication(line)) {
                    activeUserUsername = line.split(":")[0];
                    activeUserIndexNum = getIndexForUsername(activeUserUsername);
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    out.println("AUTH_SUCCESS");
                    // After successful authentication, handle further client requests
                    // For example, sending the list of students if the role is admin
                    // Listen for requests
                    while ((line = in.readLine()) != null) {
                        System.out.println("Received from " + clientSocket.getInetAddress() + ": " + line);
                        
                        if (line.trim().toUpperCase().startsWith("ADD_STUDENT:")) {
                            // Format: ADD_STUDENT:ime:prezime:brojIndeksa:jmbg:predmetiOcene:korisnickoIme:lozinka
                            String response = handleAddStudent(line);
                            out.println(response);
                        } else {
                            switch (line.trim().toUpperCase()) {
                                case "GET_STUDENTS":
                                    ArrayList<Student> students = getStudents();
                                    StringBuilder studentsList = new StringBuilder();
                                    for (Student student : students) {
                                        studentsList.append(student.getName()).append(", ");
                                    }
                                    // Remove trailing comma and space
                                    if (studentsList.length() > 0) {
                                        studentsList.setLength(studentsList.length() - 2);
                                    }
                                    out.println(studentsList.toString());
                                    break;
                                case "GET_SUBJECTS":
                                    ArrayList<Course> courses = getCourses(activeUserIndexNum);
                                    StringBuilder subjectsList = new StringBuilder();
                                    for (Course course : courses) {
                                        subjectsList.append(course.getName()).append(", ");
                                    }
                                    // Remove trailing comma and space
                                    if (subjectsList.length() > 0) {
                                        subjectsList.setLength(subjectsList.length() - 2);
                                    }
                                    out.println(subjectsList.toString());
                                    break;
                                case "EXIT":
                                    System.out.println("Client requested to disconnect");
                                    break;
                                default:
                                    System.out.println("Unknown command: " + line);
                                    break;
                            }
                        }
                        
                        if ("EXIT".equalsIgnoreCase(line.trim())) {
                            break;
                        }
                    }
                } else {
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    out.println("AUTH_FAILURE");
                    // Close connection after failed authentication
                    clientSocket.close();
                    break;
                }

                if ("exit".equalsIgnoreCase(line.trim())) {
                    System.out.println("Client requested to disconnect");
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Client disconnected: " + clientSocket.getInetAddress());
            } catch (IOException e) {
            }
        }
    }

    private String handleAddStudent(String command) {
        try {
            // Parse the command: ADD_STUDENT:ime:prezime:brojIndeksa:jmbg:predmetiOcene:korisnickoIme:lozinka:role
            String[] parts = command.split(":", 9); // Split into max 9 parts
            if (parts.length != 9) {
                return "ERROR: Invalid ADD_STUDENT format. Expected 9 parts.";
            }
            
            String cmd = parts[0]; // Should be "ADD_STUDENT"
            String ime = parts[1];
            String prezime = parts[2];
            String brojIndeksa = parts[3];
            String jmbg = parts[4];
            String predmetiOcene = parts[5];
            String korisnickoIme = parts[6];
            String lozinka = parts[7];
            String role = parts[8];
            
            System.out.println("Adding student: " + ime + " " + prezime + " (" + brojIndeksa + ")");
            
            // Check if username already exists
            for (User user : users) {
                if (user.getUsername().equals(korisnickoIme)) {
                    return "ERROR: Username already exists.";
                }
            }
            
            ArrayList<Student> students = getStudents();
            // Check if student with same index number already exists
            for (Student student : students) {
                if (student.getIndexNum().equals(brojIndeksa)) {
                    return "ERROR: Student with this index number already exists.";
                }
            }
            
            // Create new student
            try {
                Student newStudent = new Student(korisnickoIme, ime, prezime, brojIndeksa, jmbg);
                students.add(newStudent);
                
                // Add user credentials
                User newUser = new User(korisnickoIme, lozinka, role);
                users.add(newUser);
                users = getUsers();
                
                // Save to files
                saveStudentToFile(korisnickoIme, ime, prezime, brojIndeksa, jmbg, predmetiOcene);
                saveUserToFile(korisnickoIme, lozinka, role);
                
                System.out.println("Student successfully added: " + ime + " " + prezime);
                return "STUDENT_ADDED_SUCCESS";
                
            } catch (IllegalArgumentException e) {
                System.err.println("Error creating student: " + e.getMessage());
                return "ERROR: " + e.getMessage();
            }
            
        } catch (Exception e) {
            System.err.println("Error handling ADD_STUDENT: " + e.getMessage());
            return "ERROR: Failed to add student.";
        }
    }
    
    private void saveStudentToFile(String username, String ime, String prezime, String brojIndeksa, String jmbg, String predmetiOcene) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("server/students.txt", true))) {
            // Format: username,name,lastName,indexNum,jmbg
            // Note: We're using korisnickoIme as username, not brojIndeksa
            String studentLine = String.format("%s,%s,%s,%s,%s%n", username, ime, prezime, brojIndeksa, jmbg);
            writer.write(studentLine);
            System.out.println("Student saved to students.txt");
        } catch (IOException e) {
            System.err.println("Error saving student to file: " + e.getMessage());
        }
    }
    
    private void saveUserToFile(String username, String password, String role) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("server/users.txt", true))) {
            // Format: username:password:role
            String userLine = String.format("%s:%s:%s%n", username, password, role);
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
