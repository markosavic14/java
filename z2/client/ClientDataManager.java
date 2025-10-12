import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class ClientDataManager {
    private static final String CLIENT_COURSES_FILE = "client_courses.ser";
    
    public static void saveCourseConfigurations(Map<String, Map<String, String>> coursesData) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CLIENT_COURSES_FILE))) {
            oos.writeObject(coursesData);
            System.out.println("Course configurations saved successfully to " + CLIENT_COURSES_FILE);
        } catch (IOException e) {
            System.err.println("Error saving course configurations: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, Map<String, String>> loadCourseConfigurations() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CLIENT_COURSES_FILE))) {
            Map<String, Map<String, String>> coursesData = (Map<String, Map<String, String>>) ois.readObject();
            System.out.println("Course configurations loaded successfully from " + CLIENT_COURSES_FILE);
            return coursesData;
        } catch (FileNotFoundException e) {
            System.out.println("Course configurations file not found, creating new map.");
            return new HashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading course configurations: " + e.getMessage());
            return new HashMap<>();
        }
    }
    
    public static void exportCoursesToText(Map<String, Map<String, String>> coursesData, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Map.Entry<String, Map<String, String>> courseEntry : coursesData.entrySet()) {
                String courseName = courseEntry.getKey();
                Map<String, String> categories = courseEntry.getValue();
                
                writer.write("Course: " + courseName + "\n");
                int totalPoints = 0;
                
                for (Map.Entry<String, String> categoryEntry : categories.entrySet()) {
                    String categoryName = categoryEntry.getKey();
                    String pointsData = categoryEntry.getValue();
                    String[] parts = pointsData.split(":");
                    if (parts.length == 2) {
                        int points = Integer.parseInt(parts[0]);
                        int minPoints = Integer.parseInt(parts[1]);
                        totalPoints += points;
                        writer.write("  - " + categoryName + ": " + points + " points (min: " + minPoints + ")\n");
                    }
                }
                writer.write("  Total: " + totalPoints + " points\n\n");
            }
            System.out.println("Courses exported to " + filename);
        } catch (IOException e) {
            System.err.println("Error exporting courses: " + e.getMessage());
        }
    }
    
    public static boolean validateCourseConfiguration(Map<String, String> categories) {
        int totalPoints = 0;
        for (String pointsData : categories.values()) {
            String[] parts = pointsData.split(":");
            if (parts.length >= 1) {
                try {
                    totalPoints += Integer.parseInt(parts[0]);
                } catch (NumberFormatException e) {
                    return false; // Invalid data format
                }
            }
        }
        return totalPoints == 100;
    }
    
    public static CourseStatistics getCourseStatistics(Map<String, String> categories) {
        int totalPoints = 0;
        int minTotalPoints = 0;
        int maxPoints = 0;
        int minPoints = Integer.MAX_VALUE;
        int categoryCount = categories.size();
        
        for (String pointsData : categories.values()) {
            String[] parts = pointsData.split(":");
            if (parts.length == 2) {
                try {
                    int points = Integer.parseInt(parts[0]);
                    int minPointsForCategory = Integer.parseInt(parts[1]);
                    
                    totalPoints += points;
                    minTotalPoints += minPointsForCategory;
                    maxPoints = Math.max(maxPoints, points);
                    minPoints = Math.min(minPoints, points);
                } catch (NumberFormatException e) {
                    // Skip invalid entries
                }
            }
        }
        
        if (categoryCount == 0) {
            minPoints = 0;
        }
        
        return new CourseStatistics(totalPoints, minTotalPoints, maxPoints, minPoints, categoryCount);
    }
    
    public static class CourseStatistics {
        public final int totalPoints;
        public final int minTotalPoints;
        public final int maxCategoryPoints;
        public final int minCategoryPoints;
        public final int categoryCount;
        
        public CourseStatistics(int totalPoints, int minTotalPoints, int maxCategoryPoints, 
                              int minCategoryPoints, int categoryCount) {
            this.totalPoints = totalPoints;
            this.minTotalPoints = minTotalPoints;
            this.maxCategoryPoints = maxCategoryPoints;
            this.minCategoryPoints = minCategoryPoints;
            this.categoryCount = categoryCount;
        }
        
        @Override
        public String toString() {
            return String.format("Categories: %d | Total: %d/100 | Min Required: %d | Range: %d-%d", 
                               categoryCount, totalPoints, minTotalPoints, minCategoryPoints, maxCategoryPoints);
        }
    }
}