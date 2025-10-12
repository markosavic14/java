import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class DataManager {
    private static final String STUDENTS_FILE = "students.ser";
    private static final String COURSES_FILE = "courses.ser";
    private static final String USERS_FILE = "users.ser";
    
    public static void saveStudents(ArrayList<Student> students) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STUDENTS_FILE))) {
            oos.writeObject(students);
            System.out.println("Students data saved successfully to " + STUDENTS_FILE);
        } catch (IOException e) {
            System.err.println("Error saving students data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    public static ArrayList<Student> loadStudents() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(STUDENTS_FILE))) {
            ArrayList<Student> students = (ArrayList<Student>) ois.readObject();
            System.out.println("Students data loaded successfully from " + STUDENTS_FILE);
            return students;
        } catch (FileNotFoundException e) {
            System.out.println("Students file not found, creating new list.");
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading students data: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public static void saveCourses(Map<String, Course> courses) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(COURSES_FILE))) {
            oos.writeObject(courses);
            System.out.println("Courses data saved successfully to " + COURSES_FILE);
        } catch (IOException e) {
            System.err.println("Error saving courses data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, Course> loadCourses() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(COURSES_FILE))) {
            Map<String, Course> courses = (Map<String, Course>) ois.readObject();
            System.out.println("Courses data loaded successfully from " + COURSES_FILE);
            return courses;
        } catch (FileNotFoundException e) {
            System.out.println("Courses file not found, creating new map.");
            return new HashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading courses data: " + e.getMessage());
            return new HashMap<>();
        }
    }
    
    public static void saveUsers(ArrayList<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
            System.out.println("Users data saved successfully to " + USERS_FILE);
        } catch (IOException e) {
            System.err.println("Error saving users data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    public static ArrayList<User> loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USERS_FILE))) {
            ArrayList<User> users = (ArrayList<User>) ois.readObject();
            System.out.println("Users data loaded successfully from " + USERS_FILE);
            return users;
        } catch (FileNotFoundException e) {
            System.out.println("Users file not found, creating new list.");
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading users data: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public static void backupToTextFiles(ArrayList<Student> students, ArrayList<User> users) {
        // Backup students
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("students_backup.txt"))) {
            for (Student student : students) {
                writer.write(String.format("%s,%s,%s,%s,%s%n", 
                    student.getUsername(), 
                    student.getName(), 
                    student.getLastName(), 
                    student.getIndexNum(), 
                    student.getJmbg()));
            }
            System.out.println("Students backup saved to students_backup.txt");
        } catch (IOException e) {
            System.err.println("Error creating students backup: " + e.getMessage());
        }
        
        // Backup users
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users_backup.txt"))) {
            for (User user : users) {
                writer.write(String.format("%s:%s:%s%n", 
                    user.getUsername(), 
                    user.getPassword(), 
                    user.getRole()));
            }
            System.out.println("Users backup saved to users_backup.txt");
        } catch (IOException e) {
            System.err.println("Error creating users backup: " + e.getMessage());
        }
    }
    
    public static void importFromTextFiles() {
        ArrayList<Student> students = new ArrayList<>();
        ArrayList<User> users = new ArrayList<>();
        
        // Import students
        try (BufferedReader reader = new BufferedReader(new FileReader("server/students.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    try {
                        Student student = new Student(parts[0].trim(), parts[1].trim(), 
                                                    parts[2].trim(), parts[3].trim(), parts[4].trim());
                        students.add(student);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error importing student: " + e.getMessage());
                    }
                }
            }
            saveStudents(students);
        } catch (IOException e) {
            System.err.println("Error importing students from text file: " + e.getMessage());
        }
        
        // Import users
        try (BufferedReader reader = new BufferedReader(new FileReader("server/users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 3) {
                    User user = new User(parts[0], parts[1], parts[2]);
                    users.add(user);
                }
            }
            saveUsers(users);
        } catch (IOException e) {
            System.err.println("Error importing users from text file: " + e.getMessage());
        }
    }
}