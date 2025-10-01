import javax.swing.*;

import java.awt.*;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;

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
        dodajStudentaButton.addActionListener(e -> openAddStudentDialog());
        studentiButtonPanel.add(dodajStudentaButton);
        
        // Add refresh button to the students panel
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            // Placeholder for refresh logic
            if (out != null) {
                out.println("GET_STUDENTS");
                out.flush();
            }
            try {
                if (in != null) {
                    String studentsList = in.readLine();
                    System.out.println("Refreshed students from server: " + studentsList);
                    if (studentsList != null) {
                        students = new ArrayList<>();
                        for (String student : studentsList.split(",")) {
                            students.add(student.trim());
                        }
                        // Update the studentiContentPanel with the new list
                        studentiContentPanel.removeAll();
                        studentiContentPanel.add(new JLabel("Studenti panel"));
                        for (String student : students) {
                            studentiContentPanel.add(new JLabel(student));
                        }
                        studentiContentPanel.revalidate();
                        studentiContentPanel.repaint();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        studentiButtonPanel.add(refreshButton);
        
        // Add components to the main students panel
        studentiPanel.add(studentiContentPanel, BorderLayout.CENTER);
        studentiPanel.add(studentiButtonPanel, BorderLayout.SOUTH);

        JPanel predmetiPanel = new JPanel();
        predmetiPanel.add(new JLabel("Predmeti panel"));

        tabbedPane.addTab("Studenti", studentiPanel);
        tabbedPane.addTab("Predmeti", predmetiPanel);

        frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
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
        
        // Replace text area with button and display field for courses
        JTextField predmetiOceneDisplayField = new JTextField(20);
        predmetiOceneDisplayField.setEditable(false);
        predmetiOceneDisplayField.setText("Kliknite 'Upravljaj' da dodate predmete");
        JButton upravljajPredmetimaButton = new JButton("Upravljaj");
        
        // Store courses data
        final StringBuilder predmetiOceneData = new StringBuilder();
        
        upravljajPredmetimaButton.addActionListener(ev -> {
            String coursesData = openCourseManagementDialog(dialog);
            if (coursesData != null && !coursesData.isEmpty()) {
                predmetiOceneData.setLength(0);
                predmetiOceneData.append(coursesData);
                predmetiOceneDisplayField.setText("Predmeti dodati (" + coursesData.split(";").length + " predmeta)");
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
        JPanel predmetiPanel = new JPanel(new BorderLayout());
        predmetiPanel.add(predmetiOceneDisplayField, BorderLayout.CENTER);
        predmetiPanel.add(upravljajPredmetimaButton, BorderLayout.EAST);
        dialog.add(predmetiPanel, gbc);
        
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
        
        dialog.setSize(400, 380);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
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
        
        courseButtonPanel.add(dodajPredmetButton);
        courseButtonPanel.add(ukloniPredmetButton);
        courseButtonPanel.add(upravljajKategorijama);
        
        // Store course data with categories
        java.util.Map<String, java.util.Map<String, Integer>> coursesData = new java.util.HashMap<>();
        
        // Add course button action
        dodajPredmetButton.addActionListener(e -> {
            String courseName = JOptionPane.showInputDialog(courseDialog, "Unesite naziv predmeta:");
            if (courseName != null && !courseName.trim().isEmpty()) {
                courseName = courseName.trim();
                if (!coursesData.containsKey(courseName)) {
                    coursesData.put(courseName, new java.util.HashMap<>());
                    updateCourseList(courseListModel, coursesData);
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
                coursesData.remove(courseName);
                updateCourseList(courseListModel, coursesData);
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
        
        // Control buttons
        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Otkaži");
        
        final String[] result = {null};
        
        okButton.addActionListener(e -> {
            result[0] = formatCoursesData(coursesData);
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
    
    private void updateCourseList(DefaultListModel<String> model, java.util.Map<String, java.util.Map<String, Integer>> coursesData) {
        model.clear();
        for (java.util.Map.Entry<String, java.util.Map<String, Integer>> entry : coursesData.entrySet()) {
            String courseName = entry.getKey();
            java.util.Map<String, Integer> categories = entry.getValue();
            int totalCategories = categories.size();
            model.addElement(courseName + " (" + totalCategories + " kategorija)");
        }
    }
    
    private void openCategoryManagementDialog(JDialog parentDialog, String courseName, 
                                            java.util.Map<String, Integer> categories, Runnable updateCallback) {
        JDialog categoryDialog = new JDialog(parentDialog, "Kategorije za: " + courseName, true);
        categoryDialog.setLayout(new BorderLayout());
        
        // Category list
        DefaultListModel<String> categoryListModel = new DefaultListModel<>();
        JList<String> categoryList = new JList<>(categoryListModel);
        JScrollPane categoryScrollPane = new JScrollPane(categoryList);
        categoryScrollPane.setPreferredSize(new Dimension(300, 150));
        
        // Update category list
        updateCategoryList(categoryListModel, categories);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton dodajKategoriju = new JButton("Dodaj kategoriju");
        JButton ukloniKategoriju = new JButton("Ukloni kategoriju");
        
        // Add category action
        dodajKategoriju.addActionListener(e -> {
            JPanel inputPanel = new JPanel(new GridLayout(2, 2));
            JTextField categoryField = new JTextField();
            JTextField pointsField = new JTextField();
            
            inputPanel.add(new JLabel("Naziv kategorije:"));
            inputPanel.add(categoryField);
            inputPanel.add(new JLabel("Bodovi:"));
            inputPanel.add(pointsField);
            
            int result = JOptionPane.showConfirmDialog(categoryDialog, inputPanel, 
                "Dodaj kategoriju", JOptionPane.OK_CANCEL_OPTION);
            
            if (result == JOptionPane.OK_OPTION) {
                String categoryName = categoryField.getText().trim();
                String pointsStr = pointsField.getText().trim();
                
                if (!categoryName.isEmpty() && !pointsStr.isEmpty()) {
                    try {
                        int points = Integer.parseInt(pointsStr);
                        if (points >= 0) {
                            categories.put(categoryName, points);
                            updateCategoryList(categoryListModel, categories);
                            updateCallback.run();
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
                updateCallback.run();
            } else {
                JOptionPane.showMessageDialog(categoryDialog, "Molimo izaberite kategoriju za uklanjanje!", "Greška", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        buttonPanel.add(dodajKategoriju);
        buttonPanel.add(ukloniKategoriju);
        
        // Close button
        JPanel closePanel = new JPanel(new FlowLayout());
        JButton zatvoriButton = new JButton("Zatvori");
        zatvoriButton.addActionListener(e -> categoryDialog.dispose());
        closePanel.add(zatvoriButton);
        
        categoryDialog.add(new JLabel("Kategorije za predmet: " + courseName), BorderLayout.NORTH);
        categoryDialog.add(categoryScrollPane, BorderLayout.CENTER);
        categoryDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(closePanel, BorderLayout.SOUTH);
        categoryDialog.add(bottomPanel, BorderLayout.SOUTH);
        
        categoryDialog.setSize(400, 300);
        categoryDialog.setLocationRelativeTo(parentDialog);
        categoryDialog.setVisible(true);
    }
    
    private void updateCategoryList(DefaultListModel<String> model, java.util.Map<String, Integer> categories) {
        model.clear();
        for (java.util.Map.Entry<String, Integer> entry : categories.entrySet()) {
            model.addElement(entry.getKey() + " (" + entry.getValue() + " bodova)");
        }
    }
    
    private String formatCoursesData(java.util.Map<String, java.util.Map<String, Integer>> coursesData) {
        StringBuilder sb = new StringBuilder();
        for (java.util.Map.Entry<String, java.util.Map<String, Integer>> courseEntry : coursesData.entrySet()) {
            String courseName = courseEntry.getKey();
            java.util.Map<String, Integer> categories = courseEntry.getValue();
            
            sb.append(courseName).append(":");
            for (java.util.Map.Entry<String, Integer> categoryEntry : categories.entrySet()) {
                sb.append(categoryEntry.getKey()).append("=").append(categoryEntry.getValue()).append(",");
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
