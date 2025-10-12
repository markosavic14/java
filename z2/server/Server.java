
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
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
    private ArrayList<Student> students;
    private Map<String, Course> courses;
    private boolean running;

    public Server() throws Exception {
        this.users = DataManager.loadUsers();
        this.students = DataManager.loadStudents();
        this.courses = DataManager.loadCourses();
        
        // If no serialized data exists, import from text files
        if (this.users.isEmpty() || this.students.isEmpty()) {
            this.users = getUsers();
            this.students = getStudents();
            DataManager.saveUsers(this.users);
            DataManager.saveStudents(this.students);
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

    private ArrayList<Course> getCourses(String indexNum) {
        ArrayList<Course> studentCourses = new ArrayList<>();
        for (Student student : students) {
            if (student.getIndexNum().equals(indexNum)) {
                studentCourses = student.getCourses();
                break;
            }
        }
        return studentCourses;
    }
    
    private Course getCourseByName(String courseName) {
        return courses.get(courseName);
    }
    
    private void addCourse(String courseName, Map<String, String> categories) {
        Course course = new Course(courseName);
        for (Map.Entry<String, String> entry : categories.entrySet()) {
            String categoryName = entry.getKey();
            String[] pointsData = entry.getValue().split(":");
            if (pointsData.length == 2) {
                int points = Integer.parseInt(pointsData[0]);
                int minPoints = Integer.parseInt(pointsData[1]);
                course.dodajKategoriju(categoryName, points, minPoints);
            }
        }
        courses.put(courseName, course);
        DataManager.saveCourses(courses); // Save after adding
    }

    private ArrayList<Student> getStudents() {
        ArrayList<Student> loadedStudents = new ArrayList<>();
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
                        loadedStudents.add(student);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error creating student: " + e.getMessage());
                    }
                }
            }
        } catch (IOException | NullPointerException e) {
            System.err.println("Error reading students.txt: " + e.getMessage());
        }
        return loadedStudents;
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
        // TODO Expected format: username:password:role
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
                        } else if (line.trim().toUpperCase().startsWith("ADD_COURSE:")) {
                            // Format: ADD_COURSE:courseName:categories (categories format: "name1=points1:minPoints1,name2=points2:minPoints2")
                            String response = handleAddCourse(line);
                            out.println(response);
                        } else if (line.trim().toUpperCase().startsWith("EDIT_STUDENT:")) {
                            // Format: EDIT_STUDENT:oldUsername:newIme:newPrezime:newBrojIndeksa:newJmbg:newUsername:newPassword:newRole
                            String response = handleEditStudent(line);
                            out.println(response);
                        } else if (line.trim().toUpperCase().startsWith("DELETE_STUDENT:")) {
                            // Format: DELETE_STUDENT:username
                            String response = handleDeleteStudent(line);
                            out.println(response);
                        } else if (line.trim().toUpperCase().startsWith("GET_STUDENT_DETAILS:")) {
                            // Format: GET_STUDENT_DETAILS:username
                            String response = handleGetStudentDetails(line);
                            out.println(response);
                        } else if (line.trim().toUpperCase().startsWith("GET_ALL_COURSES")) {
                            // Return all available courses
                            String response = handleGetAllCourses();
                            out.println(response);
                        } else {
                            switch (line.trim().toUpperCase()) {
                                case "GET_STUDENTS":
                                    StringBuilder studentsListBuilder = new StringBuilder();
                                    for (Student student : students) {
                                        studentsListBuilder.append(student.getName()).append(" ").append(student.getLastName())
                                                          .append(" (").append(student.getIndexNum()).append(")")
                                                          .append(":").append(student.getUsername()).append(", ");
                                    }
                                    // Remove trailing comma and space
                                    if (studentsListBuilder.length() > 0) {
                                        studentsListBuilder.setLength(studentsListBuilder.length() - 2);
                                    }
                                    out.println(studentsListBuilder.toString());
                                    break;
                                case "GET_SUBJECTS":
                                    // Get courses assigned to the student
                                    ArrayList<Course> studentCourses = getCourses(activeUserIndexNum);
                                    if (studentCourses.isEmpty()) {
                                        out.println("NO_SUBJECTS");
                                    } else {
                                        StringBuilder subjectsList = new StringBuilder();
                                        for (Course course : studentCourses) {
                                            subjectsList.append(course.getName()).append(":");
                                            // Add category details
                                            for (Map.Entry<String, String> categoryEntry : course.getKategorija().entrySet()) {
                                                String categoryName = categoryEntry.getKey();
                                                String pointsData = categoryEntry.getValue();
                                                subjectsList.append(categoryName).append("=").append(pointsData).append(",");
                                            }
                                            // Remove trailing comma if exists
                                            if (subjectsList.length() > 0 && subjectsList.charAt(subjectsList.length() - 1) == ',') {
                                                subjectsList.setLength(subjectsList.length() - 1);
                                            }
                                            subjectsList.append(";");
                                        }
                                        // Remove trailing semicolon if exists
                                        if (subjectsList.length() > 0 && subjectsList.charAt(subjectsList.length() - 1) == ';') {
                                            subjectsList.setLength(subjectsList.length() - 1);
                                        }
                                        out.println(subjectsList.toString());
                                    }
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
                
                // Save to serialized files
                DataManager.saveStudents(students);
                DataManager.saveUsers(users);
                
                // Save to text files
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
    
    private String handleAddCourse(String command) {
        try {
            // Parse the command: ADD_COURSE:courseName:categories
            String[] parts = command.split(":", 3);
            if (parts.length != 3) {
                return "ERROR: Invalid ADD_COURSE format. Expected format: ADD_COURSE:courseName:categories";
            }
            
            String courseName = parts[1].trim();
            String categoriesData = parts[2].trim();
            
            // Check if course already exists
            if (courses.containsKey(courseName)) {
                return "ERROR: Course already exists: " + courseName;
            }
            
            // Parse categories: "name1=points1:minPoints1,name2=points2:minPoints2"
            Course newCourse = new Course(courseName);
            if (!categoriesData.isEmpty()) {
                String[] categoryPairs = categoriesData.split(",");
                for (String categoryPair : categoryPairs) {
                    if (categoryPair.contains("=")) {
                        String[] categoryData = categoryPair.split("=", 2);
                        String categoryName = categoryData[0].trim();
                        String pointsData = categoryData[1].trim();
                        
                        if (pointsData.contains(":")) {
                            String[] pointsParts = pointsData.split(":");
                            try {
                                int points = Integer.parseInt(pointsParts[0]);
                                int minPoints = Integer.parseInt(pointsParts[1]);
                                newCourse.dodajKategoriju(categoryName, points, minPoints);
                            } catch (NumberFormatException e) {
                                return "ERROR: Invalid points format in category: " + categoryName;
                            }
                        }
                    }
                }
            }
            
            // Add course to collection and save
            courses.put(courseName, newCourse);
            DataManager.saveCourses(courses);
            
            System.out.println("Course successfully added: " + courseName);
            return "COURSE_ADDED_SUCCESS";
            
        } catch (Exception e) {
            System.err.println("Error handling ADD_COURSE: " + e.getMessage());
            return "ERROR: Failed to add course.";
        }
    }
    
    private String handleGetAllCourses() {
        try {
            if (courses.isEmpty()) {
                return "NO_COURSES";
            }
            
            StringBuilder coursesList = new StringBuilder();
            for (Map.Entry<String, Course> entry : courses.entrySet()) {
                Course course = entry.getValue();
                coursesList.append(course.getName()).append(":");
                
                // Add category information
                for (Map.Entry<String, String> categoryEntry : course.getKategorija().entrySet()) {
                    String categoryName = categoryEntry.getKey();
                    String pointsData = categoryEntry.getValue();
                    coursesList.append(categoryName).append("=").append(pointsData).append(",");
                }
                
                // Remove trailing comma if exists
                if (coursesList.length() > 0 && coursesList.charAt(coursesList.length() - 1) == ',') {
                    coursesList.setLength(coursesList.length() - 1);
                }
                coursesList.append(";");
            }
            
            // Remove trailing semicolon if exists
            if (coursesList.length() > 0 && coursesList.charAt(coursesList.length() - 1) == ';') {
                coursesList.setLength(coursesList.length() - 1);
            }
            
            return coursesList.toString();
            
        } catch (Exception e) {
            System.err.println("Error handling GET_ALL_COURSES: " + e.getMessage());
            return "ERROR: Failed to get courses.";
        }
    }
    
    private String handleEditStudent(String command) {
        try {
            // Parse: EDIT_STUDENT:oldUsername:newIme:newPrezime:newBrojIndeksa:newJmbg:newUsername:newPassword:newRole
            String[] parts = command.split(":", 9);
            if (parts.length != 9) {
                return "ERROR: Invalid EDIT_STUDENT format. Expected 9 parts.";
            }
            
            String oldUsername = parts[1].trim();
            String newIme = parts[2].trim();
            String newPrezime = parts[3].trim();
            String newBrojIndeksa = parts[4].trim();
            String newJmbg = parts[5].trim();
            String newUsername = parts[6].trim();
            String newPassword = parts[7].trim();
            String newRole = parts[8].trim();
            
            // Find the student to edit
            Student targetStudent = null;
            User targetUser = null;
            
            for (Student student : students) {
                if (student.getUsername().equals(oldUsername)) {
                    targetStudent = student;
                    break;
                }
            }
            
            for (User user : users) {
                if (user.getUsername().equals(oldUsername)) {
                    targetUser = user;
                    break;
                }
            }
            
            if (targetStudent == null) {
                return "ERROR: Student not found: " + oldUsername;
            }
            
            if (targetUser == null) {
                return "ERROR: User not found: " + oldUsername;
            }
            
            // Check if new username already exists (if it's different from old one)
            if (!oldUsername.equals(newUsername)) {
                for (User user : users) {
                    if (user.getUsername().equals(newUsername)) {
                        return "ERROR: New username already exists: " + newUsername;
                    }
                }
                
                // Check if new index number already exists
                for (Student student : students) {
                    if (!student.getUsername().equals(oldUsername) && student.getIndexNum().equals(newBrojIndeksa)) {
                        return "ERROR: Index number already exists: " + newBrojIndeksa;
                    }
                }
            }
            
            // Remove old student and user
            students.remove(targetStudent);
            users.remove(targetUser);
            
            // Create new student and user with updated information
            try {
                Student newStudent = new Student(newUsername, newIme, newPrezime, newBrojIndeksa, newJmbg);
                // Copy courses from old student
                for (Course course : targetStudent.getCourses()) {
                    newStudent.dodajPredmet(course);
                }
                students.add(newStudent);
                
                User newUser = new User(newUsername, newPassword, newRole);
                users.add(newUser);
                
                // Save updated data
                DataManager.saveStudents(students);
                DataManager.saveUsers(users);
                
                System.out.println("Student successfully updated: " + newIme + " " + newPrezime);
                return "STUDENT_EDITED_SUCCESS";
                
            } catch (IllegalArgumentException e) {
                // Restore original data if creation fails
                students.add(targetStudent);
                users.add(targetUser);
                return "ERROR: " + e.getMessage();
            }
            
        } catch (Exception e) {
            System.err.println("Error handling EDIT_STUDENT: " + e.getMessage());
            return "ERROR: Failed to edit student.";
        }
    }
    
    private String handleDeleteStudent(String command) {
        try {
            // Parse: DELETE_STUDENT:username
            String[] parts = command.split(":", 2);
            if (parts.length != 2) {
                return "ERROR: Invalid DELETE_STUDENT format. Expected: DELETE_STUDENT:username";
            }
            
            String username = parts[1].trim();
            
            // Find and remove student
            Student targetStudent = null;
            for (Student student : students) {
                if (student.getUsername().equals(username)) {
                    targetStudent = student;
                    break;
                }
            }
            
            if (targetStudent == null) {
                return "ERROR: Student not found: " + username;
            }
            
            // Find and remove user
            User targetUser = null;
            for (User user : users) {
                if (user.getUsername().equals(username)) {
                    targetUser = user;
                    break;
                }
            }
            
            if (targetUser == null) {
                return "ERROR: User not found: " + username;
            }
            
            // Remove from collections
            students.remove(targetStudent);
            users.remove(targetUser);
            
            // Save updated data
            DataManager.saveStudents(students);
            DataManager.saveUsers(users);
            
            System.out.println("Student successfully deleted: " + username);
            return "STUDENT_DELETED_SUCCESS";
            
        } catch (Exception e) {
            System.err.println("Error handling DELETE_STUDENT: " + e.getMessage());
            return "ERROR: Failed to delete student.";
        }
    }
    
    private String handleGetStudentDetails(String command) {
        try {
            // Parse: GET_STUDENT_DETAILS:username
            String[] parts = command.split(":", 2);
            if (parts.length != 2) {
                return "ERROR: Invalid GET_STUDENT_DETAILS format. Expected: GET_STUDENT_DETAILS:username";
            }
            
            String username = parts[1].trim();
            
            // Find student
            Student targetStudent = null;
            for (Student student : students) {
                if (student.getUsername().equals(username)) {
                    targetStudent = student;
                    break;
                }
            }
            
            if (targetStudent == null) {
                return "ERROR: Student not found: " + username;
            }
            
            // Find user
            User targetUser = null;
            for (User user : users) {
                if (user.getUsername().equals(username)) {
                    targetUser = user;
                    break;
                }
            }
            
            if (targetUser == null) {
                return "ERROR: User not found: " + username;
            }
            
            // Format: username:ime:prezime:brojIndeksa:jmbg:role
            String response = String.format("%s:%s:%s:%s:%s:%s",
                targetStudent.getUsername(),
                targetStudent.getName(),
                targetStudent.getLastName(),
                targetStudent.getIndexNum(),
                targetStudent.getJmbg(),
                targetUser.getRole());
            
            return response;
            
        } catch (Exception e) {
            System.err.println("Error handling GET_STUDENT_DETAILS: " + e.getMessage());
            return "ERROR: Failed to get student details.";
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
