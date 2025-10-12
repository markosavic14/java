import javax.swing.*;

import java.awt.*;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

public class mainWindowAdmin {
    private PrintWriter out;
    private BufferedReader in;

    private ArrayList<String> students;

    public mainWindowAdmin(PrintWriter out, BufferedReader in) {
        this.out = out;
        this.in = in;

        JFrame frame = new JFrame("Admin Main Window");
        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel studentiPanel = new JPanel();
        studentiPanel.setLayout(new BorderLayout());
        
        // Create main content area for students
        JPanel studentiContentPanel = new JPanel();
        studentiContentPanel.setLayout(new BoxLayout(studentiContentPanel, BoxLayout.Y_AXIS));
        studentiContentPanel.add(new JLabel("Studenti panel"));
        
        // Create button panel for student operations
        JPanel studentiButtonPanel = new JPanel(new FlowLayout());
        JButton dodajStudentaButton = new JButton("Dodaj studenta");
        JButton editStudentaButton = new JButton("Uredi studenta");
        JButton deleteStudentaButton = new JButton("Obriši studenta");
        
        dodajStudentaButton.addActionListener(e -> openAddStudentDialog());
        editStudentaButton.addActionListener(e -> openEditStudentDialog());
        deleteStudentaButton.addActionListener(e -> openDeleteStudentDialog());
        
        studentiButtonPanel.add(dodajStudentaButton);
        studentiButtonPanel.add(editStudentaButton);
        studentiButtonPanel.add(deleteStudentaButton);
        
        // Refresh button for the students panel
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            // Get students list from server
            if (out != null) {
                out.println("GET_STUDENTS");
                out.flush();
            }
            try {
                if (in != null) {
                    String studentsList = in.readLine();
                    System.out.println("Refreshed students from server: " + studentsList);
                    
                    studentiContentPanel.removeAll();
                    studentiContentPanel.add(new JLabel("Studenti panel"));
                    
                    if (studentsList != null && !studentsList.trim().isEmpty()) {
                        students = new ArrayList<>();
                        String[] studentNames = studentsList.split(",");
                        for (String studentName : studentNames) {
                            String trimmedName = studentName.trim();
                            if (!trimmedName.isEmpty()) {
                                students.add(trimmedName);
                                studentiContentPanel.add(new JLabel("• " + trimmedName));
                            }
                        }
                        
                        if (students.isEmpty()) {
                            studentiContentPanel.add(new JLabel("Nema registrovanih studenata"));
                        }
                    } else {
                        studentiContentPanel.add(new JLabel("Nema registrovanih studenata"));
                    }
                    
                    studentiContentPanel.revalidate();
                    studentiContentPanel.repaint();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Greška pri komunikaciji sa serverom: " + ex.getMessage(), 
                                            "Greška", JOptionPane.ERROR_MESSAGE);
            }
        });
        studentiButtonPanel.add(refreshButton);
        
        // Components for the main students panel
        studentiPanel.add(studentiContentPanel, BorderLayout.CENTER);
        studentiPanel.add(studentiButtonPanel, BorderLayout.SOUTH);

        // Predmeti panel
        JPanel predmetiPanel = new JPanel();
        predmetiPanel.setLayout(new BorderLayout());
        
        // Main content area for subjects
        JPanel predmetiContentPanel = new JPanel();
        predmetiContentPanel.setLayout(new BoxLayout(predmetiContentPanel, BoxLayout.Y_AXIS));
        predmetiContentPanel.add(new JLabel("Predmeti panel"));
        
        // Button panel for subject operations
        JPanel predmetiButtonPanel = new JPanel(new FlowLayout());
        JButton dodajPredmetButton = new JButton("Dodaj predmet");
        dodajPredmetButton.addActionListener(e -> openCourseManagementDialog(null));
        predmetiButtonPanel.add(dodajPredmetButton);
        
        // Refresh button for subjects
        JButton refreshPredmetiButton = new JButton("Refresh");
        refreshPredmetiButton.addActionListener(e -> {
            if (out != null) {
                out.println("GET_ALL_COURSES");
                out.flush();
            }
            try {
                if (in != null) {
                    String coursesResponse = in.readLine();
                    System.out.println("Refreshed courses from server: " + coursesResponse);
                    
                    predmetiContentPanel.removeAll();
                    predmetiContentPanel.add(new JLabel("Predmeti panel"));
                    
                    if (coursesResponse != null && !coursesResponse.equals("NO_COURSES") && !coursesResponse.startsWith("ERROR")) {
                        // Parse courses response: "course1:cat1=points1:min1,cat2=points2:min2;course2:..."
                        String[] courseEntries = coursesResponse.split(";");
                        for (String courseEntry : courseEntries) {
                            if (courseEntry.contains(":")) {
                                String[] courseParts = courseEntry.split(":", 2);
                                String courseName = courseParts[0].trim();
                                String categoriesData = courseParts.length > 1 ? courseParts[1] : "";
                                
                                // Count categories and total points
                                int categoryCount = 0;
                                int totalPoints = 0;
                                if (!categoriesData.isEmpty()) {
                                    String[] categories = categoriesData.split(",");
                                    categoryCount = categories.length;
                                    for (String category : categories) {
                                        if (category.contains("=")) {
                                            String[] catData = category.split("=", 2);
                                            if (catData.length > 1 && catData[1].contains(":")) {
                                                try {
                                                    String points = catData[1].split(":")[0];
                                                    totalPoints += Integer.parseInt(points);
                                                } catch (NumberFormatException ex) {
                                                    // Skip invalid entries
                                                }
                                            }
                                        }
                                    }
                                }
                                
                                String validationIcon = (totalPoints == 100) ? " ✓" : " ✗";
                                String courseInfo = courseName + " (" + categoryCount + " kategorija, " + totalPoints + " bodova)" + validationIcon;
                                predmetiContentPanel.add(new JLabel(courseInfo));
                            }
                        }
                    } else {
                        predmetiContentPanel.add(new JLabel("Nema dostupnih predmeta"));
                    }
                    
                    predmetiContentPanel.revalidate();
                    predmetiContentPanel.repaint();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Greška pri komunikaciji sa serverom: " + ex.getMessage(), 
                                            "Greška", JOptionPane.ERROR_MESSAGE);
            }
        });
        predmetiButtonPanel.add(refreshPredmetiButton);
        
        // Add components to the main subjects panel
        predmetiPanel.add(predmetiContentPanel, BorderLayout.CENTER);
        predmetiPanel.add(predmetiButtonPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Studenti", studentiPanel);
        tabbedPane.addTab("Predmeti", predmetiPanel);

        frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
        
        // Auto-refresh data on startup
        SwingUtilities.invokeLater(() -> {
            // Trigger refresh for both students and courses
            refreshButton.doClick();
            refreshPredmetiButton.doClick();
        });
    }
    
    private void openAddStudentDialog() {
        JDialog dialog = new JDialog((JFrame) null, "Dodaj studenta", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Create input fields
        JTextField imeField = new JTextField(20);
        JTextField prezimeField = new JTextField(20);
        JTextField brojIndeksaField = new JTextField(20);
        JTextField jmbgField = new JTextField(20);
        
        JButton upravljajPredmetimaButton = new JButton("Upravljaj");
        
        // Store courses data
        final StringBuilder predmetiOceneData = new StringBuilder();
        
        upravljajPredmetimaButton.addActionListener(ev -> {
            String coursesData = openCourseManagementDialog(dialog);
            if (coursesData != null && !coursesData.isEmpty()) {
                predmetiOceneData.setLength(0);
                predmetiOceneData.append(coursesData);
            }
        });
        
        JTextField korisnickoImeField = new JTextField(20);
        JPasswordField lozinkaField = new JPasswordField(20);
        
        // Add role selection
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"student", "admin"});
        roleComboBox.setSelectedItem("student"); // Default to student
        
        // Add labels and fields to the dialog
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Ime:"), gbc);
        gbc.gridx = 1;
        dialog.add(imeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Prezime:"), gbc);
        gbc.gridx = 1;
        dialog.add(prezimeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Broj indeksa:"), gbc);
        gbc.gridx = 1;
        dialog.add(brojIndeksaField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("JMBG:"), gbc);
        gbc.gridx = 1;
        dialog.add(jmbgField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        dialog.add(new JLabel("Predmeti i ocene:"), gbc);
        gbc.gridx = 1;
        dialog.add(upravljajPredmetimaButton, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        dialog.add(new JLabel("Korisničko ime:"), gbc);
        gbc.gridx = 1;
        dialog.add(korisnickoImeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        dialog.add(new JLabel("Lozinka:"), gbc);
        gbc.gridx = 1;
        dialog.add(lozinkaField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 7;
        dialog.add(new JLabel("Uloga:"), gbc);
        gbc.gridx = 1;
        dialog.add(roleComboBox, gbc);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton dodajButton = new JButton("Dodaj");
        JButton otkaziButton = new JButton("Otkaži");
        
        dodajButton.addActionListener(e -> {
            // Validate input fields
            if (validateStudentInput(imeField.getText(), prezimeField.getText(), 
                                   brojIndeksaField.getText(), jmbgField.getText(),
                                   korisnickoImeField.getText(), new String(lozinkaField.getPassword()))) {
                // Send student data to server
                sendStudentToServer(imeField.getText(), prezimeField.getText(),
                                  brojIndeksaField.getText(), jmbgField.getText(),
                                  predmetiOceneData.toString(), korisnickoImeField.getText(),
                                  new String(lozinkaField.getPassword()), (String) roleComboBox.getSelectedItem());
                dialog.dispose();
            }
        });
        
        otkaziButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(dodajButton);
        buttonPanel.add(otkaziButton);
        
        gbc.gridx = 0; gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        dialog.add(buttonPanel, gbc);
        
        dialog.setSize(500, 380);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
    
    private void openEditStudentDialog() {
        // First, show a dialog to select which student to edit
        if (students == null || students.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nema studenata za uređivanje. Molimo prvo osvežite listu.", 
                                        "Greška", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create a list of students with more details for selection
        String[] studentOptions = new String[students.size()];
        String[] studentUsernames = new String[students.size()];
        
        for (int i = 0; i < students.size(); i++) {
            String studentInfo = students.get(i);
            studentOptions[i] = studentInfo;
            // Extract username from the format "Name Lastname (Index):username"
            if (studentInfo.contains(":")) {
                studentUsernames[i] = studentInfo.substring(studentInfo.lastIndexOf(":") + 1).trim();
            } else {
                studentUsernames[i] = studentInfo; // Fallback
            }
        }
        
        String selectedStudent = (String) JOptionPane.showInputDialog(null,
            "Izaberite studenta za uređivanje:",
            "Uredi studenta",
            JOptionPane.QUESTION_MESSAGE,
            null,
            studentOptions,
            studentOptions[0]);
        
        if (selectedStudent == null) {
            return; // User cancelled
        }
        
        // Find the username for the selected student
        String selectedUsername = null;
        for (int i = 0; i < studentOptions.length; i++) {
            if (studentOptions[i].equals(selectedStudent)) {
                selectedUsername = studentUsernames[i];
                break;
            }
        }
        
        if (selectedUsername == null) {
            JOptionPane.showMessageDialog(null, "Greška pri pronalaženju korisničkog imena.", 
                                        "Greška", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get student details from server
        try {
            if (out != null) {
                out.println("GET_STUDENT_DETAILS:" + selectedUsername);
                out.flush();
                
                if (in != null) {
                    String response = in.readLine();
                    if (response.startsWith("ERROR")) {
                        JOptionPane.showMessageDialog(null, "Greška pri dobijanju detalja studenta: " + response, 
                                                    "Greška", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Parse response: username:ime:prezime:brojIndeksa:jmbg:role
                    String[] studentDetails = response.split(":");
                    if (studentDetails.length == 6) {
                        openEditStudentFormDialog(studentDetails[0], studentDetails[1], studentDetails[2], 
                                                studentDetails[3], studentDetails[4], studentDetails[5]);
                    } else {
                        JOptionPane.showMessageDialog(null, "Neispravni detalji studenta.", 
                                                    "Greška", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Greška pri komunikaciji sa serverom: " + ex.getMessage(), 
                                        "Greška", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void openEditStudentFormDialog(String oldUsername, String currentIme, String currentPrezime, 
                                         String currentBrojIndeksa, String currentJmbg, String currentRole) {
        JDialog dialog = new JDialog((JFrame) null, "Uredi studenta", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Create input fields with current values
        JTextField imeField = new JTextField(currentIme, 20);
        JTextField prezimeField = new JTextField(currentPrezime, 20);
        JTextField brojIndeksaField = new JTextField(currentBrojIndeksa, 20);
        JTextField jmbgField = new JTextField(currentJmbg, 20);
        JTextField korisnickoImeField = new JTextField(oldUsername, 20);
        JPasswordField lozinkaField = new JPasswordField(20);
        
        // Add role selection
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"student", "admin"});
        roleComboBox.setSelectedItem(currentRole);
        
        // Add labels and fields to the dialog
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Ime:"), gbc);
        gbc.gridx = 1;
        dialog.add(imeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Prezime:"), gbc);
        gbc.gridx = 1;
        dialog.add(prezimeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Broj indeksa:"), gbc);
        gbc.gridx = 1;
        dialog.add(brojIndeksaField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("JMBG:"), gbc);
        gbc.gridx = 1;
        dialog.add(jmbgField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        dialog.add(new JLabel("Korisničko ime:"), gbc);
        gbc.gridx = 1;
        dialog.add(korisnickoImeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        dialog.add(new JLabel("Nova lozinka:"), gbc);
        gbc.gridx = 1;
        dialog.add(lozinkaField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        dialog.add(new JLabel("Uloga:"), gbc);
        gbc.gridx = 1;
        dialog.add(roleComboBox, gbc);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton updateButton = new JButton("Ažuriraj");
        JButton otkaziButton = new JButton("Otkaži");
        
        updateButton.addActionListener(e -> {
            String newPassword = new String(lozinkaField.getPassword());
            if (newPassword.trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Nova lozinka ne može biti prazna!", "Greška", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate input fields
            if (validateStudentInput(imeField.getText(), prezimeField.getText(), 
                                   brojIndeksaField.getText(), jmbgField.getText(),
                                   korisnickoImeField.getText(), newPassword)) {
                // Send edit request to server
                sendEditStudentToServer(oldUsername, imeField.getText(), prezimeField.getText(),
                                      brojIndeksaField.getText(), jmbgField.getText(),
                                      korisnickoImeField.getText(), newPassword, 
                                      (String) roleComboBox.getSelectedItem());
                dialog.dispose();
            }
        });
        
        otkaziButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(updateButton);
        buttonPanel.add(otkaziButton);
        
        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        dialog.add(buttonPanel, gbc);
        
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
    
    private void openDeleteStudentDialog() {
        // First, show a dialog to select which student to delete
        if (students == null || students.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nema studenata za brisanje. Molimo prvo osvežite listu.", 
                                        "Greška", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create a list of students for selection
        String[] studentOptions = new String[students.size()];
        String[] studentUsernames = new String[students.size()];
        
        for (int i = 0; i < students.size(); i++) {
            String studentInfo = students.get(i);
            studentOptions[i] = studentInfo;
            // Extract username from the format "Name Lastname (Index):username"
            if (studentInfo.contains(":")) {
                studentUsernames[i] = studentInfo.substring(studentInfo.lastIndexOf(":") + 1).trim();
            } else {
                studentUsernames[i] = studentInfo; // Fallback
            }
        }
        
        String selectedStudent = (String) JOptionPane.showInputDialog(null,
            "Izaberite studenta za brisanje:",
            "Obriši studenta",
            JOptionPane.WARNING_MESSAGE,
            null,
            studentOptions,
            studentOptions[0]);
        
        if (selectedStudent == null) {
            return; // User cancelled
        }
        
        // Find the username for the selected student
        String selectedUsername = null;
        for (int i = 0; i < studentOptions.length; i++) {
            if (studentOptions[i].equals(selectedStudent)) {
                selectedUsername = studentUsernames[i];
                break;
            }
        }
        
        if (selectedUsername == null) {
            JOptionPane.showMessageDialog(null, "Greška pri pronalaženju korisničkog imena.", 
                                        "Greška", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(null,
            "Da li ste sigurni da želite da obrišete studenta:\n" + selectedStudent + "\n\nOva akcija se ne može poništiti!",
            "Potvrda brisanja",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Send delete request to server
            sendDeleteStudentToServer(selectedUsername);
        }
    }

    private boolean validateStudentInput(String ime, String prezime, String brojIndeksa,
                                       String jmbg, String korisnickoIme, String lozinka) {
        if (ime.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ime ne može biti prazno!", "Greška", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (prezime.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Prezime ne može biti prazno!", "Greška", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validate Broj indeksa format: E/e + 1,2,3 + delimiter (/ or -) + year (2000-2023)
        if (brojIndeksa.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Broj indeksa ne može biti prazan!", "Greška", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!validateBrojIndeksa(brojIndeksa.trim())) {
            JOptionPane.showMessageDialog(null, 
                "Broj indeksa mora biti u formatu: Smer (E/e + 1,2,3) + delimiter (/ ili -) + godina (2000-2023)\n" +
                "Primer: E2-2015 ili e1/2019", 
                "Greška", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validate JMBG format: 13 digits with first 2 for day (01-31) and next 2 for month (01-12)
        if (jmbg.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "JMBG ne može biti prazan!", "Greška", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!validateJMBG(jmbg.trim())) {
            JOptionPane.showMessageDialog(null, 
                "JMBG mora da ima 13 cifara gde prve dve predstavljaju dan (01-31), a druge dve mesec (01-12)", 
                "Greška", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (korisnickoIme.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Korisničko ime ne može biti prazno!", "Greška", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (lozinka.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Lozinka ne može biti prazna!", "Greška", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    
    private boolean validateBrojIndeksa(String brojIndeksa) {
        // Pattern: E/e + 1,2,3 + delimiter (/ or -) + year (2000-2023)
        // Examples: E2-2015, e1/2019, E3-2020
        String pattern = "^[Ee][123][/-](200[0-9]|201[0-9]|202[0-3])$";
        return brojIndeksa.matches(pattern);
    }
    
    private boolean validateJMBG(String jmbg) {
        // Must be exactly 13 digits
        if (!jmbg.matches("^\\d{13}$")) {
            return false;
        }
        
        // Extract day and month
        String dayStr = jmbg.substring(0, 2);
        String monthStr = jmbg.substring(2, 4);
        
        try {
            int day = Integer.parseInt(dayStr);
            int month = Integer.parseInt(monthStr);
            
            // Validate day (01-31)
            if (day < 1 || day > 31) {
                return false;
            }
            
            // Validate month (01-12)
            if (month < 1 || month > 12) {
                return false;
            }
            
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private void sendStudentToServer(String ime, String prezime, String brojIndeksa, 
                                   String jmbg, String predmetiOcene, String korisnickoIme, String lozinka, String role) {
        try {
            // Format: ADD_STUDENT:ime:prezime:brojIndeksa:jmbg:predmetiOcene:korisnickoIme:lozinka:role
            String studentData = String.format("ADD_STUDENT:%s:%s:%s:%s:%s:%s:%s:%s",
                ime, prezime, brojIndeksa, jmbg, predmetiOcene, korisnickoIme, lozinka, role);
            
            if (out != null) {
                out.println(studentData);
                out.flush();
                System.out.println("Sent student data to server: " + studentData);
                
                // Wait for server response
                if (in != null) {
                    String response = in.readLine();
                    if ("STUDENT_ADDED_SUCCESS".equals(response)) {
                        JOptionPane.showMessageDialog(null, "Student je uspešno dodat!", "Uspeh", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Greška pri dodavanju studenta: " + response, "Greška", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Greška pri komunikaciji sa serverom: " + ex.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void sendEditStudentToServer(String oldUsername, String ime, String prezime, String brojIndeksa, 
                                       String jmbg, String newUsername, String newPassword, String role) {
        try {
            // Format: EDIT_STUDENT:oldUsername:newIme:newPrezime:newBrojIndeksa:newJmbg:newUsername:newPassword:newRole
            String editData = String.format("EDIT_STUDENT:%s:%s:%s:%s:%s:%s:%s:%s",
                oldUsername, ime, prezime, brojIndeksa, jmbg, newUsername, newPassword, role);
            
            if (out != null) {
                out.println(editData);
                out.flush();
                System.out.println("Sent edit student data to server: " + editData);
                
                // Wait for server response
                if (in != null) {
                    String response = in.readLine();
                    if ("STUDENT_EDITED_SUCCESS".equals(response)) {
                        JOptionPane.showMessageDialog(null, "Student je uspešno ažuriran!", "Uspeh", JOptionPane.INFORMATION_MESSAGE);
                        // Refresh the student list
                        SwingUtilities.invokeLater(() -> {
                            // Find the refresh button and trigger it
                            // This will update the displayed list
                            // You might need to adjust this based on your component references
                        });
                    } else {
                        JOptionPane.showMessageDialog(null, "Greška pri ažuriranju studenta: " + response, "Greška", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Greška pri komunikaciji sa serverom: " + ex.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void sendDeleteStudentToServer(String username) {
        try {
            // Format: DELETE_STUDENT:username
            String deleteData = "DELETE_STUDENT:" + username;
            
            if (out != null) {
                out.println(deleteData);
                out.flush();
                System.out.println("Sent delete student request to server: " + deleteData);
                
                // Wait for server response
                if (in != null) {
                    String response = in.readLine();
                    if ("STUDENT_DELETED_SUCCESS".equals(response)) {
                        JOptionPane.showMessageDialog(null, "Student je uspešno obrisan!", "Uspeh", JOptionPane.INFORMATION_MESSAGE);
                        // Refresh the student list
                        SwingUtilities.invokeLater(() -> {
                            // Trigger refresh
                            // You might need to adjust this based on your component references
                        });
                    } else {
                        JOptionPane.showMessageDialog(null, "Greška pri brisanju studenta: " + response, "Greška", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Greška pri komunikaciji sa serverom: " + ex.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private String openCourseManagementDialog(JDialog parentDialog) {
        JDialog courseDialog = new JDialog(parentDialog, "Upravljanje predmetima", true);
        courseDialog.setLayout(new BorderLayout());
        
        // Main panel for courses
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Course list area
        DefaultListModel<String> courseListModel = new DefaultListModel<>();
        JList<String> courseList = new JList<>(courseListModel);
        courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane courseScrollPane = new JScrollPane(courseList);
        courseScrollPane.setPreferredSize(new Dimension(400, 200));
        
        // Button panel for course operations
        JPanel courseButtonPanel = new JPanel(new FlowLayout());
        JButton dodajPredmetButton = new JButton("Dodaj predmet");
        JButton ukloniPredmetButton = new JButton("Ukloni predmet");
        JButton upravljajKategorijama = new JButton("Upravljaj kategorijama");
        JButton exportPredmeteButton = new JButton("Export predmete");
        JButton refreshCoursesButton = new JButton("Refresh");
        
        courseButtonPanel.add(dodajPredmetButton);
        courseButtonPanel.add(ukloniPredmetButton);
        courseButtonPanel.add(upravljajKategorijama);
        courseButtonPanel.add(exportPredmeteButton);
        courseButtonPanel.add(refreshCoursesButton);
        
        // Store course data with categories (categoryName -> "points:minPoints")
        java.util.Map<String, java.util.Map<String, String>> coursesData = ClientDataManager.loadCourseConfigurations();
        
        // Add course button action
        dodajPredmetButton.addActionListener(e -> {
            String courseName = JOptionPane.showInputDialog(courseDialog, "Unesite naziv predmeta:");
            if (courseName != null && !courseName.trim().isEmpty()) {
                courseName = courseName.trim();
                if (!coursesData.containsKey(courseName)) {
                    coursesData.put(courseName, new java.util.HashMap<>());
                    updateCourseList(courseListModel, coursesData);
                    ClientDataManager.saveCourseConfigurations(coursesData); // Save after adding
                } else {
                    JOptionPane.showMessageDialog(courseDialog, "Predmet već postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // Remove course button action
        ukloniPredmetButton.addActionListener(e -> {
            String selectedCourse = courseList.getSelectedValue();
            if (selectedCourse != null) {
                String courseName = selectedCourse.split(" \\(")[0]; // Extract course name
                int confirm = JOptionPane.showConfirmDialog(courseDialog,
                    "Da li ste sigurni da želite da uklonite predmet '" + courseName + "'?",
                    "Potvrda brisanja", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    coursesData.remove(courseName);
                    updateCourseList(courseListModel, coursesData);
                    ClientDataManager.saveCourseConfigurations(coursesData); // Save after removing
                }
            } else {
                JOptionPane.showMessageDialog(courseDialog, "Molimo izaberite predmet za uklanjanje!", "Greška", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        // Manage categories button action
        upravljajKategorijama.addActionListener(e -> {
            String selectedCourse = courseList.getSelectedValue();
            if (selectedCourse != null) {
                String courseName = selectedCourse.split(" \\(")[0]; // Extract course name
                openCategoryManagementDialog(courseDialog, courseName, coursesData.get(courseName), 
                                            () -> updateCourseList(courseListModel, coursesData));
            } else {
                JOptionPane.showMessageDialog(courseDialog, "Molimo izaberite predmet za upravljanje kategorijama!", "Greška", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        // Export courses button action
        exportPredmeteButton.addActionListener(e -> {
            if (!coursesData.isEmpty()) {
                String filename = JOptionPane.showInputDialog(courseDialog, 
                    "Unesite naziv fajla za export:", "courses_export.txt");
                if (filename != null && !filename.trim().isEmpty()) {
                    ClientDataManager.exportCoursesToText(coursesData, filename.trim());
                    JOptionPane.showMessageDialog(courseDialog, 
                        "Predmeti su uspešno eksportovani u fajl: " + filename.trim(), 
                        "Export uspešan", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(courseDialog, 
                    "Nema predmeta za export!", "Greška", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        // Refresh courses button action
        refreshCoursesButton.addActionListener(e -> {
            try {
                // Reload course configurations from storage
                java.util.Map<String, java.util.Map<String, String>> refreshedCoursesData = ClientDataManager.loadCourseConfigurations();
                coursesData.clear();
                coursesData.putAll(refreshedCoursesData);
                updateCourseList(courseListModel, coursesData);
                JOptionPane.showMessageDialog(courseDialog, "Lista predmeta je uspešno osvežena!", "Refresh", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(courseDialog, "Greška pri osvežavanju liste predmeta: " + ex.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        
        // Control buttons
        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Otkaži");
        
        final String[] result = {null};
        
        okButton.addActionListener(e -> {
            result[0] = formatCoursesData(coursesData);
            // Also send courses to server
            sendCoursesToServer(coursesData);
            courseDialog.dispose();
        });
        
        cancelButton.addActionListener(e -> courseDialog.dispose());
        
        controlPanel.add(okButton);
        controlPanel.add(cancelButton);
        
        // Layout
        mainPanel.add(new JLabel("Predmeti i kategorije:"), BorderLayout.NORTH);
        mainPanel.add(courseScrollPane, BorderLayout.CENTER);
        mainPanel.add(courseButtonPanel, BorderLayout.SOUTH);
        
        courseDialog.add(mainPanel, BorderLayout.CENTER);
        courseDialog.add(controlPanel, BorderLayout.SOUTH);
        
        courseDialog.setSize(500, 400);
        courseDialog.setLocationRelativeTo(parentDialog);
        courseDialog.setVisible(true);
        
        return result[0];
    }
    
    private void updateCourseList(DefaultListModel<String> model, java.util.Map<String, java.util.Map<String, String>> coursesData) {
        model.clear();
        for (java.util.Map.Entry<String, java.util.Map<String, String>> entry : coursesData.entrySet()) {
            String courseName = entry.getKey();
            java.util.Map<String, String> categories = entry.getValue();
            int totalCategories = categories.size();
            
            // Calculate total points for validation indicator
            int totalPoints = 0;
            for (String pointsData : categories.values()) {
                String[] parts = pointsData.split(":");
                if (parts.length >= 1) {
                    try {
                        totalPoints += Integer.parseInt(parts[0]);
                    } catch (NumberFormatException e) {
                        // Skip invalid entries
                    }
                }
            }
            
            String validationIcon = (totalPoints == 100) ? " ✓" : " ✗";
            model.addElement(courseName + " (" + totalCategories + " kategorija, " + totalPoints + " bodova)" + validationIcon);
        }
    }
    
    private void openCategoryManagementDialog(JDialog parentDialog, String courseName, 
                                            java.util.Map<String, String> categories, Runnable updateCallback) {
        JDialog categoryDialog = new JDialog(parentDialog, "Kategorije za: " + courseName, true);
        categoryDialog.setLayout(new BorderLayout());
        
        // Category list
        DefaultListModel<String> categoryListModel = new DefaultListModel<>();
        JList<String> categoryList = new JList<>(categoryListModel);
        JScrollPane categoryScrollPane = new JScrollPane(categoryList);
        categoryScrollPane.setPreferredSize(new Dimension(300, 150));
        
        // Update category list
        updateCategoryList(categoryListModel, categories);
        
        // Summary panel
        JPanel summaryPanel = new JPanel(new FlowLayout());
        JLabel totalPointsLabel = new JLabel();
        updateTotalPointsLabel(totalPointsLabel, categories);
        summaryPanel.add(totalPointsLabel);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton dodajKategoriju = new JButton("Dodaj kategoriju");
        JButton ukloniKategoriju = new JButton("Ukloni kategoriju");
        
        // Add category action
        dodajKategoriju.addActionListener(e -> {
            JPanel inputPanel = new JPanel(new GridLayout(3, 2));
            JTextField categoryField = new JTextField();
            JTextField pointsField = new JTextField();
            JTextField minPointsField = new JTextField();
            
            inputPanel.add(new JLabel("Naziv kategorije:"));
            inputPanel.add(categoryField);
            inputPanel.add(new JLabel("Bodovi:"));
            inputPanel.add(pointsField);
            inputPanel.add(new JLabel("Minimalni bodovi:"));
            inputPanel.add(minPointsField);
            
            int result = JOptionPane.showConfirmDialog(categoryDialog, inputPanel, 
                "Dodaj kategoriju", JOptionPane.OK_CANCEL_OPTION);
            
            if (result == JOptionPane.OK_OPTION) {
                String categoryName = categoryField.getText().trim();
                String pointsStr = pointsField.getText().trim();
                String minPointsStr = minPointsField.getText().trim();

                if (!categoryName.isEmpty() && !pointsStr.isEmpty() && !minPointsStr.isEmpty()) {
                    try {
                        int points = Integer.parseInt(pointsStr);
                        int minPoints = Integer.parseInt(minPointsStr);
                        if (points >= 0 && minPoints >= 0) {
                            // Store both points and minimum points as "points:minPoints"
                            categories.put(categoryName, points + ":" + minPoints);
                            updateCategoryList(categoryListModel, categories);
                            updateTotalPointsLabel(totalPointsLabel, categories);
                            updateCallback.run();
                            // Auto-save course configurations
                            saveCourseConfiguration(courseName, categories);
                        } else {
                            JOptionPane.showMessageDialog(categoryDialog, "Bodovi moraju biti pozitivni broj!", "Greška", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(categoryDialog, "Bodovi moraju biti broj!", "Greška", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(categoryDialog, "Sva polja moraju biti popunjena!", "Greška", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // Remove category action
        ukloniKategoriju.addActionListener(e -> {
            String selectedCategory = categoryList.getSelectedValue();
            if (selectedCategory != null) {
                String categoryName = selectedCategory.split(" \\(")[0]; // Extract category name
                categories.remove(categoryName);
                updateCategoryList(categoryListModel, categories);
                updateTotalPointsLabel(totalPointsLabel, categories);
                updateCallback.run();
                // Auto-save course configurations
                saveCourseConfiguration(courseName, categories);
            } else {
                JOptionPane.showMessageDialog(categoryDialog, "Molimo izaberite kategoriju za uklanjanje!", "Greška", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        buttonPanel.add(dodajKategoriju);
        buttonPanel.add(ukloniKategoriju);
        
        // Close button
        JButton zatvoriButton = new JButton("Zatvori");
        zatvoriButton.addActionListener(e -> {
            int totalPoints = calculateTotalPoints(categories);
            if (totalPoints != 100) {
                JOptionPane.showMessageDialog(categoryDialog, 
                    """
                    Gre\u0161ka: Ukupan broj bodova mora biti 100!
                    Trenutno ukupno: """ + totalPoints + " bodova", 
                    "Validacija bodova", JOptionPane.ERROR_MESSAGE);
                return; // Don't close the dialog
            }
            categoryDialog.dispose();
        });
        buttonPanel.add(zatvoriButton);

        categoryDialog.add(new JLabel("Kategorije za predmet: " + courseName), BorderLayout.NORTH);
        
        // Create a panel to hold both the category list and summary
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(categoryScrollPane, BorderLayout.CENTER);
        centerPanel.add(summaryPanel, BorderLayout.SOUTH);
        categoryDialog.add(centerPanel, BorderLayout.CENTER);
        
        categoryDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        categoryDialog.setSize(400, 300);
        categoryDialog.setLocationRelativeTo(parentDialog);
        categoryDialog.setVisible(true);
    }
    
    private void updateCategoryList(DefaultListModel<String> model, java.util.Map<String, String> categories) {
        model.clear();
        for (java.util.Map.Entry<String, String> entry : categories.entrySet()) {
            String categoryName = entry.getKey();
            String[] pointsData = entry.getValue().split(":");
            if (pointsData.length == 2) {
                String points = pointsData[0];
                String minPoints = pointsData[1];
                model.addElement(categoryName + " (" + points + " bodova, min: " + minPoints + ")");
            } else {
                // Fallback for old format
                model.addElement(categoryName + " (" + entry.getValue() + " bodova)");
            }
        }
    }
    
    private int calculateTotalPoints(java.util.Map<String, String> categories) {
        int total = 0;
        for (String pointsData : categories.values()) {
            String[] parts = pointsData.split(":");
            if (parts.length >= 1) {
                try {
                    total += Integer.parseInt(parts[0]);
                } catch (NumberFormatException e) {
                    // Skip invalid entries
                }
            }
        }
        return total;
    }
    
    private void updateTotalPointsLabel(JLabel label, java.util.Map<String, String> categories) {
        int total = calculateTotalPoints(categories);
        String text = "Ukupno bodova: " + total + "/100";
        if (total == 100) {
            text += " ✓";
        } else {
            text += " ✗";
        }
        label.setText(text);
    }
    
    private void saveCourseConfiguration(String courseName, java.util.Map<String, String> categories) {
        // This method will be called when a course configuration is updated
        try {
            // Load current configurations
            java.util.Map<String, java.util.Map<String, String>> allCourses = ClientDataManager.loadCourseConfigurations();
            // Update the specific course
            allCourses.put(courseName, new java.util.HashMap<>(categories));
            // Save back to file
            ClientDataManager.saveCourseConfigurations(allCourses);
            System.out.println("Course configuration saved for: " + courseName);
        } catch (Exception e) {
            System.err.println("Error saving course configuration: " + e.getMessage());
        }
    }
    
    private void sendCoursesToServer(java.util.Map<String, java.util.Map<String, String>> coursesData) {
        try {
            for (java.util.Map.Entry<String, java.util.Map<String, String>> courseEntry : coursesData.entrySet()) {
                String courseName = courseEntry.getKey();
                java.util.Map<String, String> categories = courseEntry.getValue();
                
                // Format: ADD_COURSE:courseName:categories
                StringBuilder categoriesBuilder = new StringBuilder();
                for (java.util.Map.Entry<String, String> categoryEntry : categories.entrySet()) {
                    String categoryName = categoryEntry.getKey();
                    String pointsData = categoryEntry.getValue();
                    categoriesBuilder.append(categoryName).append("=").append(pointsData).append(",");
                }
                
                // Remove trailing comma if exists
                if (categoriesBuilder.length() > 0 && categoriesBuilder.charAt(categoriesBuilder.length() - 1) == ',') {
                    categoriesBuilder.setLength(categoriesBuilder.length() - 1);
                }
                
                String courseCommand = String.format("ADD_COURSE:%s:%s", courseName, categoriesBuilder.toString());
                
                if (out != null) {
                    out.println(courseCommand);
                    out.flush();
                    System.out.println("Sent course to server: " + courseCommand);
                    
                    // Wait for server response
                    if (in != null) {
                        String response = in.readLine();
                        if ("COURSE_ADDED_SUCCESS".equals(response)) {
                            System.out.println("Course successfully added to server: " + courseName);
                        } else if (response.startsWith("ERROR")) {
                            System.err.println("Error adding course to server: " + response);
                            // Show error to user for course-specific issues
                            if (!response.contains("already exists")) {
                                JOptionPane.showMessageDialog(null, 
                                    "Greška pri dodavanju predmeta '" + courseName + "': " + response, 
                                    "Greška", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("Error sending courses to server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private String formatCoursesData(java.util.Map<String, java.util.Map<String, String>> coursesData) {
        StringBuilder sb = new StringBuilder();
        for (java.util.Map.Entry<String, java.util.Map<String, String>> courseEntry : coursesData.entrySet()) {
            String courseName = courseEntry.getKey();
            java.util.Map<String, String> categories = courseEntry.getValue();
            
            sb.append(courseName).append(":");
            for (java.util.Map.Entry<String, String> categoryEntry : categories.entrySet()) {
                String categoryName = categoryEntry.getKey();
                String pointsData = categoryEntry.getValue();
                // Extract just the points part for compatibility
                String points = pointsData.contains(":") ? pointsData.split(":")[0] : pointsData;
                sb.append(categoryName).append("=").append(points).append(",");
            }
            // Remove trailing comma if exists
            if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ',') {
                sb.setLength(sb.length() - 1);
            }
            sb.append(";");
        }
        // Remove trailing semicolon if exists
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ';') {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }
}