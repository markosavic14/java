import javax.swing.*;

public class loginWindow {
    private String username;
    private String password;
    private String role;
    private boolean loggedIn = false;

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public String getRole(){
        return role;
    }

    public boolean isLoggedIn(){
        return loggedIn;
    }

    public loginWindow() {
        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(20, 20, 80, 25);
        frame.add(userLabel);

        JTextField userText = new JTextField();
        userText.setBounds(110, 20, 150, 25);
        frame.add(userText);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(20, 60, 80, 25);
        frame.add(passLabel);

        JPasswordField passText = new JPasswordField();
        passText.setBounds(110, 60, 150, 25);
        frame.add(passText);

        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setBounds(20, 100, 80, 25);
        frame.add(roleLabel);

        String[] roles = {"admin", "student"};

        JComboBox<String> roleComboBox = new JComboBox<>(roles);
        roleComboBox.setBounds(110, 100, 150, 25);
        frame.add(roleComboBox);

        // Add Login button
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(110, 140, 150, 25);
        frame.add(loginButton);

        // Save username, password, and role on login button click
        loginButton.addActionListener(e -> {
            username = userText.getText();
            password = new String(passText.getPassword());
            role = (String) roleComboBox.getSelectedItem();
            loggedIn = true;
            frame.dispose(); // Close the login window
        });

        // Show the frame
        frame.setVisible(true);
    }
}
