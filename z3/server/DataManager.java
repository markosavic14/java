import java.io.*;
import java.util.ArrayList;

public class DataManager {
    private static final String USERS_FILE = "users.ser";
    
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
    
    public static void backupToTextFiles(ArrayList<User> users) {
        // Backup users
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users_backup.txt"))) {
            for (User user : users) {
                writer.write(String.format("%s:%s%n", 
                    user.getUsername(), 
                    user.getPassword()));
            }
            System.out.println("Users backup saved to users_backup.txt");
        } catch (IOException e) {
            System.err.println("Error creating users backup: " + e.getMessage());
        }
    }
    
    public static void importFromTextFiles() {
        ArrayList<User> users = new ArrayList<>();        
        // Import users
        try (BufferedReader reader = new BufferedReader(new FileReader("server/users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    User user = new User(parts[0], parts[1]);
                    users.add(user);
                }
            }
            saveUsers(users);
        } catch (IOException e) {
            System.err.println("Error importing users from text file: " + e.getMessage());
        }
    }
}